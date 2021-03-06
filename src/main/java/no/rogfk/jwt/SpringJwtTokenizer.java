package no.rogfk.jwt;

import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import no.rogfk.jwt.claims.Claim;
import no.rogfk.jwt.claims.validators.ClaimValidator;
import no.rogfk.jwt.exceptions.ClaimValidatorException;
import no.rogfk.jwt.exceptions.InvalidTokenException;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;

import javax.inject.Provider;
import java.beans.PropertyDescriptor;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.isEmpty;

public class SpringJwtTokenizer {

    private static final Logger log = LoggerFactory.getLogger(SpringJwtTokenizer.class);

    @Autowired
    @Qualifier("jwtEncryptor")
    private StringEncryptor encryptor;

    @Autowired
    private Provider<JwtBuilder> jwtBuilder;

    @Autowired
    private Provider<JwtParser> jwtParser;

    private Map<String, List<ClaimValidator>> claimValidators;

    private Map<String, String> standardClaims;

    SpringJwtTokenizer() {
    }

    public SpringJwtTokenizer(Collection<ClaimValidator> validators, Collection<Claim> standardClaims) {
        Map<String, List<ClaimValidator>> validatorMap = new HashMap<>();
        for (ClaimValidator validator : validators) {
            String name = validator.name();
            if (validatorMap.containsKey(name)) {
                validatorMap.get(name).add(validator);
            } else {
                List<ClaimValidator> validatorList = new ArrayList<>();
                validatorList.add(validator);
                validatorMap.put(name, validatorList);
            }
        }

        this.claimValidators = Collections.unmodifiableMap(validatorMap);
        this.standardClaims = standardClaims.stream().collect(Collectors.toMap(Claim::getName, Claim::getValue));
    }

    public Map<String, List<ClaimValidator>> getClaimValidators() {
        return claimValidators;
    }

    public String createWithUrl(String url, String queryParam, Object o) {
        String token = create(o);
        return url + "?" + queryParam + "=" + token;
    }

    public String createWithUrl(String url, Object o) {
        String token = create(o);
        String queryParam = StringUtils.uncapitalize(o.getClass().getSimpleName());
        return url + "?" + queryParam + "=" + token;
    }

    public String create(Object o) {
        BeanWrapperImpl impl = new BeanWrapperImpl(o);
        PropertyDescriptor[] descriptors = impl.getPropertyDescriptors();
        Set<Claim> claims = new HashSet<>();
        for (PropertyDescriptor descriptor : descriptors) {
            String name = descriptor.getName();
            Object value = impl.getPropertyValue(name);
            if (name != null && !"class".equals(name) && value != null) {
                claims.add(new Claim(name, value.toString()));
            }
        }

        return create(claims);
    }

    public String create(String name, String value) {
        return create(new Claim(name, value));
    }

    public String create(Claim... claims) {
        return create(new HashSet<>(Arrays.asList(claims)));
    }

    public String create(Set<Claim> claims) {
        Map<String, Object> claimMap = new HashMap<>();
        claimMap.putAll(standardClaims);
        claimMap.putAll(claims.stream().collect(Collectors.toMap(Claim::getName, Claim::getValue)));

        String token = jwtBuilder.get().setClaims(claimMap).compact();
        return encryptor.encrypt(token);
    }

    public <T> T parseWithUrl(String url, Class<T> clazz) {
        String queryParam = StringUtils.uncapitalize(clazz.getSimpleName());
        return parseWithUrl(url, queryParam, clazz);
    }

    public <T> T parseWithUrl(String url, String queryParam, Class<T> clazz) {
        queryParam += "=";
        String value = url.substring((url.lastIndexOf(queryParam) + queryParam.length()), url.length());
        return parse(value, clazz);
    }

    @SuppressWarnings("unchecked")
    public <T> T parse(String value, Class<T> clazz) {
        Set<Claim> claims = parse(value);
        BeanWrapperImpl beanWrapper = new BeanWrapperImpl(clazz);
        PropertyDescriptor[] descriptors = beanWrapper.getPropertyDescriptors();
        for (PropertyDescriptor descriptor : descriptors) {
            String name = descriptor.getName();
            Optional<Claim> claim = claims.stream().filter(c -> c.getName().equals(name)).findFirst();
            if (claim.isPresent()) {
                try {
                    beanWrapper.setPropertyValue(name, claim.get().getValue());
                } catch (NotWritablePropertyException e) {
                    log.info("Setter for property {} was not found, ignoring", name);
                }
            }
        }
        return (T) beanWrapper.getWrappedInstance();
    }

    public Optional<Claim> parse(String name, String value) {
        Set<Claim> claims = parse(value);
        return claims.stream().filter(claim -> claim.getName().equals(name)).findAny();
    }

    public Set<Claim> parse(String value) {
        try {
            String token = encryptor.decrypt(value);
            Set<Claim> claims = getClaims(token);
            claims.forEach(claim -> {
                List<ClaimValidator> validators = this.claimValidators.get(claim.getName());
                if (validators != null) {
                    validators.forEach(validator -> validateClaim(claim, validator));
                }
            });

            return claims;
        } catch (EncryptionOperationNotPossibleException e) {
            throw new InvalidTokenException("Invalid token", e);
        }
    }

    private void validateClaim(Claim claim, ClaimValidator validator) {
        boolean valid = validator.valid(claim.getValue());
        if (!valid) {
            String exceptionMessage = validator.exceptionMessage();
            if (isEmpty(exceptionMessage)) {
                exceptionMessage = "Validaton failed for " + validator.name();
            }
            throw new ClaimValidatorException(exceptionMessage);
        }
    }

    @SuppressWarnings("unchecked")
    private Set<Claim> getClaims(String token) {
        Jwt parsedValues = jwtParser.get().parse(token);
        Map<String, String> values = (Map<String, String>) parsedValues.getBody();
        return values.entrySet().stream().map(entry -> new Claim(entry.getKey(), entry.getValue())).collect(Collectors.toSet());
    }
}

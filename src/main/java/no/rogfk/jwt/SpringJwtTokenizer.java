package no.rogfk.jwt;

import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import no.rogfk.jwt.claims.Claim;
import no.rogfk.jwt.claims.validators.ClaimValidator;
import no.rogfk.jwt.exception.ClaimValidatorException;
import no.rogfk.jwt.exception.InvalidTokenException;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Provider;
import java.beans.PropertyDescriptor;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.isEmpty;

public class SpringJwtTokenizer {

    private static final Logger log = LoggerFactory.getLogger(SpringJwtTokenizer.class);

    @Autowired
    private StringEncryptor encryptor;

    @Autowired
    private Provider<JwtBuilder> jwtBuilder;

    @Autowired
    private Provider<JwtParser> jwtParser;

    private final Map<String, List<ClaimValidator>> claimValidators;

    private final Map<String, String> standardClaims;

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

    public String wrap(Object o) {
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

        return wrap(claims);
    }

    public String wrap(String name, String value) {
        return wrap(new Claim(name, value));
    }

    public String wrap(Claim... claims) {
        return wrap(new HashSet<>(Arrays.asList(claims)));
    }

    public String wrap(Set<Claim> claims) {
        Map<String, Object> claimMap = new HashMap<>();
        claimMap.putAll(standardClaims);
        claimMap.putAll(claims.stream().collect(Collectors.toMap(Claim::getName, Claim::getValue)));

        String token = jwtBuilder.get().setClaims(claimMap).compact();
        return encryptor.encrypt(token);
    }

    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> clazz, String value) {
        Set<Claim> claims = unwrap(value);
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

    public Optional<Claim> unwrap(String name, String value) {
        Set<Claim> claims = unwrap(value);
        return claims.stream().filter(claim -> claim.getName().equals(name)).findAny();
    }

    public Set<Claim> unwrap(String value) {
        try {
            String token = encryptor.decrypt(value);
            Set<Claim> claims = getClaims(token);
            claims.stream().forEach(claim -> {
                List<ClaimValidator> validators = this.claimValidators.get(claim.getName());
                if (validators != null) {
                    validators.stream().forEach(validator -> validateClaim(claim, validator));
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

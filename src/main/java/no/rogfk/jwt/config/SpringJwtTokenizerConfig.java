package no.rogfk.jwt.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.TextCodec;
import no.rogfk.jwt.SpringJwtTokenizer;
import no.rogfk.jwt.annotations.EnableJwt;
import no.rogfk.jwt.claims.Claim;
import no.rogfk.jwt.claims.validators.ClaimValidator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.AnnotationUtils;

import javax.annotation.PostConstruct;
import java.util.*;

import static org.springframework.util.StringUtils.isEmpty;

@Configuration
@EnableConfigurationProperties
@ComponentScan(basePackageClasses = SpringJwtTokenizer.class)
public class SpringJwtTokenizerConfig implements ApplicationContextAware {

    private static final Logger log = LoggerFactory.getLogger(SpringJwtTokenizerConfig.class);

    @Value("${jasypt.encryptor.algorithm:PBEWithMD5AndDES}")
    private String encryptorAlgorithm;

    @Value("${jasypt.encryptor.password:}")
    private char[] encryptorPassword;

    @Autowired
    private ClaimsConfig claimsConfig;

    private Collection<ClaimValidator> validators;

    @PostConstruct
    public void init() {
        if (claimsConfig.isEncryptionEnabled()) {
            if (encryptorPassword == null || encryptorPassword.length == 0) {
                throw new IllegalArgumentException("Missing property 'jasypt.encryptor.password'");
            } else {
                String jwtKey = claimsConfig.getKey();
                if (jwtKey == null || jwtKey.length() == 0) {
                    log.info("No JWT key set, using encryptor password as key");
                    claimsConfig.setKey(new String(encryptorPassword));
                }
            }
        } else {
            if (isEmpty(claimsConfig.getKey())) {
                throw new IllegalArgumentException("Missing property 'jwt.key'");
            } else {
                claimsConfig.setKey(TextCodec.BASE64.encode(claimsConfig.getKey()));
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, ClaimValidator> beans = applicationContext.getBeansOfType(ClaimValidator.class);
        validators = beans.values();

        Map<String, Object> annotatedBeans = applicationContext.getBeansWithAnnotation(EnableJwt.class);
        if (annotatedBeans.size() == 1) {
            Object annotatedBean = annotatedBeans.values().iterator().next();
            EnableJwt annotation = AnnotationUtils.findAnnotation(annotatedBean.getClass(), EnableJwt.class);
            setAnnotationConfig(annotation);
        } else {
            throw new IllegalStateException("Expected 1 bean with @EnableJwtTokenizer, but found " + annotatedBeans.size());
        }
    }

    private void setAnnotationConfig(EnableJwt annotation) {
        claimsConfig.setEncryptionEnabled(annotation.encryption());
        claimsConfig.setStandardValidators(annotation.standardValidators());
        String annotationIssuer = annotation.issuer();
        String issuer = claimsConfig.getIssuer();
        if (isEmpty(issuer) && !isEmpty(annotationIssuer)) {
            claimsConfig.setIssuer(annotationIssuer);
        }

        long annotationMaxAgeMs = annotation.maxAgeMinutes();
        Long maxAgeMs = claimsConfig.getMaxAgeMinutes();
        if (maxAgeMs == null && annotationMaxAgeMs > 0) {
            claimsConfig.setMaxAgeMinutes(annotationMaxAgeMs);
        }
    }

    @Bean
    public SpringJwtTokenizer springJwtTokenizer() {
        return new SpringJwtTokenizer(getClaimValidators(), getStandardClaims());
    }

    private Collection<ClaimValidator> getClaimValidators() {
        if (validators == null || validators.size() == 0) {
            log.warn("No JWT claims validators found");
            return Collections.emptyList();
        } else {
            return validators;
        }
    }

    private Collection<Claim> getStandardClaims() {
        String issuer = claimsConfig.getIssuer();
        List<Claim> standardClaims = new ArrayList<>();
        standardClaims.add(new Claim(Claims.ISSUED_AT, System.currentTimeMillis()));
        if (!isEmpty(issuer)) {
            standardClaims.add(new Claim(Claims.ISSUER, issuer));
        }
        return standardClaims;
    }

    @Bean
    public StringEncryptor stringEncryptor() {
        if (claimsConfig.isEncryptionEnabled()) {
            StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
            encryptor.setProvider(new BouncyCastleProvider());
            encryptor.setAlgorithm(encryptorAlgorithm);
            encryptor.setPasswordCharArray(encryptorPassword);
            for (int i = 0; i < encryptorPassword.length; i++) {
                encryptorPassword[i] = ' ';
            }
            return encryptor;
        } else {
            return new StringEncryptor() {
                @Override
                public String encrypt(String message) {
                    return message;
                }

                @Override
                public String decrypt(String encryptedMessage) {
                    return encryptedMessage;
                }
            };
        }
    }

    @Bean
    @Scope("prototype")
    public JwtBuilder jwtBuilder() {
        return Jwts.builder().signWith(SignatureAlgorithm.HS256, claimsConfig.getKey());
    }

    @Bean
    @Scope("prototype")
    public JwtParser jwtParser() {
        return Jwts.parser().setSigningKey(claimsConfig.getKey());
    }
}

package no.rogfk.jwt.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
public class ClaimsConfig {

    private boolean encryption = false;

    private boolean standardValidators = true;

    private String key;

    private String issuer;

    private Long maxAgeMinutes;

    public boolean isEncryption() {
        return encryption;
    }

    public void setEncryption(boolean encryption) {
        this.encryption = encryption;
    }

    public boolean isStandardValidators() {
        return standardValidators;
    }

    public void setStandardValidators(boolean standardValidators) {
        this.standardValidators = standardValidators;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public Long getMaxAgeMinutes() {
        return maxAgeMinutes;
    }

    public void setMaxAgeMinutes(Long maxAgeMinutes) {
        this.maxAgeMinutes = maxAgeMinutes;
    }
}

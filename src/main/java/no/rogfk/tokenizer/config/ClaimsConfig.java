package no.rogfk.tokenizer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
public class ClaimsConfig {

    private boolean encryptionEnabled = true;

    private boolean standardValidators = true;

    private String key;

    private String issuer;

    private Long maxAgeMinutes;

    public boolean isEncryptionEnabled() {
        return encryptionEnabled;
    }

    public void setEncryptionEnabled(boolean encryptionEnabled) {
        this.encryptionEnabled = encryptionEnabled;
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

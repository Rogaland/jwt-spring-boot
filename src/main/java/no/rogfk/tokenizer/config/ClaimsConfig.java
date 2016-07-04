package no.rogfk.tokenizer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class ClaimsConfig {

    private boolean encryptionEnabled = true;

    private boolean standardValidators = true;

    private String key;

    private String issuer;

    private Long maxAgeMinutes;
}

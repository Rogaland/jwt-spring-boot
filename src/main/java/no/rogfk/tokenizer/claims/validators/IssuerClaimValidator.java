package no.rogfk.tokenizer.claims.validators;

import io.jsonwebtoken.Claims;
import no.rogfk.tokenizer.config.ClaimsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.springframework.util.StringUtils.isEmpty;

@Component
public class IssuerClaimValidator implements ClaimValidator {

    private static final Logger log = LoggerFactory.getLogger(IssuerClaimValidator.class);

    @Autowired
    private ClaimsConfig claimsConfig;

    @Override
    public String name() {
        return Claims.ISSUER;
    }

    @Override
    public boolean valid(String value) {
        if (configValuesNotSet()) {
            return true;
        }

        String issuer = claimsConfig.getIssuer();
        if (issuer.equals(value)) {
            return true;
        } else {
            log.info("Validation failed for value: {}", value);
            return false;
        }
    }

    private boolean configValuesNotSet() {
        return (!claimsConfig.isStandardValidators() || isEmpty(claimsConfig.getIssuer()));
    }

    @Override
    public String exceptionMessage() {
        return String.format("Invalid issuer, expected %s", claimsConfig.getIssuer());
    }

}

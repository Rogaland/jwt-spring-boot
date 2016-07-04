package no.rogfk.tokenizer.claims.validators;

import io.jsonwebtoken.Claims;
import no.rogfk.tokenizer.config.ClaimsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class IssuedAtClaimValidator implements ClaimValidator {

    private static final Logger log = LoggerFactory.getLogger(IssuedAtClaimValidator.class);

    @Autowired
    private ClaimsConfig claimsConfig;

    @Override
    public String name() {
        return Claims.ISSUED_AT;
    }

    @Override
    public boolean valid(String value) {
        if (configValuesNotSet()) {
            return true;
        }

        long timestamp = Long.valueOf(value);
        long now = System.currentTimeMillis();
        long diff = ((now - timestamp) / 1000) / 60;
        if ((diff < claimsConfig.getMaxAgeMinutes())) {
            return true;
        } else {
            log.info("Token has expired, initial timestamp:{} diff:{}", timestamp, diff);
            return false;
        }
    }

    private boolean configValuesNotSet() {
        return (!claimsConfig.isStandardValidators() || claimsConfig.getMaxAgeMinutes() == null || claimsConfig.getMaxAgeMinutes() <= 0);
    }

    @Override
    public String exceptionMessage() {
        return "The token has expired";
    }
}

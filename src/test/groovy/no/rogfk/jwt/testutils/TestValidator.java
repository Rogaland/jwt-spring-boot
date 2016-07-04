package no.rogfk.jwt.testutils;

import no.rogfk.jwt.claims.validators.ClaimValidator;
import org.springframework.stereotype.Component;

@Component
public class TestValidator implements ClaimValidator {

    @Override
    public String name() {
        return "test-claim";
    }

    @Override
    public boolean valid(String value) {
        return !value.equals("fail");
    }

    @Override
    public String exceptionMessage() {
        return "Test exception";
    }
}

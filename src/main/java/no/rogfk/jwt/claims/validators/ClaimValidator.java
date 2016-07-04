package no.rogfk.jwt.claims.validators;

public interface ClaimValidator {
    String name();

    boolean valid(String value);

    default String exceptionMessage() {
        return "";
    }
}

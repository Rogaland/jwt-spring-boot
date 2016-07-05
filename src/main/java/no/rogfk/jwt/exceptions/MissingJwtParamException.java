package no.rogfk.jwt.exceptions;

public class MissingJwtParamException extends RuntimeException {

    public MissingJwtParamException(String message) {
        super(message);
    }

}

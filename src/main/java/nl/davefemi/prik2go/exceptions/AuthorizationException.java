package nl.davefemi.prik2go.exceptions;

public class AuthorizationException extends RuntimeException {
    public AuthorizationException(){
        super();
    }
    public AuthorizationException(String message) {
        super(message);
    }
}

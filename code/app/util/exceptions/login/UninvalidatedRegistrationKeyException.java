package util.exceptions.login;

public class UninvalidatedRegistrationKeyException extends Exception {

    public UninvalidatedRegistrationKeyException() {
        super();
    }

    public UninvalidatedRegistrationKeyException(Throwable cause) {
        super(cause);
    }

    public UninvalidatedRegistrationKeyException(String message, Throwable cause) {
        super(message, cause);
    }
}

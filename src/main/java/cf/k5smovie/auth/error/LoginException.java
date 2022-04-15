package cf.k5smovie.auth.error;

public class LoginException extends RuntimeException {
    public LoginException(String message) {
        super(message);
    }
}

package cf.k5smovie.auth.error;

public class ApiNotRespondException extends RuntimeException{
    public ApiNotRespondException(String message) {
        super(message);
    }
}

package ma.ensa.resumeservice.exceptions;

public class ApiLayerParseException extends Exception{
    public ApiLayerParseException(String message) {
        super(message);
    }

    public ApiLayerParseException(String message, Throwable cause) {
        super(message, cause);
    }
}

package ma.ensa.resumeservice.exceptions;

public class ApiLayerServiceException extends Exception{
    public ApiLayerServiceException(String message) {
        super(message);
    }

    public ApiLayerServiceException(String message, Throwable cause) {
        super(message, cause);
    }

}

package compilamos.manana.partygame.api.exception;

import org.springframework.http.HttpStatus;

public class ApiException extends HttpRuntimeException {
    private final ErrorCode errorCode;

    public ApiException(ErrorCode errorCode, String message, HttpStatus statusCode) {
        super(message, statusCode);
        this.errorCode = errorCode;
    }

    public ApiException(ErrorCode errorCode, String message) {
        this(errorCode, message, HttpStatus.BAD_REQUEST);
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}

package compilamos.manana.partygame.api.exception;

import org.springframework.http.HttpStatus;

/**
 * Clase base abstracta para excepciones HTTP.
 * Extiende RuntimeException para ser compatible con @ExceptionHandler.
 */
public abstract class HttpRuntimeException extends RuntimeException implements HttpException {
    protected final HttpStatus statusCode;

    public HttpRuntimeException(String message, HttpStatus statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public HttpRuntimeException(String message, HttpStatus statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    @Override
    public HttpStatus getStatusCode() {
        return statusCode;
    }
}

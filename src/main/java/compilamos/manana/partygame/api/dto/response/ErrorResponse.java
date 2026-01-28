package compilamos.manana.partygame.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;

/**
 * Respuesta estándar para errores en la API.
 */
public class ErrorResponse {
    private String message;

    @JsonProperty("error_code")
    private String errorCode;

    @JsonProperty("status_code")
    private int statusCode;

    public ErrorResponse(String message, String errorCode, HttpStatus statusCode) {
        this.message = message;
        this.errorCode = errorCode;
        this.statusCode = statusCode.value();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}

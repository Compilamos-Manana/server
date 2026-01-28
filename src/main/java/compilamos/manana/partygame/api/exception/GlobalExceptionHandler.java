package compilamos.manana.partygame.api.exception;

import compilamos.manana.partygame.api.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Manejador global de excepciones para la API.
 * Procesa todas las excepciones no controladas y devuelve una respuesta estándar.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja excepciones que extienden HttpRuntimeException.
     * Retorna el status code y error code específico definido en la excepción.
     *
     * @param ex la excepción que extiende HttpRuntimeException
     * @return ResponseEntity con el ErrorResponse y status code apropiado
     */
    @ExceptionHandler(HttpRuntimeException.class)
    public ResponseEntity<ErrorResponse> handleHttpException(HttpRuntimeException ex) {
        // Si es una ApiException, podemos obtener más información
        if (ex instanceof ApiException apiException) {
            ErrorResponse errorResponse = new ErrorResponse(
                    apiException.getMessage(),
                    apiException.getErrorCode().toString(),
                    apiException.getStatusCode()
            );
            return ResponseEntity
                    .status(apiException.getStatusCode())
                    .body(errorResponse);
        }

        // Para otras excepciones que extiendan HttpRuntimeException
        ErrorResponse errorResponse = new ErrorResponse(
                "An error occurred",
                "UNKNOWN_ERROR",
                ex.getStatusCode()
        );
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(errorResponse);
    }

    /**
     * Maneja cualquier otra excepción no controlada.
     * Retorna status 500 para excepciones que no implementan HttpException.
     *
     * @param ex la excepción
     * @return ResponseEntity con el ErrorResponse y status 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred",
                "INTERNAL_SERVER_ERROR",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }
}

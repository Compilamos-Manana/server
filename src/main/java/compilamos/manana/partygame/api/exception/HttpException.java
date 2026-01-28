package compilamos.manana.partygame.api.exception;

import org.springframework.http.HttpStatus;

/**
 * Interfaz que define el contrato para excepciones HTTP.
 * Las excepciones que implementen esta interfaz pueden proporcionar un código de estado HTTP.
 */
public interface HttpException {
    /**
     * Retorna el código de estado HTTP asociado a esta excepción.
     *
     * @return código de estado HTTP (e.g., BAD_REQUEST, NOT_FOUND, INTERNAL_SERVER_ERROR)
     */
    HttpStatus getStatusCode();
}

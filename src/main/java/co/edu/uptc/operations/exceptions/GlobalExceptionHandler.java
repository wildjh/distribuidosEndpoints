package co.edu.uptc.operations.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import co.edu.uptc.operations.persons.InvalidPaginationException;
import co.edu.uptc.operations.persons.PersonsCsvNotFoundException;
import co.edu.uptc.operations.persons.PersonsCsvReadException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ErrorLogService errorLogService;

    public GlobalExceptionHandler(ErrorLogService errorLogService) {
        this.errorLogService = errorLogService;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message;
        if ("option".equals(ex.getName())) {
            message = "La opción debe ser un número entero (1, 2, 3 o 4), pero ingresaste: " + ex.getValue();
        } else {
            message = "Error: " + ex.getName() + " ha recibido un valor no válido: " + ex.getValue() + "'.";
        }
        errorLogService.logParameterError(
                ex.getName(),
                ex.getValue(),
                message,
                ex.getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> handleMissingParameter(MissingServletRequestParameterException ex) {
        String message = "Falta el parámetro obligatorio: " + ex.getParameterName();
        errorLogService.logParameterError(
                ex.getParameterName(),
                null,
                message,
                ex.getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }

    @ExceptionHandler(InvalidPaginationException.class)
    public ResponseEntity<String> handleInvalidPagination(InvalidPaginationException ex) {
        errorLogService.logParameterError(
                ex.getParameter(),
                ex.getValue(),
                ex.getMessage(),
                ex.getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(PersonsCsvNotFoundException.class)
    public ResponseEntity<String> handleCsvNotFound(PersonsCsvNotFoundException ex) {
        errorLogService.logParameterError(
                "personsCsv",
                null,
                ex.getMessage(),
                ex.getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(PersonsCsvReadException.class)
    public ResponseEntity<String> handleCsvRead(PersonsCsvReadException ex) {
        errorLogService.logParameterError(
                "personsCsv",
                null,
                ex.getMessage(),
                ex.getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidOptionException.class)
    public ResponseEntity<String> handleCustomInvalidOption(InvalidOptionException ex) {
        errorLogService.logParameterError(
                "option",
                ex.getValorIngresado(),
                ex.getMessage(),
                ex.getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(ByZeroException.class)
    public ResponseEntity<String> byZero(ByZeroException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}

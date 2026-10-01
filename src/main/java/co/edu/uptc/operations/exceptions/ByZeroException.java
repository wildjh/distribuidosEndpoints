package co.edu.uptc.operations.exceptions;

public class ByZeroException extends RuntimeException {
    public ByZeroException(String message) {
        super("No se permite la división por cero: " + message);
    }
    
}

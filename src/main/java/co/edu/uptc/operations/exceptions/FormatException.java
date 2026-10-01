package co.edu.uptc.operations.exceptions;

public class FormatException extends RuntimeException {
    public FormatException(String message) {
        super(message);
    }
    
    public FormatException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}

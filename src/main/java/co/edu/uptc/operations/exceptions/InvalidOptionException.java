package co.edu.uptc.operations.exceptions;

public class InvalidOptionException extends RuntimeException {

    private final String valorIngresado;

    public InvalidOptionException(String valorIngresado) {
        super("La opción debe ser un número entero entre 1 y 4, pero ingresaste: '" + valorIngresado + "'.");
        this.valorIngresado = valorIngresado;
    }

    public String getValorIngresado() {
        return valorIngresado;
    }
}
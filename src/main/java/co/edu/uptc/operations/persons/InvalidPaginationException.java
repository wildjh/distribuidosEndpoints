package co.edu.uptc.operations.persons;

public class InvalidPaginationException extends RuntimeException {

    private final String parameter;
    private final String value;

    public InvalidPaginationException(String parameter, String value, String message) {
        super(message);
        this.parameter = parameter;
        this.value = value;
    }

    public String getParameter() {
        return parameter;
    }

    public String getValue() {
        return value;
    }
}

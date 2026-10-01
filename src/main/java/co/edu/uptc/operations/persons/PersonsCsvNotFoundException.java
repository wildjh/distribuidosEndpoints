package co.edu.uptc.operations.persons;

public class PersonsCsvNotFoundException extends RuntimeException {

    public PersonsCsvNotFoundException(String path) {
        super("No se encontró el archivo CSV de personas en: " + path);
    }
}

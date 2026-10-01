package co.edu.uptc.operations.persons;

public class Person {

    private final int id;
    private final String nombrePersona;

    public Person(int id, String nombrePersona) {
        this.id = id;
        this.nombrePersona = nombrePersona;
    }

    public int getId() {
        return id;
    }

    public String getNombrePersona() {
        return nombrePersona;
    }
}

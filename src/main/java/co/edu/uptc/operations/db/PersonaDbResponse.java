package co.edu.uptc.operations.db;

import java.util.List;

public class PersonaDbResponse {

    private final String atendidoPorContenedor;
    private final int totalEnPagina;
    private final List<Persona> registros;

    public PersonaDbResponse(String atendidoPorContenedor, List<Persona> registros) {
        this.atendidoPorContenedor = atendidoPorContenedor;
        this.totalEnPagina = registros.size();
        this.registros = registros;
    }

    public String getAtendidoPorContenedor() {
        return atendidoPorContenedor;
    }

    public int getTotalEnPagina() {
        return totalEnPagina;
    }

    public List<Persona> getRegistros() {
        return registros;
    }
}

package co.edu.uptc.operations.db;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/personas")
public class PersonaController {

    private static final int PAGE_SIZE = 50;

    private final PersonaRepository personaRepository;
    private final String containerName;

    public PersonaController(
            PersonaRepository personaRepository,
            @Value("${app.container-name:desconocido}") String containerName) {
        this.personaRepository = personaRepository;
        this.containerName = containerName;
    }

    @GetMapping
    public PersonaDbResponse obtenerPersonas() {
        List<Persona> personas = personaRepository.findAll(PageRequest.of(0, PAGE_SIZE)).getContent();
        return new PersonaDbResponse(containerName, personas);
    }
}

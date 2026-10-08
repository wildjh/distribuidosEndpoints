package co.edu.uptc.operations.db;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/personas")
public class PersonaController {

    private final PersonaRepository personaRepository;
    private final String containerName;

    public PersonaController(
            PersonaRepository personaRepository,
            @Value("${app.container-name:desconocido}") String containerName) {
        this.personaRepository = personaRepository;
        this.containerName = containerName;
    }

    @GetMapping
    public PersonaDbResponse obtenerPersonas(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "50") int size) {
        int safePage = Math.max(page, 0);
        int safeSize = size < 1 ? 50 : Math.min(size, 1000);
        List<Persona> personas = personaRepository
                .findAll(PageRequest.of(safePage, safeSize))
                .getContent();
        return new PersonaDbResponse(containerName, personas);
    }
}

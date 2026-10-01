package co.edu.uptc.operations.persons;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PersonController {

    private final PersonCsvService personCsvService;

    public PersonController(PersonCsvService personCsvService) {
        this.personCsvService = personCsvService;
    }

    /**
     * Ejemplo: GET /persons?page=1&size=100
     */
    @GetMapping("/persons")
    public PersonPageResponse getPersons(
            @RequestParam("page") int page,
            @RequestParam("size") int size) {
        return personCsvService.findPage(page, size);
    }
}

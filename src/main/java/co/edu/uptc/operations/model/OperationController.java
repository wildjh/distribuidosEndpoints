package co.edu.uptc.operations.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OperationController {

    private final String containerName;

    public OperationController(@Value("${app.container-name:local}") String containerName) {
        this.containerName = containerName;
    }

    @GetMapping("/operations")
    public OperationResponse executeOperation(
            @RequestParam("option") int option,
            @RequestParam("num1") float num1,
            @RequestParam("num2") float num2) {
        Model model = new Model(option, num1, num2);
        return new OperationResponse(containerName, model.executeCase());
    }
}

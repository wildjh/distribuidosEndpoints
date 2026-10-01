package co.edu.uptc.operations.exceptions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import co.edu.uptc.operations.OperationsApplication;
import jakarta.annotation.PostConstruct;

@Service
public class ErrorLogService {

    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final ObjectMapper objectMapper;
    private final Path logFilePath;
    private final String containerName;
    private final Object lock = new Object();

    public ErrorLogService(
            @Value("${app.error-log.path:parameter-errors.json}") String relativeLogPath,
            @Value("${app.container-name:local}") String containerName) {
        this.objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        this.logFilePath = resolveBesideJar(relativeLogPath);
        this.containerName = containerName;
    }

    @PostConstruct
    void logStartupInfo() {
        System.out.println("Log de errores (junto al JAR): " + logFilePath);
        System.out.println("Nombre de contenedor configurado: " + containerName);
    }

    public void logParameterError(String parameter, Object value, String message, String exceptionType) {
        Map<String, Object> entry = new LinkedHashMap<>();
        entry.put("timestamp", LocalDateTime.now().format(TIMESTAMP_FORMAT));
        entry.put("container", containerName);
        entry.put("parameter", parameter);
        entry.put("value", value != null ? String.valueOf(value) : null);
        entry.put("message", message);
        entry.put("exceptionType", exceptionType);

        synchronized (lock) {
            try {
                Path parent = logFilePath.getParent();
                if (parent != null) {
                    Files.createDirectories(parent);
                }
                List<Map<String, Object>> errors = readExistingErrors();
                errors.add(entry);
                objectMapper.writeValue(logFilePath.toFile(), errors);
            } catch (IOException e) {
                System.err.println("No se pudo guardar el log de error en " + logFilePath + ": " + e.getMessage());
            }
        }
    }

    private List<Map<String, Object>> readExistingErrors() throws IOException {
        if (!Files.exists(logFilePath) || Files.size(logFilePath) == 0) {
            return new ArrayList<>();
        }
        return objectMapper.readValue(logFilePath.toFile(), new TypeReference<List<Map<String, Object>>>() {});
    }

    /**
     * Carpeta del JAR (ApplicationHome soporta el fat JAR anidado de Spring Boot).
     */
    private Path resolveBesideJar(String relativeLogPath) {
        ApplicationHome home = new ApplicationHome(OperationsApplication.class);
        Path jarDir = home.getDir().toPath();
        return jarDir.resolve(relativeLogPath).toAbsolutePath().normalize();
    }
}

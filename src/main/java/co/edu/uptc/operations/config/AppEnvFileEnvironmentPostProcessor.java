package co.edu.uptc.operations.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import co.edu.uptc.operations.OperationsApplication;

/**
 * Carga app.env (o .env) desde:
 * 1) la carpeta del CSV de personas
 * 2) la carpeta del JAR
 * 3) el directorio de trabajo
 */
public class AppEnvFileEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Path envFile = findEnvFile(environment);
        if (envFile == null) {
            System.out.println("No se encontró app.env ni .env (carpeta del CSV, del JAR o user.dir).");
            return;
        }

        try {
            Map<String, Object> values = readEnvFile(envFile);
            if (values.isEmpty()) {
                System.out.println("Archivo de entorno vacío: " + envFile);
                return;
            }
            // Menor prioridad que variables reales del OS/contenedor.
            environment.getPropertySources().addLast(new MapPropertySource("appEnvFile", values));
            System.out.println("Variables cargadas desde: " + envFile.toAbsolutePath()
                    + " (" + values.size() + " claves)");
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer el archivo de entorno: " + envFile, e);
        }
    }

    private Path findEnvFile(ConfigurableEnvironment environment) {
        List<Path> candidates = new ArrayList<>();
        Path jarDir = new ApplicationHome(OperationsApplication.class).getDir().toPath();

        String csvRel = environment.getProperty("app.persons-csv.path", "personas.csv");
        Path csvPath = Paths.get(csvRel);
        if (!csvPath.isAbsolute()) {
            csvPath = jarDir.resolve(csvRel).normalize();
        }
        if (csvPath.getParent() != null) {
            candidates.add(csvPath.getParent().resolve("app.env"));
            candidates.add(csvPath.getParent().resolve(".env"));
        }

        candidates.add(jarDir.resolve("app.env"));
        candidates.add(jarDir.resolve(".env"));
        candidates.add(Paths.get("app.env").toAbsolutePath().normalize());
        candidates.add(Paths.get(".env").toAbsolutePath().normalize());

        for (Path candidate : candidates) {
            if (Files.isRegularFile(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    private Map<String, Object> readEnvFile(Path envFile) throws IOException {
        Map<String, Object> values = new LinkedHashMap<>();
        for (String rawLine : Files.readAllLines(envFile, StandardCharsets.UTF_8)) {
            String line = rawLine.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            if (line.startsWith("export ")) {
                line = line.substring(7).trim();
            }
            int eq = line.indexOf('=');
            if (eq <= 0) {
                continue;
            }
            String key = line.substring(0, eq).trim();
            String value = line.substring(eq + 1).trim();
            if ((value.startsWith("\"") && value.endsWith("\""))
                    || (value.startsWith("'") && value.endsWith("'"))) {
                value = value.substring(1, value.length() - 1);
            }
            values.put(key, value);
        }
        return values;
    }
}

package co.edu.uptc.operations.persons;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Service;

import co.edu.uptc.operations.OperationsApplication;
import jakarta.annotation.PostConstruct;

@Service
public class PersonCsvService {

    private static final int MAX_PAGE_SIZE = 10_000;

    private final Path csvPath;
    private final String containerName;
    private long totalElements = -1;

    public PersonCsvService(
            @Value("${app.persons-csv.path:personas.csv}") String relativeCsvPath,
            @Value("${app.container-name:local}") String containerName) {
        this.csvPath = resolveBesideJar(relativeCsvPath);
        this.containerName = containerName;
    }

    @PostConstruct
    void init() {
        if (!Files.exists(csvPath)) {
            System.err.println("Advertencia: no se encontró el CSV de personas en " + csvPath);
            return;
        }
        try {
            this.totalElements = countDataRows();
            System.out.println("CSV de personas cargado: " + csvPath + " (" + totalElements + " registros)");
        } catch (IOException e) {
            System.err.println("No se pudo contar registros del CSV: " + e.getMessage());
        }
    }

    public PersonPageResponse findPage(int page, int size) {
        validate(page, size);

        if (!Files.exists(csvPath)) {
            throw new PersonsCsvNotFoundException(csvPath.toString());
        }

        ensureTotalElements();

        int totalPages = totalElements == 0 ? 0 : (int) Math.ceil((double) totalElements / size);
        if (page > totalPages && totalPages > 0) {
            return new PersonPageResponse(containerName, page, size, totalElements, totalPages, List.of());
        }

        long skipRows = (long) (page - 1) * size;
        List<Person> content = new ArrayList<>(size);

        try (BufferedReader reader = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8)) {
            String header = reader.readLine();
            if (header == null) {
                return new PersonPageResponse(containerName, page, size, 0, 0, List.of());
            }

            for (long i = 0; i < skipRows; i++) {
                if (reader.readLine() == null) {
                    return new PersonPageResponse(containerName, page, size, totalElements, totalPages, List.of());
                }
            }

            String line;
            int read = 0;
            while (read < size && (line = reader.readLine()) != null) {
                content.add(parseLine(line));
                read++;
            }
        } catch (IOException e) {
            throw new PersonsCsvReadException("Error leyendo el CSV: " + e.getMessage(), e);
        }

        return new PersonPageResponse(containerName, page, size, totalElements, totalPages, content);
    }

    private void validate(int page, int size) {
        if (page < 1) {
            throw new InvalidPaginationException("page", String.valueOf(page),
                    "El parámetro page debe ser mayor o igual a 1.");
        }
        if (size < 1) {
            throw new InvalidPaginationException("size", String.valueOf(size),
                    "El parámetro size debe ser mayor o igual a 1.");
        }
        if (size > MAX_PAGE_SIZE) {
            throw new InvalidPaginationException("size", String.valueOf(size),
                    "El parámetro size no puede ser mayor a " + MAX_PAGE_SIZE + ".");
        }
    }

    private synchronized void ensureTotalElements() {
        if (totalElements >= 0) {
            return;
        }
        try {
            totalElements = countDataRows();
        } catch (IOException e) {
            throw new PersonsCsvReadException("No se pudo contar los registros del CSV: " + e.getMessage(), e);
        }
    }

    private long countDataRows() throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8)) {
            if (reader.readLine() == null) {
                return 0;
            }
            long count = 0;
            while (reader.readLine() != null) {
                count++;
            }
            return count;
        }
    }

    private Person parseLine(String line) {
        int comma = line.indexOf(',');
        if (comma < 0) {
            throw new PersonsCsvReadException("Línea CSV inválida: " + line, null);
        }
        int id = Integer.parseInt(line.substring(0, comma).trim());
        String nombre = line.substring(comma + 1).trim();
        return new Person(id, nombre);
    }

    private Path resolveBesideJar(String relativePath) {
        ApplicationHome home = new ApplicationHome(OperationsApplication.class);
        return home.getDir().toPath().resolve(relativePath).toAbsolutePath().normalize();
    }
}

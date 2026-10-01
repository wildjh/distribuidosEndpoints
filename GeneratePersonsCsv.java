import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Generador independiente de CSV con 5.000.000 de personas.
 *
 * Compilar:
 *   javac GeneratePersonsCsv.java
 *
 * Ejecutar:
 *   java GeneratePersonsCsv
 *   java GeneratePersonsCsv personas.csv
 *   java GeneratePersonsCsv personas.csv 5000000
 */
public class GeneratePersonsCsv {

    private static final int DEFAULT_TOTAL = 5_000_000;
    private static final String DEFAULT_OUTPUT = "personas.csv";

    public static void main(String[] args) throws IOException {
        String outputName = args.length > 0 ? args[0] : DEFAULT_OUTPUT;
        int total = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_TOTAL;

        Path output = Paths.get(outputName).toAbsolutePath().normalize();
        long start = System.currentTimeMillis();

        try (BufferedWriter writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8)) {
            writer.write("id,nombrePersona");
            writer.newLine();

            for (int id = 1; id <= total; id++) {
                writer.write(Integer.toString(id));
                writer.write(',');
                writer.write("persona");
                writer.write(Integer.toString(id));
                writer.newLine();

                if (id % 500_000 == 0) {
                    System.out.printf("Progreso: %,d / %,d (%.1f%%)%n", id, total, (id * 100.0) / total);
                }
            }
        }

        long elapsedMs = System.currentTimeMillis() - start;
        long sizeBytes = Files.size(output);

        System.out.println("CSV generado: " + output);
        System.out.printf("Registros: %,d%n", total);
        System.out.printf("Tamaño: %.2f MB%n", sizeBytes / (1024.0 * 1024.0));
        System.out.printf("Tiempo: %.2f segundos (%,d ms)%n", elapsedMs / 1000.0, elapsedMs);
    }
}

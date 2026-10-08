package co.edu.uptc.operations.db;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import com.zaxxer.hikari.HikariDataSource;

/**
 * DataSource propio: ignora SPRING_DATASOURCE_URL (si trae ${DB_HOST} literal, MySQL falla).
 * Usa solo DB_HOST, DB_PORT, DB_NAME, DB_USER y DB_PASSWORD del contenedor.
 */
@Configuration
@Profile("!test")
public class DataSourceConfig {

    @Bean
    @Primary
    public DataSource dataSource(
            @Value("${DB_HOST:}") String host,
            @Value("${DB_PORT:3306}") int port,
            @Value("${DB_NAME:app_db}") String database,
            @Value("${DB_USER:william}") String username,
            @Value("${DB_PASSWORD:}") String password) {

        String cleanHost = host == null ? "" : host.trim();
        if (cleanHost.isEmpty() || cleanHost.contains("$") || cleanHost.contains("{") || cleanHost.contains("}")) {
            throw new IllegalStateException(
                    "DB_HOST inválido: '" + host + "'. "
                            + "Coloca un archivo app.env junto al JAR (misma carpeta que personas.csv) con:\n"
                            + "  DB_HOST=10.39.21.244\n"
                            + "  DB_USER=william\n"
                            + "  DB_PASSWORD=tu_password\n"
                            + "O pásalas como variables de entorno del contenedor. "
                            + "Elimina SPRING_DATASOURCE_URL si contiene ${DB_HOST}.");
        }

        String jdbcUrl = String.format(
                "jdbc:mysql://%s:%d/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                cleanHost,
                port,
                database);

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setInitializationFailTimeout(-1);
        dataSource.setConnectionTimeout(10_000);

        System.out.println("=== MySQL config ===");
        System.out.println("DB_HOST=" + cleanHost);
        System.out.println("JDBC URL=" + jdbcUrl);
        System.out.println("DB_USER=" + username);
        System.out.println("====================");
        return dataSource;
    }
}

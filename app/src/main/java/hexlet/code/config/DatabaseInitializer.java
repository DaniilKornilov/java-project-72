package hexlet.code.config;

import hexlet.code.exception.DatabaseInitializationException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DatabaseInitializer {
    private static final String INIT_SQL = "/db/init.sql";

    public static void init(DataSource dataSource) {
        String sql = readInitSqlFromResource();

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new DatabaseInitializationException("Failed to execute Init SQL statement", e);
        }
    }

    private static String readInitSqlFromResource() {
        try (InputStream is = DatabaseInitializer.class.getResourceAsStream(DatabaseInitializer.INIT_SQL);
             BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(is)))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            throw new DatabaseInitializationException("Failed to read SQL file: " + DatabaseInitializer.INIT_SQL, e);
        }
    }
}

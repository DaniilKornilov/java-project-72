package hexlet.code.config;

import hexlet.code.exception.DatabaseInitializationException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
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
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            String sql = readInitSqlFromResource();
            statement.execute(sql);
        } catch (SQLException | IOException e) {
            throw new DatabaseInitializationException("Failed to execute Init SQL statement", e);
        }
    }

    private static String readInitSqlFromResource() throws IOException {
        try (InputStream is = DatabaseInitializer.class.getResourceAsStream(DatabaseInitializer.INIT_SQL);
             BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(is)))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
}

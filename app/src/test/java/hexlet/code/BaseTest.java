package hexlet.code;

import hexlet.code.config.DatabaseConfiguration;
import hexlet.code.config.DatabaseInitializer;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

public abstract class BaseTest {
    private static MockWebServer mockWebServer;
    private static DataSource dataSource;

    @BeforeAll
    static void prepareWebServer() {
        dataSource = DatabaseConfiguration.getDataSource();
        DatabaseInitializer.init(dataSource);
    }

    @AfterAll
    static void shutdownWebServer() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    final void setUp() throws SQLException, IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        clearDb(dataSource);
    }

    protected final MockWebServer getMockWebServer() {
        return mockWebServer;
    }

    protected final DataSource getDataSource() {
        return dataSource;
    }

    protected static String readResponse() throws IOException {
        Path path = Path.of("src", "test", "resources", "response.html");
        return Files.readString(path).trim();
    }

    @SuppressWarnings("SqlWithoutWhere")
    private static void clearDb(DataSource createdDataSource) throws SQLException {
        try (var connection = createdDataSource.getConnection();
             var statement = connection.prepareStatement("DELETE FROM url_checks; DELETE FROM urls")) {
            statement.executeUpdate();
        }
    }
}

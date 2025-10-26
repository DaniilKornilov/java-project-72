package hexlet.code;

import hexlet.code.config.DatabaseConfiguration;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import io.javalin.testtools.JavalinTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

final class AppTest {
    private static final String EXAMPLE_COM = "https://www.example.com";

    private static MockWebServer mockWebServer;

    private Javalin app;

    private UrlRepository urlRepository;
    private UrlCheckRepository urlCheckRepository;

    @BeforeEach
    void setUp() throws SQLException {
        app = App.getApp();
        DataSource dataSource = DatabaseConfiguration.getDataSource();
        urlRepository = new UrlRepository(dataSource);
        urlCheckRepository = new UrlCheckRepository(dataSource);
        removeData(dataSource);
    }

    @BeforeAll
    static void prepareWebServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void shutdownWebServer() throws IOException {
        mockWebServer.shutdown();
    }

    @SuppressWarnings("SqlWithoutWhere")
    private void removeData(DataSource dataSource) throws SQLException {
        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement("DELETE FROM url_checks; DELETE FROM urls")) {
            statement.executeUpdate();
        }
    }

    @Nested
    class MainPageTest {
        @Test
        void testIndex() {
            JavalinTest.test(app, (server, client) ->
                    assertThat(client.get("/").code()).isEqualTo(HttpStatus.OK.getCode()));
        }
    }

    @Nested
    class UrlPageTest {
        @Test
        void testIndex() {
            JavalinTest.test(app, (server, client) ->
                    assertThat(client.get("/urls").code()).isEqualTo(HttpStatus.OK.getCode()));
        }

        @Test
        void testShow() throws SQLException {
            urlRepository.save(EXAMPLE_COM);
            Url url = urlRepository.findByName(EXAMPLE_COM).orElseThrow();
            JavalinTest.test(app, (server, client) -> {
                var response = client.get("/urls/" + url.id());
                assertThat(response.code()).isEqualTo(HttpStatus.OK.getCode());
            });
        }

        @Test
        void testSave() throws SQLException {
            String inputUrl = EXAMPLE_COM;

            JavalinTest.test(app, (server, client) -> {
                var requestBody = "url=" + inputUrl;
                try (var response = client.post("/urls", requestBody)) {
                    assertThat(response.code()).isEqualTo(HttpStatus.OK.getCode());
                }
            });

            Url actualUrl = urlRepository.findByName(inputUrl).orElse(null);

            assertThat(actualUrl).isNotNull();
            assertThat(actualUrl.name()).isEqualTo(inputUrl);
        }
    }

    @Nested
    class UrlCheckTest {
        @Test
        void testCreateCheck() throws Exception {
            String body = readResponse();
            mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.OK.getCode()).setBody(body));

            String baseUrl = mockWebServer.url("/").toString();

            urlRepository.save(baseUrl);
            Url url = urlRepository.findByName(baseUrl).orElseThrow();

            JavalinTest.test(app, (server, client) -> {
                try (var response = client.post("/urls/" + url.id() + "/checks", "")) {
                    assertThat(response.code()).isEqualTo(HttpStatus.OK.getCode());
                }
            });

            var checks = urlCheckRepository.findByUrlId(url.id());
            assertThat(checks).isNotEmpty();
            var check = checks.getFirst();
            assertThat(check.statusCode()).isEqualTo(HttpStatus.OK.getCode());
            assertThat(check.title()).isEqualTo("Test title");
            assertThat(check.h1()).isEqualTo("Header");
            assertThat(check.description()).isEqualTo("Something");
        }
    }

    private static String readResponse() throws IOException {
        Path path = Path.of("src", "test", "resources", "response.html");
        return Files.readString(path).trim();
    }
}

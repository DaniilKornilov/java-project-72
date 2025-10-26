package hexlet.code;

import hexlet.code.config.DatabaseConfiguration;
import hexlet.code.config.JavalinConfiguration;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import io.javalin.testtools.JavalinTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

final class AppTest {
    private static final String EXAMPLE_COM = "https://www.example.com";

    private Javalin app;

    private UrlRepository urlRepository;

    @BeforeEach
    void setUp() throws SQLException {
        app = JavalinConfiguration.getApp();
        DataSource dataSource = DatabaseConfiguration.getDataSource();
        urlRepository = new UrlRepository(dataSource);
        removeData(dataSource);

    }

    @SuppressWarnings("SqlWithoutWhere")
    private void removeData(DataSource dataSource) throws SQLException {
        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement("DELETE FROM urls")) {
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
}

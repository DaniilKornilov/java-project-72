package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import io.javalin.testtools.JavalinTest;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

final class AppTest extends BaseTest {
    private static final String EXAMPLE_COM = "https://www.example.com";

    private Javalin app;

    private UrlRepository urlRepository;
    private UrlCheckRepository urlCheckRepository;

    @BeforeEach
    void prepareApp() {
        app = App.getApp();
        urlRepository = new UrlRepository(getDataSource());
        urlCheckRepository = new UrlCheckRepository(getDataSource());
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
                Response response = client.get("/urls/" + url.id());
                assertThat(response.code()).isEqualTo(HttpStatus.OK.getCode());
            });
        }

        @Test
        void testSave() throws SQLException {
            String inputUrl = EXAMPLE_COM;

            JavalinTest.test(app, (server, client) -> {
                String requestBody = "url=" + inputUrl;
                try (Response response = client.post("/urls", requestBody)) {
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
            getMockWebServer().enqueue(new MockResponse().setResponseCode(HttpStatus.OK.getCode()).setBody(body));

            String baseUrl = getMockWebServer().url("/").toString();

            urlRepository.save(baseUrl);
            Url url = urlRepository.findByName(baseUrl).orElseThrow();

            JavalinTest.test(app, (server, client) -> {
                try (Response response = client.post("/urls/" + url.id() + "/checks", "")) {
                    assertThat(response.code()).isEqualTo(HttpStatus.OK.getCode());
                }
            });

            List<UrlCheck> checks = urlCheckRepository.findByUrlId(url.id());
            assertThat(checks).isNotEmpty();
            UrlCheck check = checks.getFirst();
            assertThat(check.statusCode()).isEqualTo(HttpStatus.OK.getCode());
            assertThat(check.title()).isEqualTo("Test title");
            assertThat(check.h1()).isEqualTo("Header");
            assertThat(check.description()).isEqualTo("Something");
        }
    }
}

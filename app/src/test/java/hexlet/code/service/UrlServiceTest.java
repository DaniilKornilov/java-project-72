package hexlet.code.service;

import hexlet.code.BaseTest;
import hexlet.code.exception.InvalidUrlException;
import hexlet.code.exception.UrlAlreadyExistsException;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import io.javalin.http.HttpStatus;
import okhttp3.mockwebserver.MockResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class UrlServiceTest extends BaseTest {
    private UrlRepository urlRepository;
    private UrlCheckRepository urlCheckRepository;
    private UrlService urlService;

    @BeforeEach
    void setUp() {
        urlRepository = new UrlRepository(getDataSource());
        urlCheckRepository = new UrlCheckRepository(getDataSource());
        urlService = new UrlService(urlRepository, urlCheckRepository);
    }

    @Test
    void testCreateUrlSuccessAndFind() throws SQLException {
        urlService.createUrl("https://example.com/some/path");

        Optional<Url> maybe = urlRepository.findByName("https://example.com");
        assertThat(maybe).isPresent();
        Url url = maybe.orElseThrow();
        assertThat(url.name()).isEqualTo("https://example.com");
        assertThat(url.id()).isNotNull();

        List<Url> urls = urlRepository.findAll();
        assertThat(urls).isNotEmpty();
        assertThat(urlRepository.findById(url.id())).isPresent();
    }

    @Test
    void testCreateUrlInvalid() throws SQLException {
        assertThrows(InvalidUrlException.class, () -> urlService.createUrl("not a url"));

        List<Url> urls = urlRepository.findAll();
        assertThat(urls).isEmpty();
    }

    @Test
    void testCreateUrlAlreadyExists() throws SQLException {
        urlRepository.save("https://example.com");
        assertThrows(UrlAlreadyExistsException.class, () -> urlService.createUrl("https://example.com/some"));

        List<Url> urls = urlRepository.findAll();
        assertThat(urls).hasSize(1);
    }

    @Test
    void testCreateCheckSuccess() throws Exception {
        String response = readResponse();
        getMockWebServer().enqueue(new MockResponse().setResponseCode(HttpStatus.OK.getCode()).setBody(response));

        String baseUrl = getMockWebServer().url("/").toString();
        urlRepository.save(baseUrl);
        Url url = urlRepository.findByName(baseUrl).orElseThrow();

        urlService.createCheck(url.id());

        List<UrlCheck> checks = urlCheckRepository.findByUrlId(url.id());
        assertThat(checks).isNotEmpty();
        UrlCheck check = checks.getFirst();
        assertThat(check.statusCode()).isEqualTo(HttpStatus.OK.getCode());
        assertThat(check.title()).isEqualTo("Test title");
        assertThat(check.h1()).isEqualTo("Header");
        assertThat(check.description()).isEqualTo("Something");
    }

    @Test
    void testCreateCheckNetworkError() throws Exception {
        String baseUrl = getMockWebServer().url("/").toString();
        getMockWebServer().shutdown();

        urlRepository.save(baseUrl);
        Url url = urlRepository.findByName(baseUrl).orElseThrow();

        assertThrows(Exception.class, () -> urlService.createCheck(url.id()));

        List<UrlCheck> checks = urlCheckRepository.findByUrlId(url.id());
        assertThat(checks).isEmpty();
    }

    @Test
    void testFindLatestForUrls() throws SQLException {
        urlRepository.save("https://a.example");
        urlRepository.save("https://b.example");
        Url a = urlRepository.findByName("https://a.example").orElseThrow();
        Url b = urlRepository.findByName("https://b.example").orElseThrow();

        urlCheckRepository.save(new UrlCheck(a.id(), HttpStatus.OK.getCode(), "t1", "h1", "d1"));
        urlCheckRepository.save(new UrlCheck(a.id(), HttpStatus.CREATED.getCode(), "t2", "h2", "d2"));
        urlCheckRepository.save(new UrlCheck(b.id(), HttpStatus.INTERNAL_SERVER_ERROR.getCode(), "tb", "hb", "db"));

        Map<Long, UrlCheck> latest = urlCheckRepository.findLatestForUrls(List.of(a.id(), b.id()));
        assertThat(latest).hasSize(2);
        assertThat(latest.get(a.id()).statusCode()).isEqualTo(HttpStatus.CREATED.getCode());
        assertThat(latest.get(b.id()).statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.getCode());
    }
}

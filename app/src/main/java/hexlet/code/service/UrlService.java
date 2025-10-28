package hexlet.code.service;

import hexlet.code.dto.ListUrlsResponse;
import hexlet.code.dto.ShowUrlResponse;
import hexlet.code.exception.UrlAlreadyExistsException;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.UrlNormalizer;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record UrlService(UrlRepository urlRepository, UrlCheckRepository urlCheckRepository) {

    @SneakyThrows(SQLException.class)
    public void createUrl(String url) {
        String normalizedUrl = UrlNormalizer.normalize(url);

        Optional<Url> existingUrl = urlRepository.findByName(normalizedUrl);
        if (existingUrl.isPresent()) {
            throw new UrlAlreadyExistsException("Existing URL provided: " + url);
        }

        urlRepository.save(normalizedUrl);
    }

    @SneakyThrows(SQLException.class)
    public void createCheck(long urlId) {
        Url url = urlRepository.findById(urlId).orElseThrow(() -> new IllegalArgumentException("URL not found"));
        HttpResponse<String> response = Unirest.get(url.name()).asString();

        urlCheckRepository.save(constructCheck(urlId, response));
    }

    @SneakyThrows(SQLException.class)
    public ListUrlsResponse listUrls() {
        List<Url> urls = urlRepository.findAll();
        List<Long> ids = urls.stream().map(Url::id).toList();
        Map<Long, UrlCheck> latestChecks = urlCheckRepository.findLatestForUrls(ids);

        return new ListUrlsResponse(urls, latestChecks);
    }

    @SneakyThrows(SQLException.class)
    public ShowUrlResponse showUrl(long id) {
        Url url = urlRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("URL not found"));
        List<UrlCheck> checks = urlCheckRepository.findByUrlId(id);

        return new ShowUrlResponse(url, checks);
    }

    private UrlCheck constructCheck(Long urlId, HttpResponse<String> response) {
        int status = response.getStatus();
        Document document = Jsoup.parse(response.getBody());
        String title = document.title();
        Element h1Element = document.selectFirst("h1");
        String h1 = h1Element == null ? "" : h1Element.text();
        Element descriptionElement = document.selectFirst("meta[name=description]");
        String description = descriptionElement == null ? "" : descriptionElement.attr("content");

        return new UrlCheck(urlId, status, title, h1, description);
    }
}

package hexlet.code.service;

import hexlet.code.message.FlashMessage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.message.FlashType;
import hexlet.code.util.NamedRoutes;
import hexlet.code.util.UrlNormalizer;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public record UrlService(UrlRepository urlRepository, UrlCheckRepository urlCheckRepository) {
    public UrlProcessingResult createUrl(String url) {
        try {
            String normalizedUrl = UrlNormalizer.normalize(url);
            Optional<Url> existingUrl = urlRepository.findByName(normalizedUrl);

            if (existingUrl.isPresent()) {
                return UrlProcessingResult.alreadyExists();
            }

            urlRepository.save(normalizedUrl);
            return UrlProcessingResult.success();
        } catch (IllegalArgumentException e) {
            log.warn("Invalid URL provided: {}", url, e);
            return UrlProcessingResult.invalidUrl();
        } catch (Exception e) {
            log.error("Error creating URL: {}", url, e);
            return UrlProcessingResult.error();
        }
    }

    public UrlProcessingResult createCheck(Long urlId) {
        try {
            Optional<Url> urlOptional = urlRepository.findById(urlId);
            Url url = urlOptional.orElseThrow(() -> new IllegalArgumentException("URL not found"));
            HttpResponse<String> response = Unirest.get(url.name()).asString();

            urlCheckRepository.save(constructCheck(urlId, response));
            return UrlProcessingResult.checkSuccess(urlId);
        } catch (Exception e) {
            log.error("Error while checking url {}", urlId, e);
            return UrlProcessingResult.checkError();
        }
    }

    public Map<Long, UrlCheck> findLatestForUrls(List<Long> urlIds) throws SQLException {
        return urlCheckRepository.findLatestForUrls(urlIds);
    }

    public List<UrlCheck> findByUrlId(Long urlId) throws SQLException {
        return urlCheckRepository.findByUrlId(urlId);
    }

    public Optional<Url> findById(Long id) throws SQLException {
        return urlRepository.findById(id);
    }

    public List<Url> findAll() throws SQLException {
        return urlRepository.findAll();
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

    @Getter
    @RequiredArgsConstructor
    public static class UrlProcessingResult {
        private final boolean success;
        private final FlashMessage flashMessage;
        private final String redirectPath;

        public static UrlProcessingResult success() {
            return new UrlProcessingResult(true,
                    new FlashMessage("Страница успешно добавлена", FlashType.SUCCESS),
                    NamedRoutes.urlsPath());
        }

        public static UrlProcessingResult alreadyExists() {
            return new UrlProcessingResult(false,
                    new FlashMessage("Страница уже существует", FlashType.WARNING),
                    NamedRoutes.urlsPath());
        }

        public static UrlProcessingResult invalidUrl() {
            return new UrlProcessingResult(false,
                    new FlashMessage("Некорректный URL", FlashType.DANGER),
                    NamedRoutes.rootPath());
        }

        public static UrlProcessingResult error() {
            return new UrlProcessingResult(false,
                    new FlashMessage("Ошибка при добавлении", FlashType.DANGER),
                    NamedRoutes.rootPath());
        }

        public static UrlProcessingResult checkSuccess(Long urlId) {
            return new UrlProcessingResult(true,
                    new FlashMessage("Проверка успешно добавлена", FlashType.SUCCESS),
                    NamedRoutes.urlsPath() + "/" + urlId);
        }

        public static UrlProcessingResult checkError() {
            return new UrlProcessingResult(false,
                    new FlashMessage("Ошибка проверки", FlashType.DANGER),
                    NamedRoutes.urlsPath());
        }
    }
}

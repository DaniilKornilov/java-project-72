package hexlet.code.service;

import hexlet.code.message.FlashMessage;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import hexlet.code.message.FlashType;
import hexlet.code.util.NamedRoutes;
import hexlet.code.util.UrlNormalizer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
public record UrlService(UrlRepository urlRepository) {
    public UrlCreationResult createUrl(String url) {
        try {
            String normalizedUrl = UrlNormalizer.normalize(url);
            Optional<Url> existingUrl = findByName(normalizedUrl);

            if (existingUrl.isPresent()) {
                return UrlCreationResult.alreadyExists();
            }

            saveUrl(normalizedUrl);
            return UrlCreationResult.success();
        } catch (IllegalArgumentException e) {
            log.warn("Invalid URL provided: {}", url, e);
            return UrlCreationResult.invalidUrl();
        } catch (Exception e) {
            log.error("Error creating URL: {}", url, e);
            return UrlCreationResult.error();
        }
    }

    public Optional<Url> findByName(String name) throws SQLException {
        return urlRepository.findByName(name);
    }

    public Optional<Url> findById(Long id) throws SQLException {
        return urlRepository.findById(id);
    }

    public List<Url> findAll() throws SQLException {
        return urlRepository.findAll();
    }

    public void saveUrl(String url) throws SQLException {
        urlRepository.save(url);
    }

    @Getter
    @RequiredArgsConstructor
    public static class UrlCreationResult {
        private final boolean success;
        private final FlashMessage flashMessage;
        private final String redirectPath;

        public static UrlCreationResult success() {
            return new UrlCreationResult(true,
                    new FlashMessage("Страница успешно добавлена", FlashType.SUCCESS),
                    NamedRoutes.urlsPath());
        }

        public static UrlCreationResult alreadyExists() {
            return new UrlCreationResult(false,
                    new FlashMessage("Страница уже существует", FlashType.WARNING),
                    NamedRoutes.urlsPath());
        }

        public static UrlCreationResult invalidUrl() {
            return new UrlCreationResult(false,
                    new FlashMessage("Некорректный URL", FlashType.DANGER),
                    NamedRoutes.rootPath());
        }

        public static UrlCreationResult error() {
            return new UrlCreationResult(false,
                    new FlashMessage("Ошибка при добавлении", FlashType.DANGER),
                    NamedRoutes.rootPath());
        }
    }
}

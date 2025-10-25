package hexlet.code.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.net.URI;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UrlNormalizer {
    private static final int UNDEFINED_PORT = -1;

    public static String normalize(String inputUrl) {
        URI uri = parseAndValidateUri(inputUrl);

        StringBuilder normalized = new StringBuilder()
                .append(uri.getScheme())
                .append("://")
                .append(uri.getHost());

        if (uri.getPort() != UNDEFINED_PORT) {
            normalized.append(":").append(uri.getPort());
        }

        return normalized.toString();
    }

    private static URI parseAndValidateUri(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL cannot be null or blank");
        }

        try {
            URI uri = new URI(url);
            if (uri.getScheme() == null || uri.getHost() == null) {
                throw new IllegalArgumentException("URL must have scheme and host");
            }
            return uri;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid URL format: " + url, e);
        }
    }
}

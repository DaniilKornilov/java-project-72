package hexlet.code.util;

import hexlet.code.exception.InvalidUrlException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.net.URL;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UrlNormalizer {
    private static final int UNDEFINED_PORT = -1;

    public static String normalize(String inputUrl) {
        URL url = parseAndValidateUrl(inputUrl);

        StringBuilder normalized = new StringBuilder()
                .append(url.getProtocol())
                .append("://")
                .append(url.getHost());

        int port = url.getPort();
        if (port != UNDEFINED_PORT) {
            normalized.append(":").append(port);
        }

        return normalized.toString();
    }

    private static URL parseAndValidateUrl(String url) {
        try {
            return new URI(url).toURL();
        } catch (Exception e) {
            throw new InvalidUrlException("Invalid URL format: " + url);
        }
    }
}

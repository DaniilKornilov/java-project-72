package hexlet.code.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NamedRoutes {
    private static final String ROOT_PATH = "/";
    private static final String URLS_PATH = "/urls";
    private static final String CHECKS_PATH = "/checks";

    public static String rootPath() {
        return ROOT_PATH;
    }

    public static String urlPath(Long id) {
        return urlPath(String.valueOf(id));
    }

    public static String urlPath(String id) {
        return URLS_PATH + "/" + id;
    }

    public static String urlsPath() {
        return URLS_PATH;
    }

    public static String urlChecksPath(Long id) {
        return urlChecksPath(String.valueOf(id));
    }

    public static String urlChecksPath(String id) {
        return URLS_PATH + "/" + id + CHECKS_PATH;
    }
}

package hexlet.code;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static hexlet.code.config.JavalinConfiguration.getApp;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class App {
    public static void main(String[] args) {
        int port = getPort();
        log.info("Starting server on port {}", port);
        getApp().start(port);
    }

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");
        return Integer.parseInt(port);
    }
}

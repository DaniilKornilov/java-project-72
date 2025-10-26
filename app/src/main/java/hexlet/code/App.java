package hexlet.code;

import hexlet.code.config.JavalinConfiguration;
import io.javalin.Javalin;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class App {
    public static void main(String[] args) {
        int port = getPort();
        log.info("Starting server on port {}", port);
        getApp().start(port);
    }

    public static Javalin getApp() {
        return JavalinConfiguration.getApp();
    }

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");
        return Integer.parseInt(port);
    }
}

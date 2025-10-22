package hexlet.code;

import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static Javalin getApp() {
        return Javalin.create(config -> config.bundledPlugins.enableDevLogging())
                .get("/", ctx -> ctx.result("Hello World"));
    }

    public static void main(String[] args) {
        int port = getPort();
        LOGGER.info("Starting server on port {}", port);
        Javalin app = getApp();
        app.start(port);
    }

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");
        return Integer.parseInt(port);
    }
}

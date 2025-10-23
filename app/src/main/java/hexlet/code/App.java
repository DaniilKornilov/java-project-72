package hexlet.code;

import hexlet.code.config.DatabaseConfiguration;
import hexlet.code.config.DatabaseInitializer;
import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        int port = getPort();
        initDatabase();
        LOGGER.info("Starting server on port {}", port);
        getApp().start(port);
    }

    private static Javalin getApp() {
        return Javalin.create(config -> config.bundledPlugins.enableDevLogging())
                .get("/", ctx -> ctx.result("Hello World"));
    }

    private static void initDatabase() {
        DataSource dataSource = DatabaseConfiguration.getDataSource();
        DatabaseInitializer.init(dataSource);
    }

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");
        return Integer.parseInt(port);
    }
}

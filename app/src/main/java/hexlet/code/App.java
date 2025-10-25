package hexlet.code;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
import hexlet.code.config.DatabaseConfiguration;
import hexlet.code.config.DatabaseInitializer;
import hexlet.code.controller.RootController;
import hexlet.code.controller.UrlController;
import hexlet.code.repository.UrlRepository;
import hexlet.code.service.UrlService;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class App {
    public static void main(String[] args) {
        int port = getPort();
        initDatabase();
        log.info("Starting server on port {}", port);
        getApp().start(port);
    }

    private static Javalin getApp() {
        DataSource dataSource = DatabaseConfiguration.getDataSource();
        UrlRepository urlRepository = new UrlRepository(dataSource);

        UrlService urlService = new UrlService(urlRepository);
        UrlController urlController = new UrlController(urlService);

        RootController rootController = new RootController();

        Javalin javalin = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.fileRenderer(new JavalinJte(createTemplateEngine()));
        });
        javalin.get("/", rootController::showMainPage);
        javalin.post("/urls", urlController::addUrl);
        javalin.get("/urls", urlController::listUrls);
        javalin.get("/urls/{id}", urlController::showUrl);

        return javalin;
    }

    private static void initDatabase() {
        DataSource dataSource = DatabaseConfiguration.getDataSource();
        DatabaseInitializer.init(dataSource);
    }

    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        return TemplateEngine.create(codeResolver, ContentType.Html);
    }

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");
        return Integer.parseInt(port);
    }
}

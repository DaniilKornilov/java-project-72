package hexlet.code.config;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
import hexlet.code.App;
import hexlet.code.controller.RootController;
import hexlet.code.controller.UrlController;
import hexlet.code.repository.UrlRepository;
import hexlet.code.service.UrlService;
import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.sql.DataSource;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JavalinConfiguration {
    public static Javalin getApp() {
        DataSource dataSource = DatabaseConfiguration.getDataSource();
        DatabaseInitializer.init(dataSource);
        UrlRepository urlRepository = new UrlRepository(dataSource);

        UrlService urlService = new UrlService(urlRepository);
        UrlController urlController = new UrlController(urlService);

        RootController rootController = new RootController();

        Javalin javalin = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.fileRenderer(new JavalinJte(createTemplateEngine()));
        });
        javalin.get(NamedRoutes.rootPath(), rootController::showMainPage);
        javalin.post(NamedRoutes.urlsPath(), urlController::addUrl);
        javalin.get(NamedRoutes.urlsPath(), urlController::listUrls);
        javalin.get(NamedRoutes.urlsPath() + "/{id}", urlController::showUrl);

        return javalin;
    }

    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        return TemplateEngine.create(codeResolver, ContentType.Html);
    }
}

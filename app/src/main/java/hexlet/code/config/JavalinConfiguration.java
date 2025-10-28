package hexlet.code.config;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
import hexlet.code.App;
import hexlet.code.controller.RootController;
import hexlet.code.controller.UrlController;
import hexlet.code.exception.InvalidUrlException;
import hexlet.code.exception.UrlAlreadyExistsException;
import hexlet.code.message.FlashMessage;
import hexlet.code.message.FlashType;
import hexlet.code.repository.UrlRepository;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.service.UrlService;
import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import io.javalin.rendering.template.JavalinJte;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.sql.DataSource;

import static hexlet.code.util.JteUtils.handleError;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JavalinConfiguration {
    public static Javalin getApp() {
        DataSource dataSource = DatabaseConfiguration.getDataSource();
        DatabaseInitializer.init(dataSource);

        UrlRepository urlRepository = new UrlRepository(dataSource);
        UrlCheckRepository urlCheckRepository = new UrlCheckRepository(dataSource);

        UrlService urlService = new UrlService(urlRepository, urlCheckRepository);
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
        javalin.post(NamedRoutes.urlChecksPath("/{id}"), urlController::createCheck);

        javalin.exception(Exception.class, (e, ctx) ->
                handleError(ctx, new FlashMessage("Внутренняя ошибка сервера", FlashType.DANGER),
                        NamedRoutes.rootPath(), HttpStatus.INTERNAL_SERVER_ERROR.getCode()));

        javalin.exception(InvalidUrlException.class, (e, ctx) ->
                handleError(ctx, new FlashMessage("Некорректный URL", FlashType.WARNING),
                        NamedRoutes.rootPath(), HttpStatus.BAD_REQUEST.getCode()));

        javalin.exception(UrlAlreadyExistsException.class, (e, ctx) ->
                handleError(ctx, new FlashMessage("Страница уже существует", FlashType.INFO),
                        NamedRoutes.urlsPath(), HttpStatus.BAD_REQUEST.getCode()));

        return javalin;
    }

    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        return TemplateEngine.create(codeResolver, ContentType.Html);
    }
}

package hexlet.code.controller;

import hexlet.code.dto.ListUrlsResponse;
import hexlet.code.dto.ShowUrlResponse;
import hexlet.code.message.FlashMessage;
import hexlet.code.message.FlashType;
import hexlet.code.page.UrlPage;
import hexlet.code.page.UrlsPage;
import hexlet.code.service.UrlService;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;

import static hexlet.code.util.JteUtils.redirect;
import static hexlet.code.util.JteUtils.renderTemplate;
import static hexlet.code.util.JteUtils.setFlashMessage;

public record UrlController(UrlService urlService) {
    public void addUrl(Context ctx) {
        urlService.createUrl(ctx.formParam("url"));

        setFlashMessage(ctx, new FlashMessage("Страница успешно добавлена", FlashType.SUCCESS));
        redirect(ctx, NamedRoutes.urlsPath());
    }

    public void listUrls(Context ctx) {
        ListUrlsResponse response = urlService.listUrls();

        UrlsPage page = new UrlsPage(response.urls(), response.latestChecks());
        setFlashMessage(ctx, page);
        renderTemplate(ctx, "urls/index.jte", page);
    }

    public void showUrl(Context ctx) {
        long id = Long.parseLong(ctx.pathParam("id"));
        ShowUrlResponse response = urlService.showUrl(id);

        UrlPage page = new UrlPage(response.url(), response.checks());
        setFlashMessage(ctx, page);
        renderTemplate(ctx, "urls/show.jte", page);
    }

    public void createCheck(Context ctx) {
        long id = Long.parseLong(ctx.pathParam("id"));
        urlService.createCheck(id);

        setFlashMessage(ctx, new FlashMessage("Проверка успешно добавлена", FlashType.SUCCESS));
        redirect(ctx, NamedRoutes.urlsPath() + "/" + id);
    }
}

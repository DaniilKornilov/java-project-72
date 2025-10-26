package hexlet.code.controller;

import hexlet.code.page.UrlPage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.page.UrlsPage;
import hexlet.code.service.UrlService;
import io.javalin.http.Context;

import java.util.List;
import java.util.Map;

import static hexlet.code.util.JteUtils.handleError;
import static hexlet.code.util.JteUtils.renderTemplate;
import static hexlet.code.util.JteUtils.setFlashMessage;
import static io.javalin.http.HttpStatus.BAD_REQUEST;
import static io.javalin.http.HttpStatus.INTERNAL_SERVER_ERROR;

public record UrlController(UrlService urlService) {
    public void addUrl(Context ctx) {
        UrlService.UrlProcessingResult result = urlService.createUrl(ctx.formParam("url"));

        setFlashMessage(ctx, result.getFlashMessage());
        ctx.redirect(result.getRedirectPath());
    }

    public void listUrls(Context ctx) {
        try {
            List<Url> urls = urlService.findAll();
            List<Long> ids = urls.stream().map(Url::id).toList();
            Map<Long, UrlCheck> latestChecks = urlService.findLatestForUrls(ids);

            UrlsPage page = new UrlsPage(urls, latestChecks);
            setFlashMessage(ctx, page);
            renderTemplate(ctx, "urls/index.jte", page);
        } catch (Exception e) {
            handleError(ctx, "Ошибка при получении списка сайтов", INTERNAL_SERVER_ERROR.getCode());
        }
    }

    public void showUrl(Context ctx) {
        try {
            Long id = Long.valueOf(ctx.pathParam("id"));
            Url url = urlService.findById(id).orElseThrow(() -> new IllegalArgumentException("URL not found"));
            List<UrlCheck> checks = urlService.findByUrlId(id);

            UrlPage page = new UrlPage(url, checks);
            setFlashMessage(ctx, page);
            renderTemplate(ctx, "urls/show.jte", page);
        } catch (NumberFormatException e) {
            handleError(ctx, "Неверный формат ID", BAD_REQUEST.getCode());
        } catch (Exception e) {
            handleError(ctx, "Ошибка при получении сайта", INTERNAL_SERVER_ERROR.getCode());
        }
    }

    public void createCheck(Context ctx) {
        try {
            Long id = Long.valueOf(ctx.pathParam("id"));
            UrlService.UrlProcessingResult result = urlService.createCheck(id);

            setFlashMessage(ctx, result.getFlashMessage());
            ctx.redirect(result.getRedirectPath());
        } catch (NumberFormatException e) {
            handleError(ctx, "Неверный формат ID", BAD_REQUEST.getCode());
        } catch (Exception e) {
            handleError(ctx, "Ошибка при создании проверки", INTERNAL_SERVER_ERROR.getCode());
        }
    }
}

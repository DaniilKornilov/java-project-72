package hexlet.code.controller;

import hexlet.code.page.MainPage;
import io.javalin.http.Context;

import java.util.Map;

import static hexlet.code.util.JteUtils.renderTemplate;
import static hexlet.code.util.JteUtils.setFlashMessage;

public final class RootController {
    public void showMainPage(Context ctx) {
        MainPage page = new MainPage();
        setFlashMessage(ctx, page);
        renderTemplate(ctx, "index.jte", Map.of("page", page));
    }
}

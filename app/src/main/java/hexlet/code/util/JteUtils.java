package hexlet.code.util;

import hexlet.code.message.FlashMessage;
import hexlet.code.page.BasePage;
import io.javalin.http.Context;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JteUtils {
    private static final String FLASH_ATTRIBUTE = "flash";
    private static final String FLASH_TYPE_ATTRIBUTE = "flashType";
    private static final String PAGE_PARAM = "page";

    public static void setFlashMessage(Context ctx, BasePage page) {
        page.setFlash(ctx.consumeSessionAttribute(FLASH_ATTRIBUTE));
        page.setFlashType(ctx.consumeSessionAttribute(FLASH_TYPE_ATTRIBUTE));
    }

    public static void setFlashMessage(Context ctx, FlashMessage flashMessage) {
        ctx.sessionAttribute(FLASH_ATTRIBUTE, flashMessage.message());
        ctx.sessionAttribute(FLASH_TYPE_ATTRIBUTE, flashMessage.type().getValue());
    }

    public static void renderTemplate(Context ctx, String template, BasePage page) {
        ctx.render(template, Map.of(PAGE_PARAM, page));
    }

    public static void handleError(Context ctx, String message, int status) {
        ctx.status(status).result(message);
    }
}

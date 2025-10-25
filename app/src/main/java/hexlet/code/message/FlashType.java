package hexlet.code.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FlashType {
    SUCCESS("success"),
    DANGER("danger"),
    WARNING("warning"),
    INFO("info");

    private final String value;
}

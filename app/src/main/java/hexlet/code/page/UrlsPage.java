package hexlet.code.page;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class UrlsPage extends BasePage {
    private final List<Url> urls;
    private final Map<Long, UrlCheck> latestChecks;
}

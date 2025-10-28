package hexlet.code.dto;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;

import java.util.List;
import java.util.Map;

public record ListUrlsResponse(List<Url> urls, Map<Long, UrlCheck> latestChecks) {
}

package hexlet.code.dto;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;

import java.util.List;

public record ShowUrlResponse(Url url, List<UrlCheck> checks) {
}

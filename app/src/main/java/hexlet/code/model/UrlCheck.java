package hexlet.code.model;

import java.sql.Timestamp;

public record UrlCheck(
        Long id,
        Long urlId,
        Integer statusCode,
        String title,
        String h1,
        String description,
        Timestamp createdAt
) {
}

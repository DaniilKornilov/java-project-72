package hexlet.code.model;

import java.time.LocalDateTime;

public record UrlCheck(
        Long id,
        Long urlId,
        Integer statusCode,
        String title,
        String h1,
        String description,
        LocalDateTime createdAt
) {
    public UrlCheck(
            Long urlIdParam,
            Integer statusCodeParam,
            String titleParam,
            String h1Param,
            String descriptionParam
    ) {
        this(null,
                urlIdParam,
                statusCodeParam,
                titleParam,
                h1Param,
                descriptionParam,
                null
        );
    }
}

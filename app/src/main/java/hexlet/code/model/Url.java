package hexlet.code.model;

import java.time.LocalDateTime;

record Url(Long id,
           String name,
           LocalDateTime createdAt) {
    Url(String urlName, LocalDateTime urlCreatedAt) {
        this(null, urlName, urlCreatedAt);
    }
}

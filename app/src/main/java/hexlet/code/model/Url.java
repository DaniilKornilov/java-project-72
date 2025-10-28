package hexlet.code.model;

import java.time.LocalDateTime;

public record Url(Long id,
                  String name,
                  LocalDateTime createdAt) {
}

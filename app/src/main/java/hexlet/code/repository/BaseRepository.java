package hexlet.code.repository;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.sql.DataSource;

@Getter
@RequiredArgsConstructor
public abstract class BaseRepository {
    private final DataSource dataSource;
}

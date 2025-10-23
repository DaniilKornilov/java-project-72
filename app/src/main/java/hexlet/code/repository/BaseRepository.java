package hexlet.code.repository;

import javax.sql.DataSource;

public abstract class BaseRepository {
    private final DataSource dataSource;

    protected BaseRepository(DataSource connectionDataSource) {
        this.dataSource = connectionDataSource;
    }
}

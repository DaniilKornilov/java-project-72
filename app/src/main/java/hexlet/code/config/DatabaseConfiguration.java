package hexlet.code.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.sql.DataSource;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DatabaseConfiguration {
    private static final int MAX_POOL_SIZE = 10;

    private static HikariDataSource dataSource;

    public static DataSource getDataSource() {
        if (dataSource == null) {
            HikariConfig config = new HikariConfig();
            String jdbcUrl = System.getenv("JDBC_DATABASE_URL");

            if (jdbcUrl != null && !jdbcUrl.isEmpty()) {
                config.setJdbcUrl(jdbcUrl);
                config.setDriverClassName("org.postgresql.Driver");
            } else {
                config.setJdbcUrl("jdbc:h2:mem:project");
            }

            config.setMaximumPoolSize(MAX_POOL_SIZE);
            config.setAutoCommit(true);

            dataSource = new HikariDataSource(config);
        }
        return dataSource;
    }
}

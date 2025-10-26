package hexlet.code.repository;

import hexlet.code.model.UrlCheck;

import javax.sql.DataSource;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class UrlCheckRepository extends BaseRepository {
    public UrlCheckRepository(DataSource dataSource) {
        super(dataSource);
    }

    @SuppressWarnings("checkstyle:magicnumber")
    public void save(UrlCheck urlCheck) throws SQLException {
        String sql = "INSERT INTO url_checks (url_id, status_code, title, h1, description, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, urlCheck.urlId());
            statement.setInt(2, urlCheck.statusCode());
            statement.setString(3, urlCheck.title());
            statement.setString(4, urlCheck.h1());
            statement.setString(5, urlCheck.description());
            statement.setTimestamp(6, Timestamp.valueOf(java.time.LocalDateTime.now()));
            statement.executeUpdate();
        }
    }

    public List<UrlCheck> findByUrlId(Long urlId) throws SQLException {
        String sql = "SELECT id, url_id, status_code, title, h1, description, created_at "
                + "FROM url_checks WHERE url_id = ? ORDER BY id DESC";
        try (Connection connection = getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, urlId);
            ResultSet rs = statement.executeQuery();
            List<UrlCheck> checks = new ArrayList<>();
            while (rs.next()) {
                checks.add(mapRow(rs));
            }
            return checks;
        }
    }

    public Map<Long, UrlCheck> findLatestForUrls(List<Long> urlIds) throws SQLException {
        if (urlIds.isEmpty()) {
            return Collections.emptyMap();
        }

        String sql = "SELECT uc.id, uc.url_id, uc.status_code, uc.title, uc.h1, uc.description, uc.created_at "
                + "FROM url_checks uc "
                + "INNER JOIN ( "
                + "    SELECT url_id, MAX(id) as latest_id "
                + "    FROM url_checks "
                + "    WHERE url_id = ANY(?) "
                + "    GROUP BY url_id "
                + ") latest ON uc.id = latest.latest_id";

        try (Connection connection = getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            Array idArray = connection.createArrayOf("BIGINT", urlIds.toArray());
            statement.setArray(1, idArray);

            try (ResultSet rs = statement.executeQuery()) {
                Map<Long, UrlCheck> result = new HashMap<>();
                while (rs.next()) {
                    UrlCheck check = mapRow(rs);
                    result.put(check.urlId(), check);
                }
                return result;
            }
        }
    }

    private UrlCheck mapRow(ResultSet rs) throws SQLException {
        return new UrlCheck(
                rs.getLong("id"),
                rs.getLong("url_id"),
                rs.getObject("status_code") != null ? rs.getInt("status_code") : null,
                rs.getString("title"),
                rs.getString("h1"),
                rs.getString("description"),
                rs.getTimestamp("created_at")
        );
    }
}

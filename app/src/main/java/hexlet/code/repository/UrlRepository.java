package hexlet.code.repository;

import hexlet.code.model.Url;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class UrlRepository extends BaseRepository {
    public UrlRepository(DataSource dataSource) {
        super(dataSource);
    }

    public Optional<Url> findByName(String name) throws SQLException {
        String sql = "SELECT id, name, created_at FROM urls WHERE name = ?";
        try (Connection connection = getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(mapRow(resultSet));
            }
            return Optional.empty();
        }
    }

    public Optional<Url> findById(Long id) throws SQLException {
        String sql = "SELECT id, name, created_at FROM urls WHERE id = ?";
        try (Connection connection = getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(mapRow(resultSet));
            }
            return Optional.empty();
        }
    }

    public List<Url> findAll() throws SQLException {
        String sql = "SELECT id, name, created_at FROM urls ORDER BY id DESC";
        try (Connection connection = getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            List<Url> urls = new ArrayList<>();
            while (resultSet.next()) {
                urls.add(mapRow(resultSet));
            }
            return urls;
        }
    }

    public void save(String name) throws SQLException {
        String sql = "INSERT INTO urls (name, created_at) VALUES (?, ?)";
        try (Connection connection = getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, name);
            statement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                return;
            }
            throw new SQLException("Failed to insert url");
        }
    }

    private Url mapRow(ResultSet resultSet) throws SQLException {
        return new Url(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getTimestamp("created_at")
        );
    }
}

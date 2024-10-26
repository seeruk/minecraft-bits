package dev.seeruk.mod.fabric.nicks.database;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class MySQLStore implements Store {

    private final DataSource dataSource;

    @Override
    public Map<UUID, String> getColours() {
        var query = """
            SELECT BIN_TO_UUID(player_uuid), colour
            FROM seers_nicks_colours""";

        try (var conn = dataSource.getConnection()) {
            return executeQuery(conn, query, this::resultSetToMap);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<UUID, String> getNicks() {
        var query = """
            SELECT BIN_TO_UUID(player_uuid), nick
            FROM seers_nicks_nicks""";

        try (var conn = dataSource.getConnection()) {
            return executeQuery(conn, query, this::resultSetToMap);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int setColour(UUID playerUuid, String colour) {
        var query = """
            REPLACE INTO seers_nicks_colours (player_uuid, colour)
            VALUES (UUID_TO_BIN(?), ?)""";

        try (var conn = dataSource.getConnection()) {
            return executeUpdate(conn, query, (stmt) -> {
                stmt.setString(1, playerUuid.toString());
                stmt.setString(2, colour);
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int setNick(UUID playerUuid, String nick) {
        var query = """
            REPLACE INTO seers_nicks_nicks (player_uuid, nick)
            VALUES (UUID_TO_BIN(?), ?)""";

        try (var conn = dataSource.getConnection()) {
            return executeUpdate(conn, query, (stmt) -> {
                stmt.setString(1, playerUuid.toString());
                stmt.setString(2, nick);
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<UUID, String> resultSetToMap(ResultSet resultSet) throws SQLException {
        var result = new HashMap<UUID, String>();

        while (resultSet.next()) {
            var uuid = UUID.fromString(resultSet.getString(1));
            var nick = resultSet.getString(2);

            result.put(uuid, nick);
        }

        return result;
    }

    @NotNull
    private <T> T executeQuery(Connection conn, String query, ResultSetFunction<T> callback) throws SQLException {
        try (var stmt = conn.prepareStatement(query)) {
            var resultSet = stmt.executeQuery();
            return callback.run(resultSet);
        }
    }

    private int executeUpdate(Connection conn, String query, UpdateStatementFunction updateStatementFn) throws SQLException {
        try (var stmt = conn.prepareStatement(query)) {
            updateStatementFn.apply(stmt);
            return stmt.executeUpdate();
        }
    }

    private interface UpdateStatementFunction {
        void apply(PreparedStatement stmt) throws SQLException;
    }

    private interface ResultSetFunction<T> {
        T run(ResultSet resultSet) throws SQLException;
    }
}

package dev.seeruk.mod.fabric.resourceworlds.database;

import dev.seeruk.mod.fabric.resourceworlds.ResourceWorldsMod;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class MySQLStore {

    private final DataSource dataSource;

    public record PlayerLocation(UUID playerUuid, String dimension, Vec3d location, float yaw, float pitch) {}

    public Optional<PlayerLocation> getPlayerLocation(UUID playerUuid, Identifier dimension) {
        var query = """
            SELECT BIN_TO_UUID(player_uuid), dimension, x, y, z, yaw, pitch
            FROM seers_resource_worlds_player_locations
            WHERE player_uuid = UUID_TO_BIN(?)
            AND dimension = ?""";

        return getPlayerLocation(query, (stmt) -> {
            stmt.setString(1, playerUuid.toString());
            stmt.setString(2, dimension.toString());
        });
    }

    public Optional<PlayerLocation> getPlayerLeaveLocation(UUID playerUuid) {
        var query = """
            SELECT BIN_TO_UUID(player_uuid), dimension, x, y, z, yaw, pitch
            FROM seers_resource_worlds_player_locations
            WHERE player_uuid = UUID_TO_BIN(?)
            AND dimension LIKE 'minecraft:%'
            LIMIT 1""";

        return getPlayerLocation(query, (stmt) -> stmt.setString(1, playerUuid.toString()));
    }

    private Optional<PlayerLocation> getPlayerLocation(String query, ModifyStatementFunction callback) {
        try (var conn = dataSource.getConnection()) {
            try (var stmt = conn.prepareStatement(query)) {
                callback.apply(stmt);

                var resultSet = stmt.executeQuery();

                if (resultSet.next()) {
                    return Optional.of(new PlayerLocation(
                        UUID.fromString(resultSet.getString(1)),
                        resultSet.getString(2),
                        new Vec3d(
                            resultSet.getDouble(3),
                            resultSet.getDouble(4),
                            resultSet.getDouble(5)
                        ),
                        resultSet.getFloat(6),
                        resultSet.getFloat(7)
                    ));
                }

                return Optional.empty();
            }
        } catch (SQLException e) {
            ResourceWorldsMod.LOGGER.warn("failed to get player location", e);
            return Optional.empty();
        } catch (Exception e) {
            ResourceWorldsMod.LOGGER.error("failed to get player location", e);
            return Optional.empty();
        }
    }

    public int setPlayerLocation(PlayerLocation playerLocation) throws SQLException {
        var query = """
            REPLACE INTO seers_resource_worlds_player_locations (player_uuid, dimension, x, y, z, yaw, pitch)
            VALUES (UUID_TO_BIN(?), ?, ?, ?, ?, ?, ?)""";

        try (var conn = dataSource.getConnection()) {
            try (var stmt = conn.prepareStatement(query)) {
                stmt.setString(1, playerLocation.playerUuid.toString());
                stmt.setString(2, playerLocation.dimension);
                stmt.setDouble(3, playerLocation.location.getX());
                stmt.setDouble(4, playerLocation.location.getY());
                stmt.setDouble(5, playerLocation.location.getZ());
                stmt.setFloat(6, playerLocation.yaw);
                stmt.setFloat(7, playerLocation.pitch);

                return stmt.executeUpdate();
            }
        }
    }

    private interface ModifyStatementFunction {
        void apply(PreparedStatement stmt) throws SQLException;
    }
}

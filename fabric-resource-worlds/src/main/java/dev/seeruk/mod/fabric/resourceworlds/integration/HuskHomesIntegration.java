package dev.seeruk.mod.fabric.resourceworlds.integration;

import dev.seeruk.mod.fabric.resourceworlds.ResourceWorldsMod;
import dev.seeruk.mod.fabric.resourceworlds.database.MySQLStore;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.william278.huskhomes.event.TeleportWarmupCallback;
import net.william278.huskhomes.user.FabricUser;

import java.sql.SQLException;

public class HuskHomesIntegration {

    public static void register() {
        TeleportWarmupCallback.EVENT.register(teleport -> {
            if (teleport.getTimedTeleport().getTeleporter() instanceof FabricUser user) {
                return setPlayerLocation(user.getPlayer());
            }
            return ActionResult.PASS;
        });
    }

    private static ActionResult setPlayerLocation(ServerPlayerEntity player) {
        var mod = ResourceWorldsMod.getInstance();

        try {
            mod.getStore().setPlayerLocation(new MySQLStore.PlayerLocation(
                player.getUuid(),
                player.getWorld().getRegistryKey().getValue().toString(),
                player.getPos(),
                player.getYaw(),
                player.getPitch()
            ));
        } catch (SQLException e) {
            ResourceWorldsMod.LOGGER.warn("failed to set player location", e);
            return ActionResult.FAIL;
        } catch (Exception e) {
            ResourceWorldsMod.LOGGER.error("failed to set player location", e);
            return ActionResult.FAIL;
        }

        return ActionResult.SUCCESS;
    }
}

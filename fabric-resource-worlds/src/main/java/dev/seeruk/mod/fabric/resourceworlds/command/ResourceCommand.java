package dev.seeruk.mod.fabric.resourceworlds.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.seeruk.mod.fabric.resourceworlds.ResourceWorldsMod;
import dev.seeruk.mod.fabric.resourceworlds.database.MySQLStore;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;

import java.sql.SQLException;

import static net.minecraft.server.command.CommandManager.literal;

public class ResourceCommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            dispatcher.register(literal("resource")
                .then(CommandManager.argument("destination", StringArgumentType.string())
                    .suggests((commandContext, suggestionsBuilder) -> {
                        suggestionsBuilder.suggest(ResourceWorldsMod.WORLD_OVERWORLD);
                        suggestionsBuilder.suggest(ResourceWorldsMod.WORLD_NETHER);
                        suggestionsBuilder.suggest(ResourceWorldsMod.WORLD_END);
                        suggestionsBuilder.suggest("leave");
                        return suggestionsBuilder.buildFuture();
                    })
                    .executes(ResourceCommand::onResourceCommand)
                )
            );
        }));
    }

    private static int onResourceCommand(CommandContext<ServerCommandSource> context) {
        var source = context.getSource();
        if (source.getPlayer() == null) {
            return 0;
        }

        var mod = ResourceWorldsMod.getInstance();
        var config = mod.getConfig();
        var player = source.getPlayer();
        var worlds = mod.getWorlds();

        var destination = StringArgumentType.getString(context, "destination");

        // Validate destination
        if (!destination.equals("leave") && (!worlds.containsKey(destination) || !config.dimensions.containsKey(destination))) {
            var message = Text.literal("Invalid destination")
                .setStyle(Style.EMPTY.withColor(Formatting.RED.getColorValue()));

            source.sendFeedback(() -> message, false);
            return -1;
        }

        // We always need to save the players current location
        try {
            mod.getStore().setPlayerLocation(new MySQLStore.PlayerLocation(
                player.getUuid(),
                player.getWorld().getRegistryKey().getValue().toString(),
                player.getPos(),
                player.getYaw(),
                player.getPitch()
            ));
        } catch (SQLException e) {
            ResourceWorldsMod.LOGGER.error("failed to set player location", e);
            return -1;
        }

        TeleportTarget target;

        if (destination.equals("leave")) {
            var currentDimension = player.getWorld().getRegistryKey().getValue().toString();
            if (currentDimension.startsWith("minecraft:")) {
                source.sendFeedback(() -> Text.literal("You're not in a resource world"), false);
                return -1;
            }

            // Handle leaving a resource world
            var playerLocation = mod.getStore().getPlayerLeaveLocation(player.getUuid());
            var overworldSpawn = mod.getServer().getOverworld().getSpawnPos();

            // Get the player's last known vanilla dimension, or fall back to the overworld.
            var dimension = playerLocation
                .map(MySQLStore.PlayerLocation::dimension)
                .orElse("minecraft:overworld");

            // Get the player's last known vanilla coordinates, or fall back to the overworld spawn (?)
            var location = playerLocation
                .map(MySQLStore.PlayerLocation::location)
                .orElse(new Vec3d(overworldSpawn.getX(), overworldSpawn.getY(), overworldSpawn.getZ()));

            var yaw = playerLocation.map(MySQLStore.PlayerLocation::yaw).orElse(0f);
            var pitch = playerLocation.map(MySQLStore.PlayerLocation::pitch).orElse(0f);

            var world = Lists.newArrayList(mod.getServer().getWorlds()).stream()
                .filter(serverWorld -> serverWorld.getRegistryKey().getValue().toString().equals(dimension))
                .findFirst()
                .orElse(mod.getServer().getOverworld());

            target = new TeleportTarget(world, location, Vec3d.ZERO, yaw, pitch, TeleportTarget.NO_OP);
        } else {
            // Handle entering a resource world
            var world = worlds.get(destination).asWorld();
            var dimensionConfig = config.dimensions.get(destination);

            var playerLocation = mod.getStore().getPlayerLocation(player.getUuid(), world.getRegistryKey().getValue());

            var location = playerLocation
                .map(MySQLStore.PlayerLocation::location)
                .orElse(new Vec3d(dimensionConfig.spawn.x, dimensionConfig.spawn.y, dimensionConfig.spawn.z));

            var yaw = playerLocation.map(MySQLStore.PlayerLocation::yaw).orElse(0f);
            var pitch = playerLocation.map(MySQLStore.PlayerLocation::pitch).orElse(0f);

            target = new TeleportTarget(world, location, Vec3d.ZERO, yaw, pitch, TeleportTarget.NO_OP);
        }

        player.teleportTo(target);

        return 0;
    }
}

package dev.seeruk.mod.fabric.nicks.command;

import dev.seeruk.mod.fabric.nicks.NicksEvent;
import dev.seeruk.mod.fabric.nicks.NicksMod;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.server.command.ServerCommandSource;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public abstract class AbstractUpdateCommand {

    protected static final MiniMessage miniMessage = MiniMessage.miniMessage();

    // We keep track of when players last updated their colour on this server so we can avoid spam
    private static final Map<UUID, Instant> playerUpdateTimes = new HashMap<>();

    protected static boolean detectSpam(ServerCommandSource source) {
        var mod = NicksMod.getInstance();
        var adventure = mod.getAdventure();

        var audience = adventure.audience(source).audience();
        var player = source.getPlayer();

        var lastUpdated = playerUpdateTimes.get(player.getUuid());

        // Prevent players spamming this command
        // TODO: Configurable cooldown?
        if (lastUpdated != null && Duration.between(lastUpdated, Instant.now()).toMillis() < 500) {
            audience.sendMessage(miniMessage.deserialize(
                "<red>You're trying to do this too much</red>"
            ));
            return true;
        }

        return false;
    }

    protected static int finalise(ServerCommandSource source) {
        var mod = NicksMod.getInstance();
        var adventure = mod.getAdventure();
        var config = mod.getConfig();
        var store = mod.getStore();

        var audience = adventure.audience(source).audience();
        var player = source.getPlayer();

        // Update our in-memory data to grab the new nick
        store.refresh();

        // Send an event to notify other servers that a nickname / colour has changed
        var event = NicksEvent.newBuilder().setServer(NicksMod.SERVER_UUID.toString()).build();
        mod.getRedisConn().async().publish(config.redis.channel, event.toByteArray());

        playerUpdateTimes.put(player.getUuid(), Instant.now());

        // TODO: Configurable message?
        var feedback = miniMessage
            .deserialize("<green>You are now known as </green>")
            .append(Objects.requireNonNull(adventure.asAdventure(player.getDisplayName())));

        audience.sendMessage(feedback);
        return 1;
    }
}

package dev.seeruk.plugin.velocity.jsq;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.seeruk.common.config.ColorUtil;
import dev.seeruk.common.config.ConfigManager;
import dev.seeruk.common.jsq.JsqEvent;
import dev.seeruk.plugin.velocity.jsq.config.Config;
import dev.seeruk.plugin.velocity.jsq.discord.DiscordContainer;
import dev.seeruk.plugin.velocity.jsq.discord.DiscordMessenger;
import io.lettuce.core.RedisClient;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Plugin(
    id = "seers-jsq",
    name = "Seer's Velocity Join, Switch, Quit",
    description = "Adds network-wide join/switch/quit messages to your server so players know what's going on",
    authors = {"SeerUK"},
    version = BuildConstants.VERSION,
    dependencies = {
        @Dependency(id = "seers-discord", optional = true)
    }
)
public class JsqPlugin {

    private final Path dataDirectory;
    private final Logger logger;
    private final ProxyServer server;

    private Config config;
    private RedisPubSubAsyncCommands<String, byte[]> redisConn;

    // We try to keep track of players that have connected, according to this plugin, so we don't
    // notify about disconnects if we never sent a connection message!
    private Set<String> connectedPlayers;

    @Inject
    public JsqPlugin(@DataDirectory Path dataDirectory, Logger logger, ProxyServer server) {
        this.dataDirectory = dataDirectory;
        this.logger = logger;
        this.server = server;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        var configManager = new ConfigManager(dataDirectory, logger, this);
        // Overwrite the dist config, so it's always up-to-date
        configManager.saveResource("config.dist.yml", true);
        // Fetch the user-defined config
        config = configManager.getConfigWithDefaults(Config.class).orElseThrow();

        this.redisConn = RedisClient.create(config.redis.uri)
            .connectPubSub(RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE))
            .async();

        logger.info("Initialised successfully");
    }

    @Subscribe
    public void onServerConnected(ServerConnectedEvent event) {
        var player = event.getPlayer();

        connectedPlayers.add(player.getUsername());

        var nextServer = event.getServer();
        var previousServer = event.getPreviousServer();

        var placeholders = new Placeholders(
            player.getUsername(),
            nextServer.getServerInfo().getName(),
            previousServer
                .map(server -> server.getServerInfo().getName())
                .orElse("")
        );

        var messageConfig = previousServer.map(server -> config.onSwitch).orElse(config.onJoin);

        this.handleJsqEvent(player, messageConfig, placeholders);
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        var player = event.getPlayer();

        if (!connectedPlayers.contains(player.getUsername())) {
            return;
        }

        connectedPlayers.remove(player.getUsername());

        var placeholders = new Placeholders(
            player.getUsername(),
            "", // No next server on disconnect
            player.getCurrentServer()
                .map(server -> server.getServerInfo().getName())
                .orElse("unknown")
        );

        this.handleJsqEvent(player, config.onQuit, placeholders);
    }

    private void handleJsqEvent(Player player, Config.MessageConfig messageConfig, Placeholders placeholders) {
        var message = getRandomItem(messageConfig.messages);

        var chatMessage = replacePlaceholders(buildMessage(messageConfig.chat, message), placeholders);
        var discordMessage = replacePlaceholders(buildMessage(messageConfig.discord, message), placeholders);

        var protoEvent = JsqEvent.newBuilder()
            .setMessage(chatMessage)
            .setPlayerUuid(player.getUniqueId().toString())
            .setPlayerName(player.getUsername())
            .build();

        this.redisConn.publish(config.redis.channel, protoEvent.toByteArray());

        if (config.discord.enabled && server.getPluginManager().isLoaded("seers-discord")) {
            DiscordContainer.getJda().ifPresent(jda -> {
                var messenger = new DiscordMessenger(jda, config.discord.channelId);
                messenger.sendMessageEmbed(player, discordMessage, ColorUtil.getColorByName(messageConfig.discord.colour));
            });
        }
    }

    private String buildMessage(Config.ChatMessageConfig config, String message) {
        return config.prefix + message + config.suffix;
    }

    private String replacePlaceholders(String input, Placeholders placeholders) {
        return input.replace("{player}", placeholders.player())
            .replace("{next_server}", placeholders.nextServer())
            .replace("{previous_server}", placeholders.previousServer());
    }

    private <T> T getRandomItem(List<T> list) {
        return list.get(new Random().nextInt(list.size()));
    }

    private record Placeholders(
        String player,
        String nextServer,
        String previousServer
    ) {}
}

package dev.seeruk.plugin.velocity.discord;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.seeruk.common.config.ConfigManager;
import dev.seeruk.plugin.velocity.discord.command.PlayerListCommand;
import dev.seeruk.plugin.velocity.discord.config.Config;
import dev.seeruk.plugin.velocity.discord.event.DiscordBuildingEvent;
import dev.seeruk.plugin.velocity.discord.event.DiscordReadyEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Plugin(
    id = "seers-discord",
    name = "Seer's Velocity Discord",
    description = "Provides access to JDA, connects a bot, and shows server status",
    authors = {"SeerUK"},
    version = BuildConstants.VERSION
)
public class DiscordPlugin {
    private final Path dataDirectory;
    private final Logger logger;
    private final ProxyServer server;

    private Config config;
    private JDABuilder jdaBuilder;
    private JDA jda;

    private boolean isJDAReady;

    // Keeps track of when each player joined the server.
    private final Map<String, Instant> playerConnectTimes = new HashMap<>();

    private static DiscordPlugin instance;

    @Inject
    public DiscordPlugin(@DataDirectory Path dataDirectory, Logger logger, ProxyServer server) {
        instance = this;

        var configManager = new ConfigManager(dataDirectory, logger, this);
        // Overwrite the dist config, so it's always up-to-date
        configManager.saveResource("config.dist.yml", true);
        // Fetch the user-defined config
        config = configManager.getConfigWithDefaults(Config.class).orElseThrow();

        this.dataDirectory = dataDirectory;
        this.logger = logger;
        this.server = server;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        this.jdaBuilder = JDABuilder.createDefault(config.discordToken)
            .setEventManager(new AnnotatedEventManager())
            .addEventListeners(this, new PlayerListCommand(this, server));

        server.getEventManager().fire(new DiscordBuildingEvent()).thenAccept((evt) -> {
            this.jda = jdaBuilder.build();
        });

        logger.info("Initialised successfully");
    }

    public static DiscordPlugin getInstance() {
        return instance;
    }

    public JDABuilder getJdaBuilder() {
        return jdaBuilder;
    }

    public void setJdaBuilder(JDABuilder jdaBuilder) {
        this.jdaBuilder = jdaBuilder;
    }

    public Map<String, Instant> getPlayerConnectTimes() {
        return playerConnectTimes;
    }

    @SubscribeEvent
    private void onReady(ReadyEvent event) {
        isJDAReady = true;
        this.registerCommands();

        // Let other plugins know we're ready...
        server.getEventManager().fire(new DiscordReadyEvent(jda));

        logger.info("Discord bot is ready!");
    }

    @SubscribeEvent
    private void onShutdown(ShutdownEvent event) {
        isJDAReady = false;

        logger.info("Discord has shut down");
    }

    @Subscribe
    private void onPlayerConnect(ServerConnectedEvent event) {
        playerConnectTimes.put(event.getPlayer().getUsername(), Instant.now());
    }

    @Subscribe
    private void onPlayerDisconnect(DisconnectEvent event) {
        playerConnectTimes.remove(event.getPlayer().getUsername());
    }

    private void registerCommands() {
        jda.updateCommands()
            .addCommands(Commands.slash(
                "playerlist",
                "Lists players online across the network"
            ))
            .queue();
    }
}

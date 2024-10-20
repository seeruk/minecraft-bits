package dev.seeruk.plugin.velocity.discord.chat;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.seeruk.plugin.velocity.discord.event.DiscordReadyEvent;
import net.dv8tion.jda.api.JDA;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(
    id = "seers-discord-chat",
    name = "Seer's Velocity Discord Chat",
    description = "Forwards messages from Seer's Chat to Discord, and vice versa",
    version = BuildConstants.VERSION,
    dependencies = {
        @Dependency(id = "seers-discord")
    }
)
public class ChatPlugin {
    private final Path dataDirectory;
    private final Logger logger;
    private final ProxyServer server;
    private JDA jda;

    @Inject
    public ChatPlugin(@DataDirectory Path dataDirectory, Logger logger, ProxyServer server) {
        this.dataDirectory = dataDirectory;
        this.logger = logger;
        this.server = server;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        logger.info("Initialised successfully");
    }

    @Subscribe
    private void onDiscordReady(DiscordReadyEvent event) {
        this.jda = event.jda();
    }
}

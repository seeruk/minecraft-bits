package dev.seeruk.plugin.velocity.discord.chat;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.seeruk.common.chat.ChatEvent;
import dev.seeruk.common.chat.ChatEventType;
import dev.seeruk.common.config.ConfigManager;
import dev.seeruk.plugin.velocity.discord.DiscordPlugin;
import dev.seeruk.plugin.velocity.discord.chat.config.Config;
import dev.seeruk.plugin.velocity.discord.chat.message.ChatListener;
import dev.seeruk.plugin.velocity.discord.event.DiscordBuildingEvent;
import dev.seeruk.plugin.velocity.discord.event.DiscordReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(
    id = "seers-discord-chat",
    name = "Seer's Velocity Discord Chat",
    description = "Forwards messages from Seer's Chat to Discord, and vice versa",
    authors = {"SeerUK"},
    version = BuildConstants.VERSION,
    dependencies = {
        @Dependency(id = "seers-discord")
    }
)
public class ChatPlugin extends Container {

    @Inject
    public ChatPlugin(@DataDirectory Path dataDirectory, Logger logger, ProxyServer server) {
        setDataDirectory(dataDirectory);
        setLogger(logger);
        setServer(server);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        var configManager = new ConfigManager(getDataDirectory(), getLogger(), this);
        // Overwrite the dist config, so it's always up-to-date
        configManager.saveResource("config.dist.yml", true);
        // Fetch the user-defined config
        setConfig(configManager.getConfigWithDefaults(Config.class).orElseThrow());

        getLogger().info("Initialised successfully");
    }

    @Subscribe
    private void onDiscordBuilding(DiscordBuildingEvent event) {
        var plugin = DiscordPlugin.getInstance();
        var builder = plugin.getJdaBuilder()
            .enableIntents(GatewayIntent.MESSAGE_CONTENT)
            .addEventListeners(this);

        plugin.setJdaBuilder(builder);
    }

    @Subscribe
    private void onDiscordReady(DiscordReadyEvent event) {
        setJda(event.jda());

        var listener = new ChatListener(
            getJda(),
            getLogger(),
            getConfig().discord.channelId,
            getConfig().formats
        );

        getJda().addEventListener(this);

        getRedisConn().addListener(listener);
        getRedisConn().async().subscribe(getConfig().redis.channel);
    }

    @SubscribeEvent
    private void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getMessage().getChannelId().equals(getConfig().discord.channelId)) {
            // Ignore messages from channels we don't care a bout
            return;
        }

        var content = event.getMessage().getContentDisplay();
        if (content.isEmpty()) {
            // Don't care about empty messages
            return;
        }

        if (event.getAuthor().isBot()) {
            // Ignore all bots, including ourselves
            return;
        }

        var proto = ChatEvent.newBuilder()
            .setType(ChatEventType.Discord)
            .setPlayerName(event.getAuthor().getEffectiveName())
            .setMessage(String.format(
                "<blue>Discord ›</blue> %s <gray>»</gray> %s",
                event.getAuthor().getEffectiveName(),
                event.getMessage().getContentDisplay()
            ))
            .build();

        getRedisConn().async().publish(getConfig().redis.channel, proto.toByteArray());
    }
}

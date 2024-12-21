package dev.seeruk.mod.fabric.jq;

import dev.seeruk.common.config.ColorUtil;
import dev.seeruk.common.config.ConfigManager;
import dev.seeruk.mod.fabric.jq.config.Config;
import dev.seeruk.mod.fabric.jq.discord.DiscordContainer;
import dev.seeruk.mod.fabric.jq.discord.DiscordMessenger;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;
import java.util.Random;

public class JqMod implements DedicatedServerModInitializer {
    public static final String MOD_ID = "seers-jq";

    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    private Config config;
    private ConfigManager configManager;

    @Override
    public void onInitializeServer() {
        configManager = new ConfigManager(getConfigFolder(), LOGGER, this, MOD_ID);
        configManager.saveResource("config.dist.yml", true);

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            this.config = configManager.getConfigWithDefaults(Config.class).orElseThrow();
            onServerReady();
        });

        ServerPlayConnectionEvents.JOIN.register((evt, sender, server) -> {
            if (config == null) {
                return;
            }

            this.handleJqEvent(evt.getPlayer(), server, config.onJoin);
        });

        ServerPlayConnectionEvents.DISCONNECT.register((evt, server) -> {
            if (config == null) {
                return;
            }

            this.handleJqEvent(evt.getPlayer(), server, config.onQuit);
        });
    }

    private void onServerReady() {
        LOGGER.info("Initialised");
    }

    private Path getConfigFolder() {
        return FabricLoader.getInstance().getConfigDir().resolve(MOD_ID);
    }

    private void handleJqEvent(
        ServerPlayerEntity player,
        MinecraftServer server,
        Config.MessageConfig messageConfig
    ) {
        var message = getRandomItem(messageConfig.messages);

        var chatMessage = replacePlaceholders(buildMessage(messageConfig.chat, message), player);
        var discordMessage = replacePlaceholders(buildMessage(messageConfig.discord, message), player);

        server.getPlayerManager().getPlayerList().forEach((serverPlayer) ->
            serverPlayer.sendMessage(miniMessage.deserialize(chatMessage))
        );

        if (config.discord.enabled && FabricLoader.getInstance().isModLoaded("seers-discord")) {
            DiscordContainer.getJda().ifPresent(jda -> {
                var messenger = new DiscordMessenger(jda, config.discord.channelId);
                messenger.sendMessageEmbed(player, discordMessage, ColorUtil.getColorByName(messageConfig.discord.colour));
            });
        }
    }

    private String buildMessage(Config.ChatMessageConfig config, String message) {
        return config.prefix + message + config.suffix;
    }

    private String replacePlaceholders(String input, ServerPlayerEntity player) {
        return input.replace("{player}", player.getGameProfile().getName());
    }

    private <T> T getRandomItem(List<T> list) {
        return list.get(new Random().nextInt(list.size()));
    }
}

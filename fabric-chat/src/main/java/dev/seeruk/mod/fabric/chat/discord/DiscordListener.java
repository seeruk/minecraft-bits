package dev.seeruk.mod.fabric.chat.discord;

import dev.seeruk.common.config.ColorUtil;
import dev.seeruk.mod.fabric.chat.ChatMod;
import dev.seeruk.mod.fabric.chat.config.Config;
import dev.seeruk.mod.fabric.chat.event.AdvancementMessageSendCallback;
import dev.seeruk.mod.fabric.chat.event.ChatMessageSendCallback;
import dev.seeruk.mod.fabric.chat.event.DeathMessageSendCallback;
import dev.seeruk.mod.fabric.chat.event.EmoteMessageSendCallback;
import dev.seeruk.mod.fabric.discord.DiscordMod;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.kyori.adventure.platform.modcommon.MinecraftServerAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.awt.*;
import java.util.Optional;

public class DiscordListener {
    private final MinecraftServerAudiences adventure;
    private final Config.DiscordConfig config;
    private final JDA jda;
    private final MinecraftServer server;

    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public DiscordListener(MinecraftServerAudiences adventure, MinecraftServer server, Config.DiscordConfig config) {
        this.adventure = adventure;
        this.config = config;
        this.server = server;

        this.jda = DiscordMod.getInstance().getJda();
        this.jda.addEventListener(this);
    }

    public void register() {
        AdvancementMessageSendCallback.EVENT.register((player, message) -> {
            sendMessageEmbed(formatAdvancement(player, message.getString()));
        });

        ChatMessageSendCallback.EVENT.register((player, formatted, message) -> {
            sendMessage(formatChat(player, message.getString()));
        });

        DeathMessageSendCallback.EVENT.register((player, message) -> {
            sendMessageEmbed(formatDeath(player, message.getString()));
        });

        EmoteMessageSendCallback.EVENT.register((player, formatted, message) -> {
            sendMessage(formatEmote(player, message.getString()));
        });
    }

    public void onStarted() {
        sendMessageEmbed(formatServerStarted());
    }

    public void onStopping() {
        sendMessageEmbed(formatServerStopping());
    }

    @SubscribeEvent
    private void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getMessage().getChannelId().equals(config.channelId)) {
            // We only care about messages from our configured channel
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

        var component = miniMessage.deserialize(String.format(
            "<blue>Discord ›</blue> %s <gray>»</gray> %s",
            event.getAuthor().getEffectiveName(),
            event.getMessage().getContentDisplay()
        ));

        server.getPlayerManager().getPlayerList().forEach(player -> {
            adventure.audience(player).sendMessage(component);
        });
    }

    private void sendMessage(String message) {
        getTextChannel().ifPresent(textChannel -> textChannel.sendMessage(message).setSuppressEmbeds(true).queue());
    }

    private void sendMessageEmbed(MessageEmbed embed) {
        getTextChannel().ifPresent(textChannel -> textChannel.sendMessageEmbeds(embed).queue());
    }

    private Optional<TextChannel> getTextChannel() {
        var textChannel = jda.getTextChannelById(config.channelId);

        if (textChannel == null) {
            ChatMod.LOGGER.warn("Failed to retrieve text channel by ID: {}", config.channelId);
            return Optional.empty();
        }

        return Optional.of(textChannel);
    }

    private MessageEmbed formatAdvancement(ServerPlayerEntity player, String message) {
        var formatted = config.formats.advancement.format
            .replace("{message}", message);

        return formatPlayerEmbed(
            player.getGameProfile().getId().toString(),
            formatted,
            ColorUtil.getColorByName(config.formats.advancement.colour)
        );
    }

    private String formatChat(ServerPlayerEntity player, String message) {
        return config.formats.chat.format
            .replace("{display_name}", player.getGameProfile().getName())
            .replace("{message}", message);
    }

    private MessageEmbed formatDeath(ServerPlayerEntity player, String message) {
        var formatted = config.formats.death.format
            .replace("{message}", message);

        return formatPlayerEmbed(
            player.getGameProfile().getId().toString(),
            formatted,
            ColorUtil.getColorByName(config.formats.death.colour)
        );
    }

    private String formatEmote(ServerPlayerEntity player, String message) {
        return config.formats.emote.format
            .replace("{display_name}", player.getGameProfile().getName())
            .replace("{message}", message);
    }

    private MessageEmbed formatServerStarted() {
        var message = config.formats.serverStarted.format;
        var embed = new EmbedBuilder();

        embed.setColor(ColorUtil.getColorByName(config.formats.serverStarted.colour));
        embed.setAuthor(message);

        return embed.build();
    }

    private MessageEmbed formatServerStopping() {
        var message = config.formats.serverStopping.format;
        var embed = new EmbedBuilder();

        embed.setColor(ColorUtil.getColorByName(config.formats.serverStopping.colour));
        embed.setAuthor(message);

        return embed.build();
    }

    private MessageEmbed formatPlayerEmbed(String playerUuid, String message, Color color) {
        var embed = new EmbedBuilder();

        embed.setColor(color);
        embed.setAuthor(
            message,
            null,
            String.format(
                "https://minotar.net/avatar/%s/64",
                playerUuid.replace("-", "")
            )
        );

        return embed.build();
    }

    private String capitalise(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toTitleCase(str.charAt(0)) +
            str.substring(1);
    }
}

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
import net.minecraft.server.network.ServerPlayerEntity;

import java.awt.*;
import java.util.Optional;

public class DiscordListener {
    private final Config.DiscordConfig config;
    private final JDA jda;

    public DiscordListener(Config.DiscordConfig config) {
        this.config = config;
        this.jda = DiscordMod.getInstance().getJda();
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

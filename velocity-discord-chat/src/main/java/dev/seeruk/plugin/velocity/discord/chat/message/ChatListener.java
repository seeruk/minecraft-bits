package dev.seeruk.plugin.velocity.discord.chat.message;

import com.google.protobuf.InvalidProtocolBufferException;
import dev.seeruk.common.chat.ChatEvent;
import dev.seeruk.plugin.velocity.discord.chat.config.Config;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.slf4j.Logger;

import java.awt.*;

@RequiredArgsConstructor
public class ChatListener extends RedisPubSubAdapter<String, byte[]> {

    private final JDA jda;
    private final Logger logger;
    private final String channelId;
    private final Config.FormatsConfig config;

    /**
     * Handle an incoming message. We expect this to be a join/switch/quit message, ready to be
     * deserialized into a component that can be sent to the whole server.
     */
    @Override
    public void message(String channel, byte[] proto) {
        ChatEvent event;

        try {
            event = ChatEvent.parseFrom(proto);
        } catch (InvalidProtocolBufferException e) {
            logger.warn("Failed to parse chat event proto message", e);
            return;
        }

        var textChannel = jda.getTextChannelById(channelId);

        if (textChannel == null) {
            logger.warn("Failed to retrieve text channel by ID");
            return;
        }

        switch (event.getType()) {
            case Advancement -> textChannel.sendMessageEmbeds(formatAdvancement(event))
                .setSuppressEmbeds(true).queue();
            case Chat -> textChannel.sendMessage(formatChat(event))
                .setSuppressEmbeds(true).queue();
            case Death -> textChannel.sendMessageEmbeds(formatDeath(event))
                .setSuppressEmbeds(true).queue();
            case Discord -> {} // We don't send messages from Discord to Discord...
            case Emote -> textChannel.sendMessage(formatEmote(event))
                .setSuppressEmbeds(true).queue();
            default -> logger.warn("Unrecognised event received");
        }
    }

    private MessageEmbed formatAdvancement(ChatEvent event) {
        return formatPlayerEmbed(event.getPlayerUuid(), event.getMessage(), Color.decode(config.advancement.colour));
    }

    private String formatChat(ChatEvent event) {
        return config.chat.format
            .replace("{display_name}", event.getPlayerName()) // TODO: Actual display name?
            .replace("{message}", event.getMessage());
    }

    private MessageEmbed formatDeath(ChatEvent event) {
        return formatPlayerEmbed(event.getPlayerUuid(), event.getMessage(), Color.decode(config.death.colour));
    }

    private String formatEmote(ChatEvent event) {
        return config.emote.format
            .replace("{display_name}", event.getPlayerName()) // TODO: Actual display name?
            .replace("{message}", event.getMessage());
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
}

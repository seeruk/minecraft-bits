package dev.seeruk.plugin.velocity.jsq.discord;

import com.velocitypowered.api.proxy.Player;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.awt.*;
import java.util.Optional;

public class DiscordMessenger {
    private final JDA jda;
    private final String channelId;

    private static final ComponentSerializer<Component, Component, String> deserializer =
        MiniMessage.miniMessage();

    private static final ComponentSerializer<Component, TextComponent, String> serializer =
        PlainTextComponentSerializer.plainText();

    public DiscordMessenger(JDA jda, String channelId) {
        this.jda = jda;
        this.channelId = channelId;
    }

    public void sendMessageEmbed(Player player, String message, Color color) {
        var embed = new EmbedBuilder();
        var component = deserializer.deserialize(message);

        message = serializer.serialize(component);

        embed.setColor(color);
        embed.setAuthor(
            message,
            null,
            String.format(
                "https://minotar.net/avatar/%s/64",
                player.getUniqueId().toString().replace("-", "")
            )
        );

        Optional.ofNullable(jda.getTextChannelById(channelId))
            .ifPresent(channel -> {
                channel.sendMessageEmbeds(embed.build()).queue();
            });
    }
}

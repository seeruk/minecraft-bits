package dev.seeruk.mod.fabric.discord.command;

import dev.seeruk.mod.fabric.discord.DiscordMod;
import dev.seeruk.mod.fabric.discord.time.TimeFormatter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.minecraft.server.MinecraftServer;

import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class PlayerListCommand {

    private static final String NAME = "playerlist";

    private final DiscordMod mod;
    private final MinecraftServer server;

    public PlayerListCommand(DiscordMod mod, MinecraftServer server) {
        this.mod = mod;
        this.server = server;
    }

    @SubscribeEvent
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals(NAME)) {
            return;
        }

        // Tell people we're thinking...
        event.deferReply().queue();

        var connectedTimes = mod.getPlayerConnectTimes();
        var onlinePlayers = server.getPlayerManager().getPlayerList();
        var maxPlayers = server.getMaxPlayerCount();

        var titleEmbed = new EmbedBuilder();

        titleEmbed.setColor(Color.CYAN);
        titleEmbed.setTitle("Player List");
        titleEmbed.setDescription(String.format(
            "There are %d/%d players online",
            onlinePlayers.size(), maxPlayers
        ));

        var embeds = new ArrayList<>(List.of(titleEmbed.build()));

        var playerListEmbed = new EmbedBuilder();

        if (onlinePlayers.isEmpty()) {
            playerListEmbed.setTitle("There are no players online");
        } else {
            var link = onlinePlayers.size() > 1 ? "are" : "is";
            var noun = onlinePlayers.size() > 1 ? "players" : "player";

            playerListEmbed.setTitle(String.format("There %s %d %s online", link, onlinePlayers.size(), noun));

            var builder = new StringBuilder();
            var iterator = onlinePlayers.iterator();

            while (iterator.hasNext()) {
                var player = iterator.next();
                var username = player.getGameProfile().getName();
                // Add the info we definitely have...
                builder.append(String.format("* **%s**", player.getGameProfile().getName()));
                // Tack on the connected for time
                var connected = connectedTimes.get(username);
                if (connected != null) {
                    var duration = Duration.between(connected, Instant.now());
                    var formatted = TimeFormatter.formatDuration(duration);
                    builder.append(String.format(": playing for %s", formatted));
                }
                // If we still have more to go, add a new line
                if (iterator.hasNext()) {
                    builder.append(System.lineSeparator());
                }
            }

            playerListEmbed.setDescription(builder.toString());
        }

        embeds.add(playerListEmbed.build());

        event.getHook().sendMessageEmbeds(embeds).queue();
    }
}

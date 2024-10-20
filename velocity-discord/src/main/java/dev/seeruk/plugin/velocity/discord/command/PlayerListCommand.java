package dev.seeruk.plugin.velocity.discord.command;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.seeruk.plugin.velocity.discord.DiscordPlugin;
import dev.seeruk.plugin.velocity.discord.time.TimeFormatter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

import javax.annotation.Nonnull;
import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PlayerListCommand {

    private static final String NAME = "playerlist";

    private final DiscordPlugin plugin;
    private final ProxyServer server;

    public PlayerListCommand(DiscordPlugin plugin, ProxyServer server) {
        this.plugin = plugin;
        this.server = server;
    }

    @SubscribeEvent
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        if (!event.getName().equals(NAME)) {
            return;
        }

        // Tell people we're thinking...
        event.deferReply().queue();

        var connectedTimes = plugin.getPlayerConnectTimes();
        var onlinePlayers = server.getAllPlayers();
        var maxPlayers = server.getConfiguration().getShowMaxPlayers();
        var servers = server.getAllServers();

        var titleEmbed = new EmbedBuilder();

        titleEmbed.setColor(Color.CYAN);
        titleEmbed.setTitle("Player List");
        titleEmbed.setDescription(String.format(
            "There are %d/%d players online across %d servers",
            onlinePlayers.size(), maxPlayers, servers.size()
        ));

        var embeds = new ArrayList<>(List.of(titleEmbed.build()));

        // Iterate over each server, in name order.
        servers.stream()
            .sorted(Comparator.comparing(a -> a.getServerInfo().getName()))
            .forEach(server -> {
                var players = server.getPlayersConnected().stream()
                    .sorted(Comparator.comparing(Player::getUsername))
                    .toList();

                var builder = new StringBuilder();
                var embed = new EmbedBuilder();

                var serverName = server.getServerInfo().getName();

                if (players.isEmpty()) {
                    embed.setTitle(String.format("There are no players on %s", serverName));
                    embeds.add(embed.build());
                    return;
                }

                var link = players.size() > 1 ? "are" : "is";
                var noun = players.size() > 1 ? "players" : "player";

                embed.setTitle(String.format("There %s %d %s on %s", link, players.size(), noun, serverName));

                var iterator = players.iterator();

                while (iterator.hasNext()) {
                    var player = iterator.next();
                    // Add the info we definitely have...
                    builder.append(String.format("* **%s**", player.getUsername()));
                    // Tack on the connected for time
                    var connected = connectedTimes.get(player.getUsername());
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

                embed.setDescription(builder.toString());
                embeds.add(embed.build());
            });

        event.getHook().sendMessageEmbeds(embeds).queue();
    }
}

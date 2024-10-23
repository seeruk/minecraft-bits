package dev.seeruk.plugin.velocity.jsq.discord;

import dev.seeruk.plugin.velocity.discord.DiscordPlugin;
import net.dv8tion.jda.api.JDA;

import java.util.Optional;

/**
 * This must be kept in a separate class, which is only used if `seers-discord` is loaded.
 */
public class DiscordContainer {
    public static Optional<JDA> getJda() {
        return Optional.ofNullable(DiscordPlugin.getInstance().getJda());
    }
}

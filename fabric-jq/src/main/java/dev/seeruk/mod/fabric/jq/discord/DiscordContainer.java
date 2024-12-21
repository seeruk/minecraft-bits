package dev.seeruk.mod.fabric.jq.discord;

import dev.seeruk.mod.fabric.discord.DiscordMod;
import net.dv8tion.jda.api.JDA;

import java.util.Optional;

/**
 * This must be kept in a separate class, which is only used if `seers-discord` is loaded.
 */
public class DiscordContainer {
    public static Optional<JDA> getJda() {
        return Optional.ofNullable(DiscordMod.getInstance().getJda());
    }
}

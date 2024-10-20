package dev.seeruk.plugin.velocity.jsq.discord;

import net.dv8tion.jda.api.JDA;

import java.util.Optional;

public class DiscordContainer {
    private static JDA jda;

    public static Optional<JDA> getJda() {
        if (jda != null) {
            return Optional.of(jda);
        }
        return Optional.empty();
    }

    public static void setJda(JDA incoming) {
        jda = incoming;
    }
}

package dev.seeruk.plugin.velocity.discord.event;

import net.dv8tion.jda.api.JDA;

public class DiscordReadyEvent {
    private final JDA jda;

    public DiscordReadyEvent(JDA jda) {
        this.jda = jda;
    }

    public JDA jda() {
        return this.jda;
    }
}

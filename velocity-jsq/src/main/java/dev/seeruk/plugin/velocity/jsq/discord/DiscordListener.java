package dev.seeruk.plugin.velocity.jsq.discord;

import com.velocitypowered.api.event.Subscribe;
import dev.seeruk.plugin.velocity.discord.event.DiscordReadyEvent;

public class DiscordListener {
    @Subscribe
    private void onDiscordReady(DiscordReadyEvent event) {
        DiscordContainer.setJda(event.jda());
    }
}

package dev.seeruk.mod.fabric.discord;

import dev.seeruk.mod.fabric.discord.config.Config;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.minecraft.server.MinecraftServer;

@Getter
@Setter
public abstract class Container {
    private Config config;
    private JDABuilder jdaBuilder;
    private JDA jda;
    private MinecraftServer server;
}

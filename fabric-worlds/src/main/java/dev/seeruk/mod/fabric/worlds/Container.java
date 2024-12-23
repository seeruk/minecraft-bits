package dev.seeruk.mod.fabric.worlds;

import dev.seeruk.mod.fabric.worlds.config.Config;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.MinecraftServer;

@Getter
@Setter
public abstract class Container {
    private Config config;
    private MinecraftServer server;
}

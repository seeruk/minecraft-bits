package dev.seeruk.mod.fabric.grouptoggle;

import dev.seeruk.mod.fabric.grouptoggle.config.Config;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.platform.modcommon.MinecraftServerAudiences;
import net.minecraft.server.MinecraftServer;

@Getter
@Setter
public abstract class Container {
    private Config config;

    private volatile MinecraftServerAudiences adventure;

    private MinecraftServer server;
}

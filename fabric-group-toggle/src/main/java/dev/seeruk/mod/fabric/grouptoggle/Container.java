package dev.seeruk.mod.fabric.grouptoggle;

import dev.seeruk.mod.fabric.grouptoggle.config.Config;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.platform.fabric.FabricServerAudiences;
import net.minecraft.server.MinecraftServer;

@Setter
public class Container {

    @Getter
    private Config config;

    @Getter
    private volatile FabricServerAudiences adventure;

    @Getter
    private MinecraftServer server;
}

package dev.seeruk.mod.fabric.lore;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.platform.modcommon.MinecraftServerAudiences;
import net.minecraft.server.MinecraftServer;

@Getter
@Setter
public abstract class Container {
    private MinecraftServerAudiences adventure;
    private MinecraftServer server;
}

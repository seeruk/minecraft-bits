package dev.seeruk.mod.fabric.worlds;

import dev.seeruk.common.config.ConfigManager;
import dev.seeruk.mod.fabric.worlds.config.Config;
import lombok.Getter;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.nucleoid.fantasy.Fantasy;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;

import java.nio.file.Path;

public class WorldsMod extends Container implements ModInitializer {
    public static final String MOD_ID = "seers-worlds";

    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Getter
    private static WorldsMod instance;

    @Override
    public void onInitialize() {
        instance = this;

        var configManager = new ConfigManager(getConfigFolder(), LOGGER, this, MOD_ID);
        configManager.saveResource("config.dist.yml", true);

        setConfig(configManager.getConfigWithDefaults(Config.class).orElseThrow());

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            setServer(server);
            onServerReady();
        });
    }

    public void onServerReady() {
        LOGGER.info("Initialised");

        var fantasy = Fantasy.get(getServer());
        var worlds = getConfig().worlds;

        if (worlds != null) {
            for (var world : worlds) {
                var config = new RuntimeWorldConfig()
                    .setDimensionType(getDimensionTypeForConfig(world.type))
                    .setDifficulty(getServer().getSaveProperties().getDifficulty())
                    .setGenerator(getGeneratorForConfig(getServer(), world.type))
                    .setSeed(world.seed);

                // We don't need to return this, it just needs to be created or opened.
                fantasy.getOrOpenPersistentWorld(Identifier.of(world.namespace, world.name), config);
            }
        }
    }

    private ChunkGenerator getGeneratorForConfig(MinecraftServer server, Config.WorldType worldType) {
        return switch (worldType) {
            case THE_NETHER -> server.getWorld(World.NETHER).getChunkManager().getChunkGenerator();
            case THE_END -> server.getWorld(World.END).getChunkManager().getChunkGenerator();
            default -> server.getOverworld().getChunkManager().getChunkGenerator();
        };
    }

    private RegistryKey<DimensionType> getDimensionTypeForConfig(Config.WorldType worldType) {
        return switch (worldType) {
            case THE_NETHER -> DimensionTypes.THE_NETHER;
            case THE_END -> DimensionTypes.THE_END;
            default -> DimensionTypes.OVERWORLD;
        };
    }

    private static Path getConfigFolder() {
        return FabricLoader.getInstance().getConfigDir().resolve(MOD_ID);
    }
}

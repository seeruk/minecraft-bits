package dev.seeruk.mod.fabric.resourceworlds;

import dev.seeruk.common.config.ConfigManager;
import dev.seeruk.mod.fabric.resourceworlds.command.ResourceCommand;
import dev.seeruk.mod.fabric.resourceworlds.config.Config;
import dev.seeruk.mod.fabric.resourceworlds.database.Migrator;
import dev.seeruk.mod.fabric.resourceworlds.integration.HuskHomesIntegration;
import lombok.Getter;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.nucleoid.fantasy.Fantasy;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
import xyz.nucleoid.fantasy.RuntimeWorldHandle;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceWorldsMod extends Container implements DedicatedServerModInitializer {

	public static final String MOD_ID = "seers-resource-worlds";

	public static final String WORLD_OVERWORLD = "overworld";
	public static final String WORLD_NETHER = "nether";
	public static final String WORLD_END = "end";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Getter
	private static ResourceWorldsMod instance;

	@Getter
	private final Map<String, RuntimeWorldHandle> worlds = new HashMap<>();

	@Override
	public void onInitializeServer() {
		instance = this;

		ResourceCommand.register();

		var configManager = new ConfigManager(getConfigFolder(), LOGGER, this, MOD_ID);
		configManager.saveResource("config.dist.yml", true);

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			setConfig(configManager.getConfigWithDefaults(Config.class).orElseThrow());
			setServer(server);
			onServerReady();
		});

		ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
			onServerStopping();
		});
	}

	private void onServerReady() {
		var migrator = new Migrator(this.getClass().getClassLoader(), getDatasource());
		migrator.migrate();

		ensureWorldsExist();

		if (FabricLoader.getInstance().isModLoaded("huskhomes")) {
			HuskHomesIntegration.register();
		}

		LOGGER.info("Initialised");
	}

	private void ensureWorldsExist() {
		var fantasy = Fantasy.get(getServer());

		var overworld = new RuntimeWorldConfig()
			.setDimensionType(DimensionTypes.OVERWORLD)
			.setGenerator(getServer().getOverworld().getChunkManager().getChunkGenerator())
			.setSeed(getConfig().dimensions.get(WORLD_OVERWORLD).seed);

		var nether = new RuntimeWorldConfig()
			.setDimensionType(DimensionTypes.THE_NETHER)
			.setGenerator(getServer().getWorld(World.NETHER).getChunkManager().getChunkGenerator())
			.setSeed(getConfig().dimensions.get(WORLD_NETHER).seed);

		var end = new RuntimeWorldConfig()
			.setDimensionType(DimensionTypes.THE_END)
			.setGenerator(getServer().getWorld(World.END).getChunkManager().getChunkGenerator())
			.setSeed(getConfig().dimensions.get(WORLD_END).seed);

		List.of(overworld, nether, end).forEach(config -> {
			config.setDifficulty(getServer().getSaveProperties().getDifficulty());
			config.setMirrorOverworldGameRules(true);
			config.setShouldTickTime(true);
		});

		var overworldHandle = fantasy.getOrOpenPersistentWorld(Identifier.of("resource", "overworld"), overworld);
		var netherHandle = fantasy.getOrOpenPersistentWorld(Identifier.of("resource", "the_nether"), nether);
		var endHandle = fantasy.getOrOpenPersistentWorld(Identifier.of("resource", "the_end"), end);

		worlds.put(WORLD_OVERWORLD, overworldHandle);
		worlds.put(WORLD_NETHER, netherHandle);
		worlds.put(WORLD_END, endHandle);
	}

	private void onServerStopping() {
		// TODO: Anything?
	}

	private static Path getConfigFolder() {
		return FabricLoader.getInstance().getConfigDir().resolve(MOD_ID);
	}
}

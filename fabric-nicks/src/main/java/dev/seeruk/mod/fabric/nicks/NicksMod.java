package dev.seeruk.mod.fabric.nicks;

import dev.seeruk.common.config.ConfigManager;
import dev.seeruk.mod.fabric.nicks.config.Config;
import dev.seeruk.mod.fabric.nicks.database.Migrator;
import lombok.Getter;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.kyori.adventure.platform.fabric.FabricServerAudiences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class NicksMod extends Container implements DedicatedServerModInitializer {

	public static final String MOD_ID = "seers-nicks";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Getter
	private static NicksMod instance;

	@Override
	public void onInitializeServer() {
		var migrator = new Migrator();

        try {
			migrator.runFlyway(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        instance = this;

		var configManager = new ConfigManager(getConfigFolder(), LOGGER, this, MOD_ID);
		configManager.saveResource("config.dist.yml", true);

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			setConfig(configManager.getConfigWithDefaults(Config.class).orElseThrow());
			setAdventure(FabricServerAudiences.of(server));
			setServer(server);
			onServerReady();
		});

		ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
			onServerStopping();
		});
	}

	private void onServerReady() {
		// Listen for Redis messages
		getRedisConn().async().subscribe(config.redisChannel);

		LOGGER.info("Initialised, listening on Redis channel: {}", config.redisChannel);
	}

	private void onServerStopping() {
		getRedisClient().close();
		getRedisConn().close();

		setAdventure(null);
		setRedisClient(null);
		setRedisConn(null);
	}

	private static Path getConfigFolder() {
		return FabricLoader.getInstance().getConfigDir().resolve(MOD_ID);
	}
}
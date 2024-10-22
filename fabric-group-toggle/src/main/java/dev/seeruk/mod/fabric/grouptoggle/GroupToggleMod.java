package dev.seeruk.mod.fabric.grouptoggle;

import dev.seeruk.common.config.ConfigManager;
import dev.seeruk.mod.fabric.grouptoggle.config.Config;
import lombok.Getter;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.kyori.adventure.platform.fabric.FabricServerAudiences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class GroupToggleMod extends Container implements ModInitializer {
	public static final String MOD_ID = "seers-group-toggle";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Getter
	private static GroupToggleMod instance;

	@Override
	public void onInitialize() {
		instance = this;

		// Commands must register early
		GroupsCommand.register();

		var configManager = new ConfigManager(getConfigFolder(), LOGGER, this, MOD_ID);
		configManager.saveResource("config.dist.yml", true);

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			setConfig(configManager.getConfigWithDefaults(Config.class).orElseThrow());
			setAdventure(FabricServerAudiences.of(server));
			setServer(server);
			onServerReady();
		});
	}

	public void onServerReady() {
		LOGGER.info("Initialised");
	}

	private static Path getConfigFolder() {
		return FabricLoader.getInstance().getConfigDir().resolve(MOD_ID);
	}
}

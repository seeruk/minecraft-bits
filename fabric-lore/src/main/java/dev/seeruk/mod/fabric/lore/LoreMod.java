package dev.seeruk.mod.fabric.lore;

import lombok.Getter;
import net.fabricmc.api.DedicatedServerModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.kyori.adventure.platform.modcommon.MinecraftServerAudiences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoreMod extends Container implements DedicatedServerModInitializer {
	public static final String MOD_ID = "seers-lore";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Getter
	private static LoreMod instance;

	@Override
	public void onInitializeServer() {
		instance = this;

		ItemLoreCommand.register();
		ItemNameCommand.register();

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			setAdventure(MinecraftServerAudiences.of(server));
			setServer(server);
			onServerReady();
		});

		LOGGER.info("Initialised");
	}

	private void onServerReady() {
		// ...
	}
}

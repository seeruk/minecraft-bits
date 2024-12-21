package dev.seeruk.mod.fabric.discord;

import dev.seeruk.common.config.ConfigManager;
import dev.seeruk.mod.fabric.discord.command.PlayerListCommand;
import dev.seeruk.mod.fabric.discord.config.Config;
import lombok.Getter;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class DiscordMod extends Container implements ModInitializer {
	public static final String MOD_ID = "seers-discord";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// Keeps track of when each player joined the server.
	@Getter
    private final Map<String, Instant> playerConnectTimes = new HashMap<>();

	@Getter
	private static DiscordMod instance;

	@Override
	public void onInitialize() {
		instance = this;

		var configManager = new ConfigManager(getConfigFolder(), LOGGER, this, MOD_ID);
		configManager.saveResource("config.dist.yml", true);

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			setConfig(configManager.getConfigWithDefaults(Config.class).orElseThrow());
			setServer(server);
			onServerReady();
		});

		ServerPlayConnectionEvents.JOIN.register((evt, sender, server) -> {
			playerConnectTimes.put(evt.player.getGameProfile().getName(), Instant.now());
		});

		ServerPlayConnectionEvents.DISCONNECT.register((evt, server) -> {
			playerConnectTimes.remove(evt.player.getGameProfile().getName());
		});
	}

	public void onServerReady() {
		LOGGER.info("Initialised");

		setJdaBuilder(JDABuilder.createDefault(getConfig().discordToken)
			.setEventManager(new AnnotatedEventManager())
			.addEventListeners(this, new PlayerListCommand(this, getServer())));

		// TODO: Inside event handler.
		setJda(getJdaBuilder().build());
	}

    private static Path getConfigFolder() {
		return FabricLoader.getInstance().getConfigDir().resolve(MOD_ID);
	}

	@SubscribeEvent
	private void onReady(ReadyEvent event) {
		this.registerCommands();
		LOGGER.info("Discord bot is ready!");
	}

	@SubscribeEvent
	private void onShutdown(ShutdownEvent event) {
		LOGGER.info("Discord has shut down");
	}

	private void registerCommands() {
		getJda().updateCommands()
			.addCommands(Commands.slash(
				"playerlist",
				"Lists players online"
			))
			.queue();
	}
}

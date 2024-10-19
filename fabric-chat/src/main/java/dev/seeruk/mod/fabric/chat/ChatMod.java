package dev.seeruk.mod.fabric.chat;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import dev.seeruk.mod.fabric.chat.config.Config;
import dev.seeruk.mod.fabric.chat.message.ChatListener;
import lombok.Getter;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.message.MessageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Optional;

public class ChatMod extends Container implements DedicatedServerModInitializer {
	public static final String MOD_ID = "seers-chat";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static RegistryKey<MessageType> MESSAGE_TYPE_SEER = RegistryKey.of(
		RegistryKeys.MESSAGE_TYPE,
		Identifier.of(MOD_ID, "chat")
	);

	@Getter
	private static ChatMod instance;

	@Override
	public void onInitializeServer() {
		instance = this;

		saveResource("config.dist.yml", true);

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			setConfig(getConfigWithDefaults().orElseThrow());
			setServer(server);
			onServerReady();
		});

		ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
			onServerStopping();
		});
	}

	private void onServerReady() {
		// Listen for Redis messages
		getRedisConn().addListener(new ChatListener(getConfig(), LOGGER, getServer()));
		getRedisConn().async().subscribe(config.redisChannel);

		getChatMessageSendListener().register();

		LOGGER.info("Initialised, listening on Redis channel: {}", config.redisChannel);
	}

	private void onServerStopping() {
		getRedisClient().close();
		getRedisConn().close();
		setRedisClient(null);
		setRedisConn(null);
	}

	private Path getConfigFolder() {
		return FabricLoader.getInstance().getConfigDir().resolve(MOD_ID);
	}

	private Optional<Config> getConfigWithDefaults() {
		var mapper = new YAMLMapper();
		var configFile = getConfigFolder().resolve("config.yml").toFile();
		var defaultFile = Objects.requireNonNull(getResource("config.dist.yml"));

		var config = new Config();

		try {
			mapper.readerForUpdating(config).readValue(defaultFile);
		} catch (IOException e) {
			LOGGER.error("Failed to read default configuration", e);
			return Optional.empty();
		}

		if (configFile.exists()) {
			try {
				mapper.readerForUpdating(config).readValue(configFile);
			} catch (IOException e) {
				LOGGER.warn("Failed to read configuration", e);
			}
		} else {
			LOGGER.warn("No configuration found. Proceeding with defaults. Copy config.dist.yml to customise");
		}

		return Optional.of(config);
	}

	private void saveResource(String resourcePath, boolean replace) {
		try {
			var resource = getResource(resourcePath);
			var target = getConfigFolder().resolve(resourcePath);
			var exists = target.toFile().exists();

			if (!exists || replace) {
				Files.createDirectories(target.getParent());
				Files.copy(Objects.requireNonNull(resource), target, StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException e) {
			LOGGER.error(String.format("Failed to save resource: %s", resourcePath), e);
		}
	}

	private InputStream getResource(String resourcePath) {
		return getClass().getClassLoader().getResourceAsStream(resourcePath);
	}
}

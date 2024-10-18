package dev.seeruk.mod.fabric.jsq;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import dev.seeruk.mod.fabric.jsq.config.Config;
import io.lettuce.core.RedisClient;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Optional;

public class JsqMod implements DedicatedServerModInitializer {
	public static final String MOD_ID = "seers-jsq";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private RedisClient redisClient;
	private StatefulRedisPubSubConnection<String, byte[]> redisConn;
	private MinecraftServer server;

	@Override
	public void onInitializeServer() {
		saveResource("config.dist.yml", true);

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			this.server = server;
			onServerReady();
		});

		ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
			onServerStopping();
		});
	}

	private void onServerReady() {
		var config = getConfigWithDefaults().orElseThrow();

		// Connect to Redis
		redisClient = RedisClient.create(config.redisUri);
		redisConn = redisClient.connectPubSub(RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE));

		// Listen for Redis messages
		redisConn.addListener(new JsqListener(LOGGER, server, MiniMessage.miniMessage()));
		redisConn.async().subscribe(config.redisChannel);

		LOGGER.info("Initialised, listening on Redis channel: {}", config.redisChannel);
	}

	private void onServerStopping() {
		redisClient.close();
		redisConn.close();
		redisClient = null;
		redisConn = null;
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

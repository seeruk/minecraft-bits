package dev.seeruk.mod.fabric.jsq;

import dev.seeruk.common.config.ConfigManager;
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

import java.nio.file.Path;

public class JsqMod implements DedicatedServerModInitializer {
    public static final String MOD_ID = "seers-jsq";

    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private ConfigManager configManager;
    private RedisClient redisClient;
    private StatefulRedisPubSubConnection<String, byte[]> redisConn;
    private MinecraftServer server;

    @Override
    public void onInitializeServer() {
        configManager = new ConfigManager(getConfigFolder(), LOGGER, this, MOD_ID);
        configManager.saveResource("config.dist.yml", true);

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            this.server = server;
            onServerReady();
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            onServerStopping();
        });
    }

    private void onServerReady() {
        var config = configManager.getConfigWithDefaults(Config.class).orElseThrow();

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
}

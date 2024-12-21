package dev.seeruk.mod.fabric.chat;


import dev.seeruk.mod.fabric.chat.config.Config;
import dev.seeruk.mod.fabric.chat.event.RedisPublishListener;
import io.lettuce.core.RedisClient;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.platform.modcommon.MinecraftServerAudiences;
import net.minecraft.server.MinecraftServer;

@Setter
public abstract class Container {
    @Getter
    protected Config config;

    @Getter
    private volatile MinecraftServerAudiences adventure;

    @Getter
    private MinecraftServer server;

    private RedisClient redisClient;
    private StatefulRedisPubSubConnection<String, byte[]> redisConn;

    public RedisPublishListener getRedisPublishListener() {
        return new RedisPublishListener(config, getRedisConn());
    }

    public RedisClient getRedisClient() {
        if (redisClient == null) {
            redisClient = RedisClient.create(config.redisUri);
        }
        return redisClient;
    }

    public StatefulRedisPubSubConnection<String, byte[]> getRedisConn() {
        if (redisConn == null) {
            redisConn = getRedisClient().connectPubSub(RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE));
        }
        return redisConn;
    }
}

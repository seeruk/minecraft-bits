package dev.seeruk.mod.fabric.nicks;


import dev.seeruk.mod.fabric.nicks.config.Config;
import io.lettuce.core.RedisClient;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.platform.fabric.FabricServerAudiences;
import net.minecraft.server.MinecraftServer;

@Setter
public abstract class Container {
    @Getter
    protected Config config;

    @Getter
    private volatile FabricServerAudiences adventure;

    @Getter
    private MinecraftServer server;

    private RedisClient redisClient;
    private StatefulRedisPubSubConnection<String, byte[]> redisConn;

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

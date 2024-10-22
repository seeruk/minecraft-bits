package dev.seeruk.plugin.velocity.discord.chat;

import com.velocitypowered.api.proxy.ProxyServer;
import dev.seeruk.plugin.velocity.discord.chat.config.Config;
import io.lettuce.core.RedisClient;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.JDA;
import org.slf4j.Logger;

import java.nio.file.Path;

@Setter
public class Container {
    @Getter
    private Config config;

    @Getter
    private Path dataDirectory;

    @Getter
    private JDA jda;

    @Getter
    private Logger logger;

    @Getter
    private ProxyServer server;

    private RedisClient redisClient;
    private StatefulRedisPubSubConnection<String, byte[]> redisConn;

    public RedisClient getRedisClient() {
        if (redisClient == null) {
            redisClient = RedisClient.create(config.redis.uri);
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

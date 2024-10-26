package dev.seeruk.mod.fabric.nicks;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.seeruk.mod.fabric.nicks.config.Config;
import dev.seeruk.mod.fabric.nicks.database.MemoryStore;
import dev.seeruk.mod.fabric.nicks.database.MySQLStore;
import io.lettuce.core.RedisClient;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.platform.fabric.FabricServerAudiences;
import net.minecraft.server.MinecraftServer;

import javax.sql.DataSource;

@Setter
public abstract class Container {
    @Getter
    protected Config config;

    @Getter
    private volatile FabricServerAudiences adventure;

    @Getter
    private MinecraftServer server;

    private HikariDataSource dataSource;
    private RedisClient redisClient;
    private StatefulRedisPubSubConnection<String, byte[]> redisConn;
    private MemoryStore store;

    public DataSource getDatasource() {
        if (dataSource == null) {
            var conf = new HikariConfig();

            conf.setJdbcUrl(config.database.url);
            conf.setUsername(config.database.username);
            conf.setPassword(config.database.password);
            conf.addDataSourceProperty("cachePrepStmts", "true");
            conf.addDataSourceProperty("prepStmtCacheSize", "250");
            conf.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            dataSource = new HikariDataSource(conf);
        }
        return dataSource;
    }

    public MemoryStore getStore() {
        if (store == null) {
            store = new MemoryStore(new MySQLStore(getDatasource()));
        }
        return store;
    }

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

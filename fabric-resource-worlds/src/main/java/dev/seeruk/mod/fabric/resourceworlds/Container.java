package dev.seeruk.mod.fabric.resourceworlds;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.seeruk.mod.fabric.resourceworlds.config.Config;
import dev.seeruk.mod.fabric.resourceworlds.database.MySQLStore;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.MinecraftServer;

import javax.sql.DataSource;

@Setter
public abstract class Container {
    @Getter
    protected Config config;

    @Getter
    private MinecraftServer server;

    private HikariDataSource dataSource;

    private MySQLStore store;

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

    public MySQLStore getStore() {
        if (store == null) {
            store = new MySQLStore(getDatasource());
        }

        return store;
    }
}

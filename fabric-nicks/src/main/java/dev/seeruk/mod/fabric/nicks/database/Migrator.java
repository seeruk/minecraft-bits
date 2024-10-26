package dev.seeruk.mod.fabric.nicks.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.seeruk.mod.fabric.nicks.NicksMod;
import org.flywaydb.core.Flyway;

public class Migrator {
    // TODO: Rename entirely, make this all about getting the DB connection or whatever.
    // TODO: We only support MySQL, and we require MySQL. Make note of that?
    public void runFlyway(NicksMod mod) {
        var config = new HikariConfig();

        config.setJdbcUrl("jdbc:mysql://localhost/seers_nicks");
        config.setUsername("TODO");
        config.setPassword("TODO");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        var ds = new HikariDataSource(config);

        var flyway = Flyway.configure(NicksMod.class.getClassLoader())
            .baselineVersion("0")
            .baselineOnMigrate(true)
            .dataSource(ds)
            .table("schema_history")
            .locations("classpath:seers-nicks/migrations")
            .validateMigrationNaming(true)
            .validateOnMigrate(true)
            .load();

        flyway.repair();
        flyway.migrate();
    }
}

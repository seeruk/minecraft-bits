package dev.seeruk.mod.fabric.nicks.database;

import lombok.RequiredArgsConstructor;
import org.flywaydb.core.Flyway;

import javax.sql.DataSource;

@RequiredArgsConstructor
public class Migrator {

    // TODO: Rename entirely, make this all about getting the DB connection or whatever.
    // TODO: We only support MySQL, and we require MySQL. Make note of that?
    // TODO: Split out into a shared lib or something?

    private final ClassLoader classLoader;
    private final DataSource dataSource;

    public void migrate() {
        var flyway = Flyway.configure(classLoader)
            .baselineVersion("0")
            .baselineOnMigrate(true)
            .dataSource(dataSource)
            .table("seers_nicks_schema_history")
            .locations("classpath:seers-nicks/migrations")
            .validateMigrationNaming(true)
            .validateOnMigrate(true)
            .load();

        flyway.repair();
        flyway.migrate();
    }
}

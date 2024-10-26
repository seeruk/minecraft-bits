package dev.seeruk.mod.fabric.nicks.database;

public interface RefreshableStore extends Store {
    void refresh();
}

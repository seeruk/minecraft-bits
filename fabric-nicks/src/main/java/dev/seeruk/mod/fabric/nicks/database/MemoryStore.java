package dev.seeruk.mod.fabric.nicks.database;

import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class MemoryStore implements RefreshableStore {

    private final Store store;

    private final Map<UUID, String> colours = new ConcurrentHashMap<>();
    private final Map<UUID, String> nicks = new ConcurrentHashMap<>();

    @Override
    public Map<UUID, String> getColours() {
        return colours;
    }

    @Override
    public Map<UUID, String> getNicks() {
        return nicks;
    }

    @Override
    public int setColour(UUID playerUuid, String colour) {
        return store.setColour(playerUuid, colour);
    }

    @Override
    public int setNick(UUID playerUuid, String nick) {
        return store.setNick(playerUuid, nick);
    }

    @Override
    public void refresh() {
        colours.putAll(store.getColours());
        nicks.putAll(store.getNicks());
    }
}

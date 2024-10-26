package dev.seeruk.mod.fabric.nicks.database;

import java.util.Map;
import java.util.UUID;

public interface Store {
    Map<UUID, String> getColours();
    Map<UUID, String> getNicks();

    int setColour(UUID playerUuid, String colour);
    int setNick(UUID playerUuid, String nick);
}

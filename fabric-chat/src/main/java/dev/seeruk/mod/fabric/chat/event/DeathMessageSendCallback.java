package dev.seeruk.mod.fabric.chat.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public interface DeathMessageSendCallback {
    Event<DeathMessageSendCallback> EVENT = EventFactory.createArrayBacked(
        DeathMessageSendCallback.class,
        (listeners) -> (player, message) -> {
            for (var listener : listeners) {
                listener.interact(player, message);
            }
        }
    );

    void interact(ServerPlayerEntity player, Text message);
}

package dev.seeruk.mod.fabric.chat.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public interface ChatMessageSendCallback {
    Event<ChatMessageSendCallback> EVENT = EventFactory.createArrayBacked(
        ChatMessageSendCallback.class,
        (listeners) -> (player, formatted, message) -> {
            for (var listener : listeners) {
                listener.interact(player, formatted, message);
            }
        }
    );

    void interact(ServerPlayerEntity player, Text formatted, Text message);
}

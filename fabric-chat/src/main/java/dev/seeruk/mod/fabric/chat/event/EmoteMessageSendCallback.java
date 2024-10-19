package dev.seeruk.mod.fabric.chat.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public interface EmoteMessageSendCallback {
    Event<EmoteMessageSendCallback> EVENT = EventFactory.createArrayBacked(
        EmoteMessageSendCallback.class,
        (listeners) -> (player, formatted, message) -> {
            for (var listener : listeners) {
                listener.interact(player, formatted, message);
            }
        }
    );

    void interact(@NotNull ServerPlayerEntity player, @NotNull Text formatted, @NotNull Text message);
}

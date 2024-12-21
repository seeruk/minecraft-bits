package dev.seeruk.mod.fabric.chat.text;

import dev.seeruk.mod.fabric.chat.ChatMod;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.text.Text;

public class TextUtils {
    public static Text componentAsText(ComponentLike component) {
        return ChatMod.getInstance().getAdventure().asNative(component.asComponent());
    }

    @SuppressWarnings("removal")
    public static Component textAsComponent(Text text) {
        return ChatMod.getInstance().getAdventure().asAdventure(text);
    }

    public static String serialize(Text text) {
        return GsonComponentSerializer.gson().serialize(textAsComponent(text));
    }

    public static ComponentLike deserialize(String text) {
        return GsonComponentSerializer.gson().deserialize(text);
    }
}

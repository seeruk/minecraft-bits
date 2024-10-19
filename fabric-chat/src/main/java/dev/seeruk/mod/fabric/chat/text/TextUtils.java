package dev.seeruk.mod.fabric.chat.text;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.text.Text;

public class TextUtils {
    public static String serialize(Text text) {
        return Text.Serialization.toJsonString(text, DynamicRegistryManager.EMPTY);
    }

    public static Text deserialize(String text) {
        return Text.Serialization.fromJson(text, DynamicRegistryManager.EMPTY);
    }
}

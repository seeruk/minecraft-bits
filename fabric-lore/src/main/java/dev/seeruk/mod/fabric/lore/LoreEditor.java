package dev.seeruk.mod.fabric.lore;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.ArrayList;

public class LoreEditor {

    public static final SimpleCommandExceptionType LINE_NOT_EXISTS_EXCEPTION =
        new SimpleCommandExceptionType(Text.of("That line does not exist"));

    public static final SimpleCommandExceptionType LINE_EXCEEDS_MAXIMUM_EXCEPTION =
        new SimpleCommandExceptionType(Text.of("That line number exceeds the maximum (16)"));

    public static final SimpleCommandExceptionType MAX_LINES_REACHED_EXCEPTION =
        new SimpleCommandExceptionType(Text.of("Maximum lore lines reached"));

    public static int MAX_LINES = 16;

    private static final LegacyComponentSerializer serialiser = LegacyComponentSerializer.legacyAmpersand();

    public static void addLoreLine(ItemStack stack, String lore) throws CommandSyntaxException {
        var itemLore = stack.get(DataComponentTypes.LORE);
        if (itemLore == null) {
            itemLore = new LoreComponent(new ArrayList<>());
        }

        var lines = new ArrayList<>(itemLore.lines());
        if (lines.size() >= MAX_LINES) {
            throw MAX_LINES_REACHED_EXCEPTION.create();
        }

        lines.add(formatLore(lore));

        stack.set(DataComponentTypes.LORE, new LoreComponent(lines));
    }

    public static void insertLoreLine(ItemStack stack, int line, String lore) throws CommandSyntaxException {
        var itemLore = stack.get(DataComponentTypes.LORE);
        if (itemLore == null) {
            itemLore = new LoreComponent(new ArrayList<>());
        }

        var lines = new ArrayList<>(itemLore.lines());

        if ((lines.size() + 1) > MAX_LINES) {
            throw LINE_EXCEEDS_MAXIMUM_EXCEPTION.create();
        }

        if (lines.size() < line) {
            throw LINE_NOT_EXISTS_EXCEPTION.create();
        }

        lines.add(line, formatLore(lore));

        stack.set(DataComponentTypes.LORE, new LoreComponent(lines));
    }

    public static void setLoreLine(ItemStack stack, int line, String lore) throws CommandSyntaxException {
        if (line > MAX_LINES) {
            throw LINE_EXCEEDS_MAXIMUM_EXCEPTION.create();
        }

        var itemLore = stack.get(DataComponentTypes.LORE);
        if (itemLore == null) {
            itemLore = new LoreComponent(new ArrayList<>());
        }

        var lines = new ArrayList<>(itemLore.lines());

        lines.ensureCapacity(line);

        if (lines.size() < line) {
            for (int i = lines.size(); i < line; i++) {
                lines.add(Text.of(" "));
            }
        }

        lines.set(line-1, formatLore(lore));

        stack.set(DataComponentTypes.LORE, new LoreComponent(lines));
    }

    public static void removeLoreLine(ItemStack stack, int line) throws CommandSyntaxException {
        var itemLore = stack.get(DataComponentTypes.LORE);
        if (itemLore == null) {
            itemLore = new LoreComponent(new ArrayList<>());
        }

        var lines = new ArrayList<>(itemLore.lines());

        if (lines.size() < line) {
            throw LINE_NOT_EXISTS_EXCEPTION.create();
        }

        lines.remove(line-1);

        stack.set(DataComponentTypes.LORE, new LoreComponent(lines));
    }

    private static Text formatLore(String input) {
        return LoreMod.getInstance().getAdventure().asNative(
            serialiser.deserialize(input)
        );
    }
}

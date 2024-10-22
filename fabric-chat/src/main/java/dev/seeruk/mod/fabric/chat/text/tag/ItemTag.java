package dev.seeruk.mod.fabric.chat.text.tag;

import dev.seeruk.mod.fabric.chat.text.TextUtils;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Optional;

@RequiredArgsConstructor
public class ItemTag {

    private final ServerPlayerEntity player;

    public Tag createTag(final ArgumentQueue args, final Context ctx) {
        Component component;

        if (args.hasNext()) {
            final var slot = args.pop().value();

            component = TextUtils.textAsComponent(getNumberedSlot(player, slot)
                .or(() -> getEquipmentSlot(player, slot))
                .orElseThrow(() -> ctx.newException("Invalid slot")));
        } else {
            component = TextUtils.textAsComponent(getItemText(getMainhandSlot(player)));
        }

        // Finally, return the tag instance to insert the placeholder!
        return Tag.selfClosingInserting(component);
    }

    private static ItemStack getMainhandSlot(ServerPlayerEntity player) {
        return player.getInventory().getMainHandStack();
    }

    private static Optional<Text> getNumberedSlot(ServerPlayerEntity player, String arg) {
        var inventory = player.getInventory();

        try {
            // Try arg as slot number
            var slot = Integer.parseInt(arg);
            if (slot >= 0 && slot < inventory.size()) {
                return Optional.of(getItemText(inventory.getStack(slot)));
            }
        } catch (NumberFormatException e) {
            // Continue
        }

        return Optional.empty();
    }

    private static Optional<Text> getEquipmentSlot(ServerPlayerEntity player, String arg) {
        try {
            var slot = EquipmentSlot.byName(arg);
            return Optional.of(getItemText(player.getEquippedStack(slot)));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private static Text getItemText(ItemStack stack) {
        if (!stack.isEmpty()) {
            MutableText mutableText = Text.empty()
                .append("[")
                .append(stack.getName())
                .append("]");

            if (stack.contains(DataComponentTypes.CUSTOM_NAME)) {
                // This is probably correct...
                mutableText.formatted(Formatting.ITALIC);
            }

            mutableText.formatted(stack.getRarity().getFormatting());

            mutableText.styled((style) ->
                style.withHoverEvent(new HoverEvent(
                    HoverEvent.Action.SHOW_ITEM,
                    new HoverEvent.ItemStackContent(stack)
                ))
            );

            return mutableText;
        }

        return Text.empty().append(ItemStack.EMPTY.getName());
    }
}

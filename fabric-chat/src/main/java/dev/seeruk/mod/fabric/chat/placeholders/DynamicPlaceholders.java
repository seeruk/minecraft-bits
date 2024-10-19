package dev.seeruk.mod.fabric.chat.placeholders;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderHandler;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.impl.GeneralUtils;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Optional;
import java.util.Random;
import java.util.regex.Pattern;

public class DynamicPlaceholders {

    public static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("(?<!((?<!(\\\\))\\\\))[\\[](?<id>[^{}]+)[\\]]");

    public static Text parseText(ServerPlayerEntity player, Text input) {
        return Placeholders.parseText(
            input,
            PlaceholderContext.of(player),
            PLACEHOLDER_PATTERN,
            DynamicPlaceholders::getPlaceholder
        );
    }

    private static PlaceholderHandler getPlaceholder(String id) {
        return switch (id) {
            case "i", "item" -> ItemPlaceholder::handler;
            case "random" -> RandomPlaceholder::handler;
            default -> null;
        };
    }

    private static class ItemPlaceholder {
        private static PlaceholderResult handler(PlaceholderContext ctx, String arg) {
            var player = ctx.player();
            if (player == null) {
                return PlaceholderResult.invalid("No player");
            }

            if (arg != null && !arg.isEmpty()) {
                return getNumberedSlot(player, arg)
                    .or(() -> getEquipmentSlot(player, arg))
                    .orElse(PlaceholderResult.invalid());
            }

            return itemPlaceholderText(getMainhandSlot(player));
        }

        private static PlaceholderResult itemPlaceholderText(ItemStack stack) {
            return PlaceholderResult.value(Text.empty()
                .append("[")
                .append(GeneralUtils.getItemText(stack, true))
                .append("]"));
        }

        private static ItemStack getMainhandSlot(ServerPlayerEntity player) {
            return player.getInventory().getMainHandStack();
        }

        private static Optional<PlaceholderResult> getNumberedSlot(ServerPlayerEntity player, String arg) {
            var inventory = player.getInventory();

            try {
                // Try arg as slot number
                var slot = Integer.parseInt(arg);
                if (slot >= 0 && slot < inventory.size()) {
                    return Optional.of(itemPlaceholderText(inventory.getStack(slot)));
                }
            } catch (NumberFormatException e) {
                // Continue
            }

            return Optional.empty();
        }

        private static Optional<PlaceholderResult> getEquipmentSlot(ServerPlayerEntity player, String arg) {
            try {
                var slot = EquipmentSlot.byName(arg);
                return Optional.of(itemPlaceholderText(player.getEquippedStack(slot)));
            } catch (IllegalArgumentException e) {
                return Optional.empty();
            }
        }
    }

    private static class RandomPlaceholder {
        private static final Random random = new Random();

        private static PlaceholderResult handler(PlaceholderContext ctx, String arg) {
            if (arg == null) {
                int randomNumber = random.nextInt(10);
                return PlaceholderResult.value(String.valueOf(randomNumber));
            }

            try {
                int randomNumber = random.nextInt(Integer.parseInt(arg));
                return PlaceholderResult.value(String.valueOf(randomNumber));
            } catch (NumberFormatException e) {
                return PlaceholderResult.invalid("Invalid number!");
            }
        }
    }
}

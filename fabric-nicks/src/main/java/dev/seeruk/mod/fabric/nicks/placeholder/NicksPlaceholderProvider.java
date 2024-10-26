package dev.seeruk.mod.fabric.nicks.placeholder;

import dev.seeruk.mod.fabric.nicks.text.PlayerNicknameText;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import net.minecraft.util.Identifier;

import static dev.seeruk.mod.fabric.nicks.NicksMod.MOD_ID;

public class NicksPlaceholderProvider {
    public static void register() {
        var placeholderIdentifier = MOD_ID.replace("-", "_");

        Placeholders.register(Identifier.of(placeholderIdentifier, "display_name"), (ctx, arg) -> {
            if (ctx.player() == null) {
                return PlaceholderResult.invalid("No player!");
            }

            return PlaceholderResult.value(PlayerNicknameText.get(ctx.player()));
        });

        Placeholders.register(Identifier.of(placeholderIdentifier, "display_name_unformatted"), (ctx, arg) -> {
            if (ctx.player() == null) {
                return PlaceholderResult.invalid("No player!");
            }

            return PlaceholderResult.value(PlayerNicknameText.getUnformatted(ctx.player()));
        });
    }
}

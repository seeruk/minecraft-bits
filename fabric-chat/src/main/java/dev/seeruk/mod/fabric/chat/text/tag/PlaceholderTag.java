package dev.seeruk.mod.fabric.chat.text.tag;

import dev.seeruk.mod.fabric.chat.text.TextUtils;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;

@RequiredArgsConstructor
public class PlaceholderTag {

    private final ServerPlayerEntity player;

    public Tag createTag(final ArgumentQueue args, final Context ctx) {
        if (!args.hasNext()) {
            throw ctx.newException("This tag requires at least one argument");
        }

        var elements = new ArrayList<String>();
        while (args.hasNext()) {
            elements.add(args.pop().value());
        }

        // Then get PAPI to parse the placeholder for the given player.
        var text = Placeholders.parseText(
            Text.of("%" + String.join(":", elements) + "%"),
            PlaceholderContext.of(player)
        );

        // Turn it into an actual Component
        final var component = TextUtils.textAsComponent(text);

        // Finally, return the tag instance to insert the placeholder!
        return Tag.selfClosingInserting(component);
    }
}

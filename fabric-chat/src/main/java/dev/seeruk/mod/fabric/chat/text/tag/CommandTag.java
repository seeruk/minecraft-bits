package dev.seeruk.mod.fabric.chat.text.tag;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;

public class CommandTag {

    public static Tag createTag(final ArgumentQueue args, final Context ctx) throws ParsingException {
        var command = args.popOr("This tag requires exactly one argument, the command to suggest").value();

        return Tag.styling(
            NamedTextColor.GOLD,
            TextDecoration.UNDERLINED,
            ClickEvent.suggestCommand("/" + command),
            HoverEvent.showText(Component.text("Suggested command: /" + command))
        );
    }
}

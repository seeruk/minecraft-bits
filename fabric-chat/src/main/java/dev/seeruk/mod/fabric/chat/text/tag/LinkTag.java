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

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

public class LinkTag {

    private static final String defaultScheme = "https";
    private static final List<String> allowedSchemes = List.of("http", "https");

    public static Tag createTag(final ArgumentQueue args, final Context ctx) throws ParsingException {
        var link = args.popOr("This tag requires exactly one argument, the link to open").value();

        link = ensureScheme(link);
        if (!isValidUrl(ctx, link)) {
            throw ctx.newException("Invalid link");
        }

        return Tag.styling(
            NamedTextColor.AQUA,
            TextDecoration.UNDERLINED,
            ClickEvent.openUrl(link),
            HoverEvent.showText(Component.text("Open: " + link))
        );
    }

    private static String ensureScheme(String input) {
        for (var scheme : allowedSchemes) {
            if (input.startsWith(scheme + "://")) {
                return input;
            }
        }
        return defaultScheme + "://" + input;
    }

    private static boolean isValidUrl(Context ctx, String input) {
        try {
            var url = new URL(input).toURI();
            return allowedSchemes.contains(url.getScheme());
        } catch (URISyntaxException | MalformedURLException e) {
            return false;
        }
    }
}

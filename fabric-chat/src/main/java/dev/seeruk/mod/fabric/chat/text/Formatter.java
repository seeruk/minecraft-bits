package dev.seeruk.mod.fabric.chat.text;

import dev.seeruk.mod.fabric.chat.placeholders.DynamicPlaceholders;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.NodeParser;
import eu.pb4.placeholders.api.parsers.ParserBuilder;
import eu.pb4.placeholders.api.parsers.PatternPlaceholderParser;
import eu.pb4.placeholders.api.parsers.TagParser;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Map;

public class Formatter {
    private static final NodeParser safeParser = ParserBuilder.of()
        .markdown()
        .legacyAll()
        .add(TagParser.DEFAULT_SAFE)
        .build();

    private static final NodeParser unsafeParser = ParserBuilder.of()
        .markdown()
        .legacyAll()
        .add(TagParser.DEFAULT)
        .build();

    public static boolean canPlayerUseGlobalPlaceholders(ServerPlayerEntity player) {
        return player.hasPermissionLevel(player.getServer().getOpPermissionLevel());
    }

    /**
     * Apply the given format string to the given text. The format string has access to things like
     * global placeholders. The original text should feature in the format as "{text}" to be
     * interpolated into the pattern as formatted Text too.
     *
     * @param format the format string to use
     * @param player the player to apply placeholders for
     * @param text the original text to wrap in the format
     * @return the resulting Text
     */
    public static Text applyFormat(String format, ServerPlayerEntity player, Text text) {
        var decoratedFormat = decorateText(player, Text.of(format), true);

        return Placeholders.parseText(
            decoratedFormat,
            PatternPlaceholderParser.ALT_PLACEHOLDER_PATTERN_CUSTOM,
            Map.of("text", text)
        );
    }

    /**
     * Apply decoration to some Text. Decoration being Markdown-like, Legacy, and Tag-like formats.
     *
     * @param player the player to apply placeholders for
     * @param text the text to decorate
     * @param allowUnsafe whether to allow unsafe replacements
     * @return the resulting text
     */
    public static Text decorateText(ServerPlayerEntity player, Text text, boolean allowUnsafe) {
        if (allowUnsafe) {
            // This is not always safe, so make it possible to choose!
            text = Placeholders.parseText(text, PlaceholderContext.of(player));
        }

        // Allow all players to use certain placeholders
        text = DynamicPlaceholders.parseText(player, text);

        var parser = allowUnsafe ? unsafeParser : safeParser;

        return parser.parseNode(TextNode.convert(text)).toText();
    }
}

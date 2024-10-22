package dev.seeruk.mod.fabric.chat.text;

import dev.seeruk.mod.fabric.chat.text.tag.CommandTag;
import dev.seeruk.mod.fabric.chat.text.tag.ItemTag;
import dev.seeruk.mod.fabric.chat.text.tag.LinkTag;
import dev.seeruk.mod.fabric.chat.text.tag.PlaceholderTag;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.parsers.PatternPlaceholderParser;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Map;

public class Formatter {
    public static final MiniMessage safeSerializer =
        MiniMessage.builder()
            .tags(TagResolver.builder()
                .resolver(TagResolver.resolver("cmd", CommandTag::createTag))
                .resolver(TagResolver.resolver("command", CommandTag::createTag))
                .resolver(TagResolver.resolver("a", LinkTag::createTag))
                .resolver(TagResolver.resolver("link", LinkTag::createTag))
                .resolver(StandardTags.color())
                .resolver(StandardTags.decorations())
                .resolver(StandardTags.reset())
                .resolver(StandardTags.hoverEvent())
                .resolver(StandardTags.rainbow())
                .resolver(StandardTags.gradient())
                .resolver(StandardTags.transition())
                .resolver(StandardTags.font())
                .build()
            )
            .build();

    public static final MiniMessage unsafeSerializer =
        MiniMessage.builder()
            .tags(TagResolver.builder()
                .resolver(TagResolver.resolver("cmd", CommandTag::createTag))
                .resolver(TagResolver.resolver("command", CommandTag::createTag))
                .resolver(TagResolver.resolver("a", LinkTag::createTag))
                .resolver(TagResolver.resolver("link", LinkTag::createTag))
                .resolver(StandardTags.defaults())
                .build()
            )
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
     * @param text   the original text to wrap in the format
     * @return the resulting Text
     */
    public static Text applyFormat(String format, ServerPlayerEntity player, Text text) {
        var decoratedFormat = decoratePlainText(player, format, true);

        return Placeholders.parseText(
            decoratedFormat,
            PatternPlaceholderParser.ALT_PLACEHOLDER_PATTERN_CUSTOM,
            Map.of("text", text)
        );
    }

    public static Text decoratePlainText(ServerPlayerEntity player, String plainText) {
        return decoratePlainText(player, plainText, canPlayerUseGlobalPlaceholders(player));
    }

    /**
     * Apply decoration to a plain text string. This text string is then returned as native Text.
     *
     * @param player      the player to apply placeholders for
     * @param plainText   the plain text string to decorate
     * @param allowUnsafe whether to allow unsafe replacements
     * @return the resulting text
     */
    public static Text decoratePlainText(ServerPlayerEntity player, String plainText, boolean allowUnsafe) {
        // We need to apply any "String-based" formatting first, because Adventure doesn't support
        // applying formatting to Text, only to String, which will remove any other formatting
        // already done (e.g. by placeholders).
        plainText = TagMarkdownReplacer.DEFAULT.replace(plainText);

        var itemTag = new ItemTag(player);

        // Some tags require the player, so we create them here
        var resolvers = new ArrayList<TagResolver>();
        // "Safe" tags (i.e. for normal players)
        resolvers.add(TagResolver.resolver("item", itemTag::createTag));

        if (allowUnsafe) {
            // Add on "unsafe" tags (e.g. for ops)
            // TODO: This tag might be pointless!
            var placeholderTag = new PlaceholderTag(player);

            resolvers.add(TagResolver.resolver("papi", placeholderTag::createTag));
            resolvers.add(TagResolver.resolver("placeholder", placeholderTag::createTag));
        }

        // The last thing we do with strings before things become Component
        var parser = allowUnsafe ? unsafeSerializer : safeSerializer;
        var text = TextUtils.componentAsText(parser.deserialize(plainText, resolvers.toArray(TagResolver[]::new)));

        // Now we have native Text, we must keep it that way.
        if (allowUnsafe) {
            // This is not always safe, so make it possible to choose!
            text = Placeholders.parseText(text, PlaceholderContext.of(player));
        }

        return text;
    }
}

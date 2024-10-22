package dev.seeruk.mod.fabric.chat.text;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static java.util.Map.entry;

public class TagMarkdownReplacer implements Replacer {

    public static TagMarkdownReplacer DEFAULT = new TagMarkdownReplacer();

    private final List<Map.Entry<Pattern, String>> replacements = List.of(
        // The ordering of these entries is very important!
        entry(Pattern.compile("\\[item (.*?)]"), "<item:$1>"),
        entry(Pattern.compile("\\[item]"), "<item>"),
        entry(Pattern.compile("\\[(.*?)]\\((.*?)\\)"), "<link:$2>$1</link>"),
        entry(Pattern.compile("\\[/(.*?)]"), "<command:$1>/$1</command>"),
        entry(Pattern.compile("\\*\\*\\*(.+?)\\*\\*\\*"), "<bold><italic>$1</italic></bold>"),
        entry(Pattern.compile("\\*\\*(.+?)\\*\\*"), "<bold>$1</bold>"),
        entry(Pattern.compile("__(.+?)__"), "<underlined>$1</underlined>"),
        entry(Pattern.compile("\\?\\?(.+?)\\?\\?"), "<obfuscated>$1</obfuscated>"),
        entry(Pattern.compile("~~(.+?)~~"), "<strikethrough>$1</strikethrough>"),
        entry(Pattern.compile("\\*(.+?)\\*"), "<italic>$1</italic>")
    );

    @Override
    public String replace(String input) {
        for (var entry : replacements) {
            input = entry.getKey().matcher(input).replaceAll(entry.getValue());
        }

        return input;
    }
}

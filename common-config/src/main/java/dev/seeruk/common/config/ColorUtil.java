package dev.seeruk.common.config;

import java.awt.*;
import java.util.Map;

import static java.util.Map.entry;

public class ColorUtil {
    private static final Map<String, Color> colours = Map.ofEntries(
        entry("white", Color.white),
        entry("lightGray", Color.lightGray),
        entry("gray", Color.gray),
        entry("darkGray", Color.darkGray),
        entry("black", Color.black),
        entry("red", Color.red),
        entry("pink", Color.pink),
        entry("orange", Color.orange),
        entry("yellow", Color.yellow),
        entry("green", Color.green),
        entry("magenta", Color.magenta),
        entry("cyan", Color.cyan),
        entry("blue", Color.blue)
    );

    public static Color getColorByName(String name) {
        if (colours.containsKey(name)) {
            return colours.get(name);
        }
        return Color.decode(name);
    }
}

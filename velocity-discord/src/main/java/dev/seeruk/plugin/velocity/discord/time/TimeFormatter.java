package dev.seeruk.plugin.velocity.discord.time;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class TimeFormatter {
    public static String formatDuration(Duration duration) {
        // Get the total time in respective units
        List<String> parts = getParts(duration);

        // If the list is empty, return "0 seconds" to indicate an empty duration
        if (parts.isEmpty()) {
            return "0 seconds";
        }

        // Join the parts together
        String result = String.join(", ", parts);

        // Replace the last comma with "and" if there's more than one part
        int lastCommaIndex = result.lastIndexOf(", ");
        if (lastCommaIndex != -1) {
            result = result.substring(0, lastCommaIndex) + " and" + result.substring(lastCommaIndex + 1);
        }

        return result;
    }

    private static @NotNull List<String> getParts(Duration duration) {
        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;

        // Create a list to hold the parts of the duration
        List<String> parts = new ArrayList<>();

        // Add each part if it's greater than 0
        if (days > 0) {
            parts.add(days + " day" + (days > 1 ? "s" : ""));
        }
        if (hours > 0) {
            parts.add(hours + " hour" + (hours > 1 ? "s" : ""));
        }
        if (minutes > 0) {
            parts.add(minutes + " minute" + (minutes > 1 ? "s" : ""));
        }
        if (seconds > 0) {
            parts.add(seconds + " second" + (seconds > 1 ? "s" : ""));
        }

        return parts;
    }
}

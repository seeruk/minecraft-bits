package dev.seeruk.mod.fabric.chat.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Config {
    public DiscordConfig discord;

    public String displayNameFormat;
    public String chatFormat;
    public String emoteFormat;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DiscordConfig {
        public boolean enabled;
        public String channelId;
        public DiscordFormatsConfig formats;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DiscordFormatsConfig {
        public DiscordMessageFormatConfig chat;
        public DiscordMessageFormatConfig emote;
        public DiscordEmbedFormatConfig advancement;
        public DiscordEmbedFormatConfig death;
        public DiscordEmbedFormatConfig serverStarted;
        public DiscordEmbedFormatConfig serverStopping;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DiscordMessageFormatConfig {
        public String format;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DiscordEmbedFormatConfig extends DiscordMessageFormatConfig {
        public String colour;
    }
}

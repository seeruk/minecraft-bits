package dev.seeruk.plugin.velocity.discord.chat.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class Config {
    public RedisConfig redis;
    public DiscordConfig discord;
    public FormatsConfig formats;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RedisConfig {
        public String uri;
        public String channel;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DiscordConfig {
        public String channelId;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FormatsConfig {
        public MessageFormatConfig chat;
        public MessageFormatConfig emote;
        public MessageEmbedFormatConfig advancement;
        public MessageEmbedFormatConfig death;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MessageFormatConfig {
        public String format;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MessageEmbedFormatConfig {
        public String colour;
    }
}

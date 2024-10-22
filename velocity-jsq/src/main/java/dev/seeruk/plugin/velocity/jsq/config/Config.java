package dev.seeruk.plugin.velocity.jsq.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Config {
    public RedisConfig redis;
    public DiscordConfig discord;

    @JsonProperty("join")
    public MessageConfig onJoin;

    @JsonProperty("switch")
    public MessageConfig onSwitch;

    @JsonProperty("quit")
    public MessageConfig onQuit;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RedisConfig {
        public String uri;
        public String channel;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DiscordConfig {
        public boolean enabled;
        public String channelId;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MessageConfig {
        public ChatMessageConfig chat;
        public DiscordMessageConfig discord;
        public List<String> messages;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChatMessageConfig {
        public String prefix;
        public String suffix;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DiscordMessageConfig extends ChatMessageConfig {
        public String colour;
    }
}

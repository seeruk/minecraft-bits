package dev.seeruk.mod.fabric.jq.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Config {
    public DiscordConfig discord;

    @JsonProperty("join")
    public MessageConfig onJoin;

    @JsonProperty("quit")
    public MessageConfig onQuit;

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

package dev.seeruk.mod.fabric.chat.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Config {
    /**
     * The name of the server. Used while sharing messages across servers to ensure that messages
     * aren't displayed twice on the origin server, etc.
     */
    public String server;

    /**
     * The Redis connection string, used by Lettuce.
     */
    public String redisUri;

    /**
     * The name of a Redis channel to listen for chat messages on.
     */
    public String redisChannel;

    /**
     * The format used for a player's display name.
     */
    public String displayNameFormat;

    /**
     * The format used for all normal chat messages. This format will be sent to other servers too.
     * Formatting must be done on the target server (placeholders are handled in advance).
     */
    public String chatFormat;

    /**
     * The format used for all emote chat messages. This format will be sent to other servers too.
     * Formatting must be done on the target server (placeholders are handled in advance).
     */
    public String emoteFormat;
}

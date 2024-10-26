package dev.seeruk.mod.fabric.nicks.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Config {
    // TODO: Database bits.

    /**
     * The Redis connection string, used by Lettuce.
     */
    public String redisUri;

    /**
     * The name of a Redis channel to listen for chat messages on.
     */
    public String redisChannel;
}

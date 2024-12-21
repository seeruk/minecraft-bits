package dev.seeruk.mod.fabric.jsq.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Config {
    public String redisUri;
    public String redisChannel;

    public boolean sendMessages;
}

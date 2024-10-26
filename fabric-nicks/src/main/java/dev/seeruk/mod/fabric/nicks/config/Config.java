package dev.seeruk.mod.fabric.nicks.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Config {

    public DatabaseConfig database;
    public RedisConfig redis;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DatabaseConfig {
        public String url;
        public String username;
        public String password;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RedisConfig {
        public String uri;
        public String channel;
    }
}

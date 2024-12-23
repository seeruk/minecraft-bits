package dev.seeruk.mod.fabric.resourceworlds.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Config {

    public DatabaseConfig database;

    public Map<String, WorldConfig> dimensions;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DatabaseConfig {
        public String url;
        public String username;
        public String password;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WorldConfig {
        public Coordinates spawn;
        public Long seed;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Coordinates {
        public double x;
        public double y;
        public double z;
    }
}

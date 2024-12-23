package dev.seeruk.mod.fabric.worlds.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Config {
    public WorldConfig[] worlds;

    public static class WorldConfig {
        public String namespace;
        public String name;
        public WorldType type;
        public Long seed;
    }

    public enum WorldType {
        OVERWORLD,
        THE_NETHER,
        THE_END
    }
}

package dev.seeruk.common.config;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Optional;

public class ConfigManager {
    private final Path dataDir;
    private final Logger logger;
    private final Object plugMod;

    public ConfigManager(Path dataDir, Logger logger, Object plugMod) {
        this.dataDir = dataDir;
        this.logger = logger;
        this.plugMod = plugMod;
    }

    public <T> Optional<T> getConfigWithDefaults(Class<T> target) {
        return getConfigWithDefaults(target, "config.yml", "config.dist.yml");
    }

    public <T> Optional<T> getConfigWithDefaults(Class<T> target, String configFileName, String defaultsFileName) {
        var mapper = new YAMLMapper();
        var configFile = dataDir.resolve(configFileName).toFile();

        var config = createInstance(target);

        if (!defaultsFileName.isEmpty()) {
            var defaultFile = Objects.requireNonNull(getResource(defaultsFileName));

            try {
                mapper.readerForUpdating(config).readValue(defaultFile);
            } catch (IOException e) {
                logger.warn("Failed to read default configuration", e);
            }
        }

        if (configFile.exists()) {
            try {
                mapper.readerForUpdating(config).readValue(configFile);
            } catch (IOException e) {
                logger.warn("Failed to read configuration", e);
            }
        } else if (!defaultsFileName.isEmpty()) {
            logger.warn("No configuration found. Proceeding with defaults. Copy config.dist.yml to customise");
        }

        return Optional.of(config);
    }

    public void saveResource(String resourcePath, boolean replace) {
        try {
            var resource = getResource(resourcePath);
            var target = dataDir.resolve(resourcePath);
            var exists = target.toFile().exists();

            if (!exists || replace) {
                Files.createDirectories(target.getParent());
                Files.copy(Objects.requireNonNull(resource), target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            logger.error(String.format("Failed to save resource: %s", resourcePath), e);
        }
    }

    private InputStream getResource(String resourcePath) {
        return plugMod.getClass().getClassLoader().getResourceAsStream(resourcePath);
    }

    private <T> T createInstance(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create new config instance");
        }
    }
}

package dev.seeruk.mod.fabric.grouptoggle.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Config {
    /**
     * A list of groups allowed to be managed by users via this mod.
     */
    public List<String> groups;

}

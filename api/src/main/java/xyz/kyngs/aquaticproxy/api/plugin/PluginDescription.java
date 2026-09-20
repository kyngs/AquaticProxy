package xyz.kyngs.aquaticproxy.api.plugin;

import xyz.kyngs.aquaticproxy.api.util.SemanticVersion;

public record PluginDescription(
        String id,
        String friendlyName,
        String[] authors,
        SemanticVersion version,
        Class<? extends Plugin> pluginClass,
        Runnable unregisterCallback) {
}

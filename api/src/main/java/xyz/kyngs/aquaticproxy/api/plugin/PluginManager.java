package xyz.kyngs.aquaticproxy.api.plugin;

import xyz.kyngs.aquaticproxy.api.ResourceOwner;

import java.util.Collection;

public interface PluginManager {

    Collection<RegisteredPlugin> getRegisteredPlugins();

    RegisteredPlugin getRegisteredPlugin(String id);

    Plugin getPlugin(String id);

    RegisteredPlugin registerPlugin(ResourceOwner parent, PluginDescription plugin);

    Plugin loadPlugin(RegisteredPlugin plugin);

    void enablePlugin(RegisteredPlugin plugin);

    void disablePlugin(RegisteredPlugin plugin);

    void unloadPlugin(RegisteredPlugin plugin);
}

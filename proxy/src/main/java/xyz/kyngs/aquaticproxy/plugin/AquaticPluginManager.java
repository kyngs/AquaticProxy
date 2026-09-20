package xyz.kyngs.aquaticproxy.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.kyngs.aquaticproxy.api.ResourceOwner;
import xyz.kyngs.aquaticproxy.api.plugin.Plugin;
import xyz.kyngs.aquaticproxy.api.plugin.PluginDescription;
import xyz.kyngs.aquaticproxy.api.plugin.PluginManager;
import xyz.kyngs.aquaticproxy.api.plugin.RegisteredPlugin;
import xyz.kyngs.aquaticproxy.module.AquaticModuleManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarFile;

public class AquaticPluginManager implements PluginManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(AquaticPluginManager.class);
    private final Map<String, RegisteredPlugin> registeredPlugins;
    private final AquaticModuleManager moduleManager;

    public AquaticPluginManager(AquaticModuleManager moduleManager) {
        this.moduleManager = moduleManager;
        registeredPlugins = new ConcurrentHashMap<>();
    }

    public int loadPlugins(ResourceOwner parent) {
        var plugins = getRegisteredPlugins().stream()
                .filter(plugin -> parent.equals(plugin.getParent()) && plugin.getState() == RegisteredPlugin.State.INSTANTIATED)
                .toList();

        plugins.forEach(this::loadPlugin);

        return plugins.size();
    }

    public int enablePlugins(ResourceOwner parent) {
        var plugins = getRegisteredPlugins().stream()
                .filter(plugin -> parent.equals(plugin.getParent()) && plugin.getState() == RegisteredPlugin.State.LOADED)
                .toList();

        plugins.forEach(this::enablePlugin);

        return plugins.size();
    }

    public int disablePlugins(ResourceOwner parent) {
        var plugins = getRegisteredPlugins().stream()
                .filter(plugin -> parent.equals(plugin.getParent()) && plugin.getState() == RegisteredPlugin.State.ENABLED)
                .toList();

        plugins.forEach(this::disablePlugin);

        return plugins.size();
    }

    public int unloadPlugins(ResourceOwner parent) {
        var plugins = getRegisteredPlugins().stream()
                .filter(plugin -> parent.equals(plugin.getParent()) && plugin.getState() == RegisteredPlugin.State.LOADED)
                .toList();

        plugins.forEach(this::unloadPlugin);

        return plugins.size();
    }

    @Override
    public Collection<RegisteredPlugin> getRegisteredPlugins() {
        return List.copyOf(registeredPlugins.values());
    }

    @Override
    public RegisteredPlugin getRegisteredPlugin(String id) {
        return registeredPlugins.get(id);
    }

    @Override
    public Plugin getPlugin(String id) {
        return getRegisteredPlugin(id) != null ?
                getRegisteredPlugin(id).getInstance() : null;
    }

    @Override
    public RegisteredPlugin registerPlugin(ResourceOwner parent, PluginDescription plugin) {
        return registeredPlugins.computeIfAbsent(plugin.id(), _ -> {
            try {
                return new RegisteredPlugin(parent, plugin, instantiatePlugin(plugin), RegisteredPlugin.State.INSTANTIATED);
            } catch (Exception e) {
                throw new RuntimeException("Failed to register plugin: " + plugin.id(), e);
            }
        });
    }

    @Override
    public Plugin loadPlugin(RegisteredPlugin plugin) {
        try {
            plugin.getStateLock().writeLock().lock();
            if (plugin.getState() != RegisteredPlugin.State.INSTANTIATED) {
                throw new IllegalStateException("Plugin must be in INSTANTIATED state to load.");
            }
            plugin.getInstance().load(moduleManager);
            plugin.setState(RegisteredPlugin.State.LOADED);
            return plugin.getInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load plugin: " + plugin.getDescription().id(), e);
        } finally {
            plugin.getStateLock().writeLock().unlock();
        }
    }

    @Override
    public void enablePlugin(RegisteredPlugin plugin) {
        try {
            plugin.getStateLock().writeLock().lock();
            if (plugin.getState() != RegisteredPlugin.State.LOADED) {
                throw new IllegalStateException("Plugin must be in LOADED state to enable.");
            }
            plugin.getInstance().enable();
            plugin.setState(RegisteredPlugin.State.ENABLED);
        } catch (Exception e) {
            throw new RuntimeException("Failed to enable plugin: " + plugin.getDescription().id(), e);
        } finally {
            plugin.getStateLock().writeLock().unlock();
        }
    }

    @Override
    public void disablePlugin(RegisteredPlugin plugin) {
        try {
            plugin.getStateLock().writeLock().lock();
            if (plugin.getState() != RegisteredPlugin.State.ENABLED) {
                throw new IllegalStateException("Plugin must be in ENABLED state to disable.");
            }
            plugin.getInstance().disable();
            plugin.setState(RegisteredPlugin.State.LOADED);
        } catch (Exception e) {
            throw new RuntimeException("Failed to disable plugin: " + plugin.getDescription().id(), e);
        } finally {
            plugin.getStateLock().writeLock().unlock();
        }
    }

    @Override
    public void unloadPlugin(RegisteredPlugin plugin) {
        try {
            plugin.getStateLock().writeLock().lock();
            if (plugin.getState() != RegisteredPlugin.State.LOADED) {
                throw new IllegalStateException("Plugin must be in LOADED state to unload.");
            }
            plugin.getInstance().unload(moduleManager);
            plugin.setState(RegisteredPlugin.State.INSTANTIATED);
        } catch (Exception e) {
            throw new RuntimeException("Failed to unload plugin: " + plugin.getDescription().id(), e);
        } finally {
            plugin.getStateLock().writeLock().unlock();
        }
    }

    private Plugin instantiatePlugin(PluginDescription description) {
        try {
            var clazz = description.pluginClass();

            if (!Plugin.class.isAssignableFrom(clazz)) {
                throw new IllegalArgumentException("Plugin class must implement Plugin interface.");
            }

            var constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);

            return constructor.newInstance();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Plugin class must have a no-argument constructor.", e);
        } catch (Exception e) {
            if (e instanceof RuntimeException r) throw r;
            throw new RuntimeException("Failed to instantiate plugin: " + description.id(), e);
        }
    }

    public void discoverPlugins(ResourceOwner parent) {
        //noinspection ResultOfMethodCallIgnored; best effort
        new File("plugins").mkdirs();
        var plugins = JarPluginLoader.discoverJarPlugins(new File("plugins"));

        for (var plugin : plugins) {
            try {
                registerPlugin(parent, plugin);
            } catch (RuntimeException e) {
                LOGGER.error("Failed to register plugin: {}", plugin.id(), e);
            }
        }
    }
}

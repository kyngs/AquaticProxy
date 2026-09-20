package xyz.kyngs.aquaticproxy.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.kyngs.aquaticproxy.api.plugin.Plugin;
import xyz.kyngs.aquaticproxy.api.plugin.PluginDescription;
import xyz.kyngs.aquaticproxy.api.util.SemanticVersion;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.jar.JarFile;

public class JarPluginLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(JarPluginLoader.class);

    public static Collection<PluginDescription> discoverJarPlugins(File directory) {
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IllegalArgumentException("Directory does not exist or is not a directory: " + directory.getAbsolutePath());
        }

        var files = directory.listFiles((_, name) -> name.toLowerCase().endsWith(".jar"));
        if (files == null || files.length == 0) return List.of();

        var discoveredPlugins = new ArrayList<PluginDescription>();

        for (var file : files) {
            try {
                var jarFile = new JarFile(file); // Cannot use try-with-resources here because we need to keep the jar open for the classloader
                var manifest = jarFile.getManifest();

                var pluginId = manifest.getMainAttributes().getValue("Plugin-Id");
                var pluginName = manifest.getMainAttributes().getValue("Plugin-Name");
                var pluginVersion = manifest.getMainAttributes().getValue("Plugin-Version");
                var pluginMainClass = manifest.getMainAttributes().getValue("Plugin-Main-Class");
                var pluginAuthors = manifest.getMainAttributes().getValue("Plugin-Authors");

                if (pluginId == null || pluginVersion == null || pluginMainClass == null) {
                    LOGGER.warn("Jar file {} is missing required plugin attributes in its manifest. Skipping.", file.getAbsolutePath());
                    jarFile.close();
                    continue;
                }

                if (pluginName == null) {
                    pluginName = pluginId;
                }

                if (pluginAuthors == null) {
                    pluginAuthors = "";
                }

                SemanticVersion version;
                try {
                    version = SemanticVersion.parse(pluginVersion);
                } catch (IllegalArgumentException e) {
                    LOGGER.warn("Invalid version format {} for plugin {} in jar file {}. Skipping.", pluginVersion, pluginId, file.getAbsolutePath());
                    jarFile.close();
                    continue;
                }

                var classLoader = new URLClassLoader(new URL[] {file.toURI().toURL()});
                Class<? extends Plugin> mainClass;
                try {
                    var tempClass = classLoader.loadClass(pluginMainClass);
                    if (!Plugin.class.isAssignableFrom(tempClass)) {
                        LOGGER.warn("Main class {} for plugin {} does not implement Plugin interface. Skipping.", pluginMainClass, pluginId);
                        classLoader.close();
                        continue;
                    }
                    mainClass = (Class<? extends Plugin>) tempClass;
                } catch (ClassNotFoundException e) {
                    LOGGER.warn("Main class {} for plugin {} not found in jar file {}. Skipping.", pluginMainClass, pluginId, file.getAbsolutePath());
                    classLoader.close();
                    continue;
                }

                discoveredPlugins.add(
                        new PluginDescription(pluginId, pluginName, pluginAuthors.split(","), version, mainClass, () -> {
                            try {
                                classLoader.close();
                            } catch (IOException e) {
                                LOGGER.error("Failed to close classloader for plugin {}: {}", pluginId, e.getMessage(), e);
                            }
                        })
                );
            } catch (IOException e) {
                throw new RuntimeException("Failed to open jar file: " + file.getAbsolutePath(), e);
            }
        }

        return discoveredPlugins;
    }
}

package xyz.kyngs.aquaticproxy.configuration;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.MergeRule;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.kyngs.aquaticproxy.api.configuration.ConfigurationModule;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Objects;

public class AquaticConfigurationModule implements ConfigurationModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(AquaticConfigurationModule.class);
    public static final String CONFIG_FILE_NAME = "aquaticproxy.yaml";

    public static final LoaderSettings LOADER_SETTINGS = LoaderSettings.builder()
            .setAutoUpdate(true)
            .build();

    public static final UpdaterSettings UPDATER_SETTINGS = UpdaterSettings.builder()
            .setKeepAll(true)
            .build();

    private YamlDocument config;

    @Override
    public void load() {
        LOGGER.info("Loading configuration from {}", CONFIG_FILE_NAME);
        try {
            config = YamlDocument.create(
                    new File(CONFIG_FILE_NAME),
                    Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(CONFIG_FILE_NAME)),
                    LOADER_SETTINGS, UPDATER_SETTINGS
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void save() {
        if (config != null) {
            try {
                config.save();
            } catch (IOException e) {
                LOGGER.error("Failed to save configuration to {}", CONFIG_FILE_NAME, e);
            }
        }
    }

    @Override
    public InetSocketAddress getBindAddress() {
        return new InetSocketAddress(
                config.getString("network.bind"),
                config.getInt("network.port")
        );
    }

    @Override
    public int getCompressionThreshold() {
        return config.getInt("network.compression-threshold");
    }

}

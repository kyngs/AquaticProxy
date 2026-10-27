package xyz.kyngs.aquaticproxy.api.configuration;

import net.kyori.adventure.key.Key;
import xyz.kyngs.aquaticproxy.api.module.Module;
import xyz.kyngs.aquaticproxy.api.module.ModuleKey;

import java.net.InetSocketAddress;

public interface ConfigurationModule extends Module {
    ModuleKey<ConfigurationModule> KEY = new ModuleKey<>(Key.key("aquaticproxy", "configuration"), ConfigurationModule.class);

    default int getCompressionThreshold() {
        return 256;
    }

    default InetSocketAddress getBindAddress() {
        return new InetSocketAddress(25565);
    }
}

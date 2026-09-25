package xyz.kyngs.aquaticproxy.api.network.protocol.packet.configuration;

import net.kyori.adventure.text.Component;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.shared.DisconnectPacket;

public class ConfigurationDisconnectPacket extends DisconnectPacket {
    public ConfigurationDisconnectPacket(Component reason) {
        super(reason);
    }
}

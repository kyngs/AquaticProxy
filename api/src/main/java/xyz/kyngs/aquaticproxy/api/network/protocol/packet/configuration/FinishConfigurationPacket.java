package xyz.kyngs.aquaticproxy.api.network.protocol.packet.configuration;

import xyz.kyngs.aquaticproxy.api.network.protocol.packet.NoBodyPacket;

public class FinishConfigurationPacket implements NoBodyPacket {
    public static final FinishConfigurationPacket INSTANCE = new FinishConfigurationPacket();
}

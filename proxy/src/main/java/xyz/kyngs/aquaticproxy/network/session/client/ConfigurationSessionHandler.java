package xyz.kyngs.aquaticproxy.network.session.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.kyngs.aquaticproxy.api.network.protocol.PacketHandler;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolState;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.login.LoginStartPacket;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.login.LoginSuccessPacket;
import xyz.kyngs.aquaticproxy.api.network.session.ClientSessionHandler;
import xyz.kyngs.aquaticproxy.api.util.GameProfile;
import xyz.kyngs.aquaticproxy.network.AquaticClientConnection;

import java.util.List;
import java.util.UUID;

public class ConfigurationSessionHandler implements ClientSessionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationSessionHandler.class);

    private final AquaticClientConnection connection;

    public ConfigurationSessionHandler(AquaticClientConnection connection) {
        super();
        this.connection = connection;
    }

    @Override
    public ProtocolState getProtocolState() {
        return ProtocolState.CONFIGURATION;
    }

    @Override
    public void activate() {

    }

    @Override
    public void deactivate() {

    }

}

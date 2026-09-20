package xyz.kyngs.aquaticproxy.network.session.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.kyngs.aquaticproxy.api.module.ModuleManager;
import xyz.kyngs.aquaticproxy.api.network.protocol.PacketHandler;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolState;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolVersion;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.handshake.HandshakePacket;
import xyz.kyngs.aquaticproxy.api.network.session.ClientSessionHandler;
import xyz.kyngs.aquaticproxy.api.status.ServerStatusModule;
import xyz.kyngs.aquaticproxy.module.AquaticModuleManager;
import xyz.kyngs.aquaticproxy.network.AquaticClientConnection;

public class HandshakeSessionHandler implements ClientSessionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(HandshakeSessionHandler.class);

    private final AquaticClientConnection connection;
    private final AquaticModuleManager moduleManager;

    public HandshakeSessionHandler(AquaticClientConnection connection, AquaticModuleManager moduleManager) {
        super();
        this.connection = connection;
        this.moduleManager = moduleManager;
    }

    @Override
    public ProtocolState getProtocolState() {
        return ProtocolState.HANDSHAKE;
    }

    @Override
    public void activate() {

    }

    @Override
    public void deactivate() {

    }

    @Override
    public PacketHandler.Result handle(HandshakePacket packet) {
        LOGGER.info("Received handshake packet {}", packet);
        connection.setProtocolVersion(ProtocolVersion.fromId(packet.getProtocolVersion()));
        if (packet.getIntent() == HandshakePacket.Intent.STATUS) {
            switchToStatus();
        } else if (packet.getIntent() == HandshakePacket.Intent.LOGIN || packet.getIntent() == HandshakePacket.Intent.TRANSFER) {
            switchToLogin();
        }
        return PacketHandler.Result.CANCELLED;
    }

    public void switchToStatus() {
        var statusModule = moduleManager.getServerStatusModule();
        if (statusModule == null) {
            connection.disconnect();
            return;
        }
        connection.switchProtocolState(new StatusSessionHandler(connection, statusModule));
    }

    public void switchToLogin() {
        connection.switchProtocolState(new LoginSessionHandler(connection, moduleManager));
    }
}

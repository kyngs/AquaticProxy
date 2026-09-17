package xyz.kyngs.aquaticproxy.network.session.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.kyngs.aquaticproxy.api.network.protocol.PacketHandler;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolState;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolVersion;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.handshake.HandshakePacket;
import xyz.kyngs.aquaticproxy.api.network.session.ClientSessionHandler;
import xyz.kyngs.aquaticproxy.network.AquaticClientConnection;

public class HandshakeSessionHandler implements ClientSessionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(HandshakeSessionHandler.class);

    private final AquaticClientConnection connection;

    public HandshakeSessionHandler(AquaticClientConnection connection) {
        super();
        this.connection = connection;
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
        connection.switchProtocolState(new StatusSessionHandler());
    }

    public void switchToLogin() {
        connection.switchProtocolState(new LoginSessionHandler(connection));
    }
}

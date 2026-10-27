package xyz.kyngs.aquaticproxy.network.session.backend;

import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolState;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolVersion;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.handshake.HandshakePacket;
import xyz.kyngs.aquaticproxy.api.network.session.BackendSessionHandler;
import xyz.kyngs.aquaticproxy.network.AquaticBackendConnection;

public class BackendHandshakeSessionHandler implements BackendSessionHandler {

    private final AquaticBackendConnection connection;

    public BackendHandshakeSessionHandler(AquaticBackendConnection connection) {
        this.connection = connection;
    }

    @Override
    public ProtocolState getProtocolState() {
        return ProtocolState.HANDSHAKE;
    }

    @Override
    public void activate() {
        connection.writePacket(new HandshakePacket(
                connection.getPairedConnection() == null ? HandshakePacket.Intent.STATUS : HandshakePacket.Intent.LOGIN,
                connection.getPairedConnection() == null ? ProtocolVersion.LATEST.version() : connection.getPairedConnection().getProtocolVersion().version(),
                connection.getAdapter().getRemoteAddress().getHostString(),
                connection.getAdapter().getRemoteAddress().getPort()
        ));
        switchToLogin();
    }

    public void switchToLogin() {
        connection.switchProtocolState(new BackendLoginSessionHandler(connection, connection.getPairedConnection().getPlayer()));
    }

    @Override
    public void deactivate() {

    }
}

package xyz.kyngs.aquaticproxy.network.session.client;

import xyz.kyngs.aquaticproxy.api.network.protocol.PacketHandler;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolState;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.status.StatusPingRequestPacket;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.status.StatusPongResponsePacket;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.status.StatusRequestPacket;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.status.StatusResponsePacket;
import xyz.kyngs.aquaticproxy.api.network.session.ClientSessionHandler;
import xyz.kyngs.aquaticproxy.api.status.ServerStatusModule;
import xyz.kyngs.aquaticproxy.network.AquaticClientConnection;

public class ClientStatusSessionHandler implements ClientSessionHandler {

    private final AquaticClientConnection connection;
    private final ServerStatusModule statusModule;

    public ClientStatusSessionHandler(AquaticClientConnection connection, ServerStatusModule statusModule) {
        super();
        this.connection = connection;
        this.statusModule = statusModule;
    }

    @Override
    public ProtocolState getProtocolState() {
        return ProtocolState.STATUS;
    }

    @Override
    public void activate() {
    }

    @Override
    public void deactivate() {
    }

    @Override
    public PacketHandler.Result handle(StatusPingRequestPacket packet) {
        connection.writePacket(new StatusPongResponsePacket(packet.getTimestamp()));
        return PacketHandler.Result.CANCELLED;
    }

    @Override
    public PacketHandler.Result handle(StatusRequestPacket packet) {
        var status = statusModule.getServerStatus(connection);
        connection.writePacket(new StatusResponsePacket(status));
        return PacketHandler.Result.CANCELLED;
    }
}

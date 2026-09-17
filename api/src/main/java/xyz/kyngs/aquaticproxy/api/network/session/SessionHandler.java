package xyz.kyngs.aquaticproxy.api.network.session;

import xyz.kyngs.aquaticproxy.api.network.protocol.PacketHandler;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolState;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.status.StatusPingRequestPacket;

public interface SessionHandler {
    ProtocolState getProtocolState();

    void activate();

    void deactivate();
}

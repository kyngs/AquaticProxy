package xyz.kyngs.aquaticproxy.api.network.protocol;

import xyz.kyngs.aquaticproxy.api.network.Connection;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.Packet;

public interface PacketHandler<P extends Packet, C extends Connection> {
    Result handle(P packet, C connection);

    enum Result {
        FORWARD,
        MODIFIED,
        CANCELLED
    }
}

package xyz.kyngs.aquaticproxy.api.network.protocol.packet;

import io.netty.buffer.ByteBuf;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolVersion;

public interface Packet {
    void decode(ByteBuf buf, ProtocolVersion version);

    void encode(ByteBuf buf, ProtocolVersion version);
}

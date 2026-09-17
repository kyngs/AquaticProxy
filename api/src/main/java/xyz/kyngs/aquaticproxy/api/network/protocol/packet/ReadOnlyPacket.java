package xyz.kyngs.aquaticproxy.api.network.protocol.packet;

import io.netty.buffer.ByteBuf;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolVersion;

public interface ReadOnlyPacket extends Packet {
    @Override
    default void encode(ByteBuf buf, ProtocolVersion version) {
        throw new UnsupportedOperationException("This packet is read-only and cannot be encoded.");
    }
}

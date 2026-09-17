package xyz.kyngs.aquaticproxy.api.network.protocol.packet;

import io.netty.buffer.ByteBuf;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolVersion;

public interface WriteOnlyPacket extends Packet {
    @Override
    default void decode(ByteBuf buf, ProtocolVersion version) {
        throw new UnsupportedOperationException("This packet is write-only and cannot be decoded.");
    }
}

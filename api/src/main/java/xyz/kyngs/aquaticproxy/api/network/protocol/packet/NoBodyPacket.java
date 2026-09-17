package xyz.kyngs.aquaticproxy.api.network.protocol.packet;

import io.netty.buffer.ByteBuf;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolVersion;

public interface NoBodyPacket extends Packet {
    @Override
    default void decode(ByteBuf buf, ProtocolVersion version) {};

    @Override
    default void encode(ByteBuf buf, ProtocolVersion version) {};
}

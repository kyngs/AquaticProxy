package xyz.kyngs.aquaticproxy.api.network.protocol.packet.status;

import io.netty.buffer.ByteBuf;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolVersion;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.WriteOnlyPacket;

public record StatusPongResponsePacket(long timestamp) implements WriteOnlyPacket {
    @Override
    public void encode(ByteBuf buf, ProtocolVersion version) {
        buf.writeLong(timestamp);
    }
}

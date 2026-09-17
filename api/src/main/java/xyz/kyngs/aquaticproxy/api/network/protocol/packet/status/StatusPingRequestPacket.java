package xyz.kyngs.aquaticproxy.api.network.protocol.packet.status;

import io.netty.buffer.ByteBuf;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolVersion;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.ReadOnlyPacket;

public class StatusPingRequestPacket implements ReadOnlyPacket {
    private long timestamp;
    @Override
    public void decode(ByteBuf buf, ProtocolVersion version) {
        timestamp = buf.readLong();
    }

    public long getTimestamp() {
        return timestamp;
    }
}

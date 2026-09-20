package xyz.kyngs.aquaticproxy.api.network.protocol.packet.status;

import io.netty.buffer.ByteBuf;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolVersion;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.WriteOnlyPacket;
import xyz.kyngs.aquaticproxy.api.status.ServerStatus;

import static xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolUtil.writeString;

public class StatusResponsePacket implements WriteOnlyPacket {
    private final String rawStatus;
    private final ServerStatus status;

    public StatusResponsePacket(ServerStatus status) {
        this.status = status;
        this.rawStatus = null;
    }

    public StatusResponsePacket(String rawStatus) {
        this.rawStatus = rawStatus;
        this.status = null;
    }

    @Override
    public void encode(ByteBuf buf, ProtocolVersion version) {
        if (rawStatus != null) {
            writeString(buf, rawStatus);
        } else {
            writeString(buf, status.toJSON(version));
        }
    }
}

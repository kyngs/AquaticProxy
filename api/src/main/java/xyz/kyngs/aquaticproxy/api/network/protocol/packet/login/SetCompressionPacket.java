package xyz.kyngs.aquaticproxy.api.network.protocol.packet.login;

import io.netty.buffer.ByteBuf;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolVersion;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.Packet;

import static xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolUtil.readVarInt;
import static xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolUtil.writeVarInt;

public class SetCompressionPacket implements Packet {

    private int threshold;

    @Override
    public void decode(ByteBuf buf, ProtocolVersion version) {
        this.threshold = readVarInt(buf);
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public void encode(ByteBuf buf, ProtocolVersion version) {
        writeVarInt(buf, threshold);
    }
}

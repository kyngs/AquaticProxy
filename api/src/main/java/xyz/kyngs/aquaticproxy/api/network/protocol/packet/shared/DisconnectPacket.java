package xyz.kyngs.aquaticproxy.api.network.protocol.packet.shared;

import io.netty.buffer.ByteBuf;
import net.kyori.adventure.text.Component;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolVersion;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.Packet;

import static xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolUtil.readComponent;
import static xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolUtil.writeComponent;

public class DisconnectPacket implements Packet {

    private Component reason;

    public DisconnectPacket(Component reason) {
        this.reason = reason;
    }

    public DisconnectPacket() {
    }

    @Override
    public void decode(ByteBuf buf, ProtocolVersion version) {
        reason = readComponent(buf, version);
    }

    @Override
    public void encode(ByteBuf buf, ProtocolVersion version) {
        writeComponent(buf, reason, version);
    }

    public Component getReason() {
        return reason;
    }

    public void setReason(Component reason) {
        this.reason = reason;
    }
}

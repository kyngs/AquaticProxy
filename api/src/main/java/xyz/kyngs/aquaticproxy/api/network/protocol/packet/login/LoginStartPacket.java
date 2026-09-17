package xyz.kyngs.aquaticproxy.api.network.protocol.packet.login;

import io.netty.buffer.ByteBuf;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolVersion;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.Packet;

import java.util.UUID;

import static xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolUtil.*;

public class LoginStartPacket implements Packet {

    private String username;
    private UUID claimedUUID;

    @Override
    public void decode(ByteBuf buf, ProtocolVersion version) {
        username = readString(buf, 16);
        claimedUUID = readUuid(buf);
    }

    @Override
    public void encode(ByteBuf buf, ProtocolVersion version) {
        writeString(buf, username);
        writeUuid(buf, claimedUUID);
    }
}

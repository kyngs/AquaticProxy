package xyz.kyngs.aquaticproxy.api.network.protocol.packet.login;

import io.netty.buffer.ByteBuf;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolVersion;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.Packet;

import java.util.UUID;

import static xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolUtil.*;

public class LoginStartPacket implements Packet {

    private String username;
    private UUID claimedUUID;

    public LoginStartPacket() {
    }

    public LoginStartPacket(UUID claimedUUID, String username) {
        this.claimedUUID = claimedUUID;
        this.username = username;
    }

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

    public UUID getClaimedUUID() {
        return claimedUUID;
    }

    public void setClaimedUUID(UUID claimedUUID) {
        this.claimedUUID = claimedUUID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

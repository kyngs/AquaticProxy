package xyz.kyngs.aquaticproxy.api.network.protocol.packet.login;

import io.netty.buffer.ByteBuf;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolVersion;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.Packet;

import static xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolUtil.*;

public class EncryptionRequestPacket implements Packet {
    private String serverId;
    private byte[] publicKey;
    private byte[] verifyToken;
    private boolean shouldAuthenticate;

    public byte[] getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(byte[] publicKey) {
        this.publicKey = publicKey;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public boolean isShouldAuthenticate() {
        return shouldAuthenticate;
    }

    public void setShouldAuthenticate(boolean shouldAuthenticate) {
        this.shouldAuthenticate = shouldAuthenticate;
    }

    public byte[] getVerifyToken() {
        return verifyToken;
    }

    public void setVerifyToken(byte[] verifyToken) {
        this.verifyToken = verifyToken;
    }

    @Override
    public void decode(ByteBuf buf, ProtocolVersion version) {
        serverId = readString(buf);
        publicKey = new byte[readVarInt(buf)];
        buf.readBytes(publicKey);
        verifyToken = new byte[readVarInt(buf)];
        buf.readBytes(verifyToken);
        shouldAuthenticate = buf.readBoolean();
    }

    @Override
    public void encode(ByteBuf buf, ProtocolVersion version) {
        writeString(buf, serverId);
        writeVarInt(buf, publicKey.length);
        buf.writeBytes(publicKey);
        writeVarInt(buf, verifyToken.length);
        buf.writeBytes(verifyToken);
        buf.writeBoolean(shouldAuthenticate);
    }
}

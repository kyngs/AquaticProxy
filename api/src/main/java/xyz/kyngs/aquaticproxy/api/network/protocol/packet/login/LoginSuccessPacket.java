package xyz.kyngs.aquaticproxy.api.network.protocol.packet.login;

import io.netty.buffer.ByteBuf;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolVersion;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.Packet;
import xyz.kyngs.aquaticproxy.api.util.GameProfile;

import java.util.UUID;

import static xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolUtil.readUuid;
import static xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolUtil.writeUuid;

public class LoginSuccessPacket implements Packet {

    private GameProfile profile;
    private UUID sessionId;

    public LoginSuccessPacket() {
    }

    public LoginSuccessPacket(GameProfile profile, UUID sessionId) {
        this.profile = profile;
        this.sessionId = sessionId;
    }

    @Override
    public void decode(ByteBuf buf, ProtocolVersion version) {
        profile = GameProfile.deserialize(buf, version);
        sessionId = readUuid(buf);
    }

    @Override
    public void encode(ByteBuf buf, ProtocolVersion version) {
        profile.serialize(buf, version);
        writeUuid(buf, sessionId);
    }

    public GameProfile getProfile() {
        return profile;
    }

    public void setProfile(GameProfile profile) {
        this.profile = profile;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
    }
}

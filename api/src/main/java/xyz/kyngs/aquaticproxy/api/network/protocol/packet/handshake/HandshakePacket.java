package xyz.kyngs.aquaticproxy.api.network.protocol.packet.handshake;

import io.netty.buffer.ByteBuf;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolVersion;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.Packet;

import static xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolUtil.*;

public class HandshakePacket implements Packet {

    private int protocolVersion;
    private String serverAddress;
    private int serverPort;
    private Intent intent;

    public HandshakePacket() {
    }

    public HandshakePacket(Intent intent, int protocolVersion, String serverAddress, int serverPort) {
        this.intent = intent;
        this.protocolVersion = protocolVersion;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    @Override
    public void decode(ByteBuf buf, ProtocolVersion version) {
        protocolVersion = readVarInt(buf);
        serverAddress = readString(buf);
        serverPort = buf.readUnsignedShort();
        intent = Intent.fromId(readVarInt(buf));
    }

    @Override
    public void encode(ByteBuf buf, ProtocolVersion version) {
        writeVarInt(buf, protocolVersion);
        writeString(buf, serverAddress);
        buf.writeShort(serverPort);
        writeVarInt(buf, intent.getId());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HandShakePacket{");
        sb.append("intent=").append(intent);
        sb.append(", protocolVersion=").append(protocolVersion);
        sb.append(", serverAddress='").append(serverAddress).append('\'');
        sb.append(", serverPort=").append(serverPort);
        sb.append('}');
        return sb.toString();
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public enum Intent {
        STATUS(1),
        LOGIN(2),
        TRANSFER(3);

        private final int id;

        Intent(int id) {
            this.id = id;
        }

        public static Intent fromId(int id) {
            return switch (id) {
                case 1 -> STATUS;
                case 2 -> LOGIN;
                case 3 -> TRANSFER;
                default -> throw new IllegalArgumentException("Unknown intent id: " + id);
            };
        }

        public int getId() {
            return id;
        }
    }
}

package xyz.kyngs.aquaticproxy.api.util;

import io.netty.buffer.ByteBuf;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolVersion;

import java.util.List;
import java.util.UUID;

import static xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolUtil.*;

public record GameProfile(UUID uuid, String username, List<Property> properties) {
    public record Property(String name, String value, String signature) {}

    public void serialize(ByteBuf buf, ProtocolVersion version) {
        writeUuid(buf, uuid);
        writeString(buf, username);
        writeVarInt(buf, properties.size());
        for (Property property : properties) {
            writeString(buf, property.name());
            writeString(buf, property.value());
            if (property.signature() != null) {
                buf.writeBoolean(true);
                writeString(buf, property.signature());
            } else {
                buf.writeBoolean(false);
            }
        }
    }

    public static GameProfile deserialize(ByteBuf buf, ProtocolVersion version) {
        UUID uuid = readUuid(buf);
        String username = readString(buf);
        int propertiesCount = readVarInt(buf);
        List<Property> properties = new java.util.ArrayList<>(propertiesCount);
        for (int i = 0; i < propertiesCount; i++) {
            String name = readString(buf);
            String value = readString(buf);
            boolean hasSignature = buf.readBoolean();
            String signature = hasSignature ? readString(buf) : null;
            properties.add(new Property(name, value, signature));
        }
        return new GameProfile(uuid, username, properties);
    }
}

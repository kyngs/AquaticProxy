package xyz.kyngs.aquaticproxy.api.network.protocol.packet.status;

import io.netty.buffer.ByteBuf;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolVersion;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.WriteOnlyPacket;

import static xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolUtil.writeString;

public class StatusResponsePacket implements WriteOnlyPacket {
    @Override
    public void encode(ByteBuf buf, ProtocolVersion version) {
        writeString(buf, "{\n" +
                         "    \"version\": {\n" +
                         "        \"name\": \"1.21.8\",\n" +
                         "        \"protocol\": 772\n" +
                         "    },\n" +
                         "    \"players\": {\n" +
                         "        \"max\": 20,\n" +
                         "        \"online\": 1,\n" +
                         "        \"sample\": [\n" +
                         "            {\n" +
                         "                \"name\": \"thinkofdeath\",\n" +
                         "                \"id\": \"4566e69f-c907-48ee-8d71-d7ba5aa00d20\"\n" +
                         "            }\n" +
                         "        ]\n" +
                         "    },\n" +
                         "    \"description\": {\n" +
                         "        \"text\": \"Hello, world!\"\n" +
                         "    },\n" +
                         "    \"favicon\": \"data:image/png;base64,<data>\",\n" +
                         "    \"enforcesSecureChat\": false\n" +
                         "}\n");
    }
}

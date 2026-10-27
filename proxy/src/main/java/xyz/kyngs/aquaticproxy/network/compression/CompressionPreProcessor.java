package xyz.kyngs.aquaticproxy.network.compression;

import io.netty.buffer.ByteBuf;
import xyz.kyngs.aquaticproxy.api.network.PreProcessor;
import xyz.kyngs.aquaticproxy.api.network.compression.Compressor;

import static xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolUtil.readVarInt;

public class CompressionPreProcessor implements PreProcessor {

    private final Compressor compressor;

    public CompressionPreProcessor(Compressor compressor) {
        this.compressor = compressor;
    }

    @Override
    public ByteBuf transform(ByteBuf buf) {
        var dataLength = readVarInt(buf);
        if (dataLength == 0) {
            return buf;
        }
        var res = compressor.decompress(buf, dataLength);
        if (res != buf) {
            buf.release();
        }
        return res;
    }
}

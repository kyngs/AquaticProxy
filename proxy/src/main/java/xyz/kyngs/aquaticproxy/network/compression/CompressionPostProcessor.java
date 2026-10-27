package xyz.kyngs.aquaticproxy.network.compression;

import io.netty.buffer.ByteBuf;
import xyz.kyngs.aquaticproxy.api.network.PostProcessor;
import xyz.kyngs.aquaticproxy.api.network.compression.Compressor;

import static xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolUtil.writeVarInt;

public class CompressionPostProcessor implements PostProcessor {

    private final Compressor compressor;
    private final int threshold;

    public CompressionPostProcessor(Compressor compressor, int threshold) {
        this.compressor = compressor;
        this.threshold = threshold;
    }

    @Override
    public ByteBuf transform(ByteBuf buf) {
        var dataLength = buf.readableBytes();
        var compress = dataLength >= threshold;
        var capacity = compress
                ? 5 + compressor.maxCompressedLength(dataLength) // VarInt + worst-case payload
                : 1 + dataLength;                                // 0x00 + raw payload
        var out = buf.alloc().buffer(capacity);
        try {
            if (compress) {
                writeVarInt(out, dataLength);
                compressor.compress(buf, out);
            } else {
                out.writeByte(0); // Data Length 0 = not compressed
                out.writeBytes(buf);
            }
            return out;
        } catch (Throwable t) {
            out.release();
            throw t;
        } finally {
            buf.release();
        }
    }
}

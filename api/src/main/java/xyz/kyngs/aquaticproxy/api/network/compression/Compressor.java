package xyz.kyngs.aquaticproxy.api.network.compression;

import io.netty.buffer.ByteBuf;

public interface Compressor {
    void compress(ByteBuf in, ByteBuf out);

    int maxCompressedLength(int n);

    ByteBuf decompress(ByteBuf buf, int dataLength);
}

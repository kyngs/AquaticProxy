package xyz.kyngs.aquaticproxy.network.compression;

import io.netty.buffer.ByteBuf;
import xyz.kyngs.aquaticproxy.api.network.compression.Compressor;

import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class JavaZipCompressor implements Compressor {
    private static final int MAX_UNCOMPRESSED = 8 * 1024 * 1024; // 2^23, vanilla's cap
    public static final JavaZipCompressor DEFAULT = new JavaZipCompressor();

    private final int level;

    public JavaZipCompressor() {
        this(Deflater.DEFAULT_COMPRESSION);
    }

    public JavaZipCompressor(int level) {
        this.level = level;
    }

    @Override
    public void compress(ByteBuf in, ByteBuf out) {
        var bound = maxCompressedLength(in.readableBytes());
        if (out.maxWritableBytes() < bound) {
            throw new IllegalArgumentException(
                    "Output buffer can't hold worst-case compressed size of " + bound + " bytes");
        }
        out.ensureWritable(bound);
        if (out.nioBufferCount() != 1) {
            throw new IllegalArgumentException("Output buffer must be a single-component buffer");
        }

        var start = out.writerIndex();
        var dst = out.nioBuffer(start, out.writableBytes()); // shares memory with out
        var deflater = new Deflater(level);
        try {
            deflater.setInput(in.nioBuffer());
            deflater.finish();
            while (!deflater.finished()) {
                if (deflater.deflate(dst) == 0 && !dst.hasRemaining()) {
                    // Can't happen if compressBound is right; guards against an infinite loop.
                    throw new IllegalStateException("Compressed output exceeded compressBound");
                }
            }
            out.writerIndex(start + dst.position());
            in.skipBytes(in.readableBytes());
        } finally {
            deflater.end();
        }
    }

    @Override
    public int maxCompressedLength(int n) {
        // zlib's compressBound(): worst case for incompressible input
        return n + (n >>> 12) + (n >>> 14) + (n >>> 25) + 13;
    }

    @Override
    public ByteBuf decompress(ByteBuf buf, int dataLength) {
        if (dataLength <= 0 || dataLength > MAX_UNCOMPRESSED) {
            throw new IllegalArgumentException("Bad uncompressed length: " + dataLength);
        }
        var inflater = new Inflater();
        // +1 byte of slack so a stream that's longer than claimed is detected
        var out = buf.alloc().buffer(dataLength + 1, dataLength + 1);
        var ok = false;
        try {
            inflater.setInput(buf.nioBuffer());
            var dst = out.nioBuffer(0, dataLength + 1); // inflate advances dst.position()
            while (!inflater.finished()) {
                int n = inflater.inflate(dst);
                if (n == 0 && !inflater.finished()) {
                    throw new DataFormatException(inflater.needsInput()
                            ? "Truncated zlib stream"
                            : "Decompressed data longer than declared " + dataLength);
                }
            }
            if (dst.position() != dataLength) {
                throw new DataFormatException(
                        "Declared " + dataLength + " bytes, got " + dst.position());
            }
            out.writerIndex(dataLength);
            buf.skipBytes(buf.readableBytes() - inflater.getRemaining());
            ok = true;
            return out;
        } catch (DataFormatException e) {
            throw new IllegalArgumentException("Invalid zlib data: " + e.getMessage(), e);
        } finally {
            inflater.end();
            if (!ok) out.release();
        }
    }
}

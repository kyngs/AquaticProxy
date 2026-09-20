package xyz.kyngs.aquaticproxy.api.network.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.json.JSONOptions;
import net.kyori.option.OptionSchema;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

// Some parts here are kindly borrowed from https://github.com/PaperMC/Velocity/blob/bc553001230ba01fc41a9a8e0aad1b21b87fabfc/proxy/src/main/java/com/velocitypowered/proxy/protocol/ProtocolUtils.java
public class ProtocolUtil {

    private static final GsonComponentSerializer PRE_1_16_SERIALIZER =
            GsonComponentSerializer.builder()
                    .options(
                            OptionSchema.globalSchema().stateBuilder()
                                    // general options
                                    .value(JSONOptions.EMIT_CLICK_URL_HTTPS, Boolean.TRUE)
                                    // before 1.16
                                    .value(JSONOptions.EMIT_RGB, Boolean.FALSE)
                                    .value(JSONOptions.EMIT_HOVER_EVENT_TYPE, JSONOptions.HoverEventValueMode.VALUE_FIELD)
                                    .value(JSONOptions.EMIT_CLICK_EVENT_TYPE, JSONOptions.ClickEventValueMode.CAMEL_CASE)
                                    // before 1.20.3
                                    .value(JSONOptions.EMIT_COMPACT_TEXT_COMPONENT, Boolean.FALSE)
                                    .value(JSONOptions.EMIT_HOVER_SHOW_ENTITY_ID_AS_INT_ARRAY, Boolean.FALSE)
                                    .value(JSONOptions.VALIDATE_STRICT_EVENTS, Boolean.FALSE)
                                    // before 1.21.5
                                    .value(JSONOptions.EMIT_CHANGE_PAGE_CLICK_EVENT_PAGE_AS_STRING, Boolean.TRUE)
                                    .build()
                    )
                    .build();
    private static final GsonComponentSerializer PRE_1_20_3_SERIALIZER =
            GsonComponentSerializer.builder()
                    .options(
                            OptionSchema.globalSchema().stateBuilder()
                                    // general options
                                    .value(JSONOptions.EMIT_CLICK_URL_HTTPS, Boolean.TRUE)
                                    // after 1.16
                                    .value(JSONOptions.EMIT_RGB, Boolean.TRUE)
                                    .value(JSONOptions.EMIT_HOVER_EVENT_TYPE, JSONOptions.HoverEventValueMode.CAMEL_CASE)
                                    .value(JSONOptions.EMIT_CLICK_EVENT_TYPE, JSONOptions.ClickEventValueMode.CAMEL_CASE)
                                    .value(JSONOptions.EMIT_HOVER_SHOW_ENTITY_KEY_AS_TYPE_AND_UUID_AS_ID, true)
                                    // before 1.20.3
                                    .value(JSONOptions.EMIT_COMPACT_TEXT_COMPONENT, Boolean.FALSE)
                                    .value(JSONOptions.EMIT_HOVER_SHOW_ENTITY_ID_AS_INT_ARRAY, Boolean.FALSE)
                                    .value(JSONOptions.VALIDATE_STRICT_EVENTS, Boolean.FALSE)
                                    // before 1.21.5
                                    .value(JSONOptions.EMIT_CHANGE_PAGE_CLICK_EVENT_PAGE_AS_STRING, Boolean.TRUE)
                                    .build()
                    )
                    .build();
    private static final GsonComponentSerializer PRE_1_21_5_SERIALIZER =
            GsonComponentSerializer.builder()
                    .options(
                            OptionSchema.globalSchema().stateBuilder()
                                    // general options
                                    .value(JSONOptions.EMIT_CLICK_URL_HTTPS, Boolean.TRUE)
                                    // after 1.16
                                    .value(JSONOptions.EMIT_RGB, Boolean.TRUE)
                                    .value(JSONOptions.EMIT_HOVER_EVENT_TYPE, JSONOptions.HoverEventValueMode.CAMEL_CASE)
                                    .value(JSONOptions.EMIT_CLICK_EVENT_TYPE, JSONOptions.ClickEventValueMode.CAMEL_CASE)
                                    .value(JSONOptions.EMIT_HOVER_SHOW_ENTITY_KEY_AS_TYPE_AND_UUID_AS_ID, true)
                                    // after 1.20.3
                                    .value(JSONOptions.EMIT_COMPACT_TEXT_COMPONENT, Boolean.TRUE)
                                    .value(JSONOptions.EMIT_HOVER_SHOW_ENTITY_ID_AS_INT_ARRAY, Boolean.TRUE)
                                    .value(JSONOptions.VALIDATE_STRICT_EVENTS, Boolean.TRUE)
                                    // before 1.21.5
                                    .value(JSONOptions.EMIT_CHANGE_PAGE_CLICK_EVENT_PAGE_AS_STRING, Boolean.TRUE)
                                    .build()
                    )
                    .build();
    private static final GsonComponentSerializer MODERN_SERIALIZER =
            GsonComponentSerializer.builder()
                    .options(
                            OptionSchema.globalSchema().stateBuilder()
                                    // general options
                                    .value(JSONOptions.EMIT_CLICK_URL_HTTPS, Boolean.TRUE)
                                    // after 1.16
                                    .value(JSONOptions.EMIT_RGB, Boolean.TRUE)
                                    .value(JSONOptions.EMIT_HOVER_EVENT_TYPE, JSONOptions.HoverEventValueMode.SNAKE_CASE)
                                    .value(JSONOptions.EMIT_CLICK_EVENT_TYPE, JSONOptions.ClickEventValueMode.SNAKE_CASE)
                                    // after 1.20.3
                                    .value(JSONOptions.EMIT_COMPACT_TEXT_COMPONENT, Boolean.TRUE)
                                    .value(JSONOptions.EMIT_HOVER_SHOW_ENTITY_ID_AS_INT_ARRAY, Boolean.TRUE)
                                    // after 1.21.5
                                    .value(JSONOptions.EMIT_HOVER_SHOW_ENTITY_KEY_AS_TYPE_AND_UUID_AS_ID, Boolean.FALSE)
                                    .value(JSONOptions.VALIDATE_STRICT_EVENTS, Boolean.TRUE)
                                    .value(JSONOptions.EMIT_CHANGE_PAGE_CLICK_EVENT_PAGE_AS_STRING, Boolean.FALSE)
                                    .build()
                    )
                    .build();

    public static final int DEFAULT_MAX_STRING_SIZE = 65536; // 64KiB
    private static final int MAXIMUM_VARINT_SIZE = 5;
    /*private static final BinaryTagType<? extends BinaryTag>[] BINARY_TAG_TYPES = new BinaryTagType[] {
            BinaryTagTypes.END, BinaryTagTypes.BYTE, BinaryTagTypes.SHORT, BinaryTagTypes.INT,
            BinaryTagTypes.LONG, BinaryTagTypes.FLOAT, BinaryTagTypes.DOUBLE,
            BinaryTagTypes.BYTE_ARRAY, BinaryTagTypes.STRING, BinaryTagTypes.LIST,
            BinaryTagTypes.COMPOUND, BinaryTagTypes.INT_ARRAY, BinaryTagTypes.LONG_ARRAY};*/
    private static final PacketDecoderException BAD_VARINT_CACHED =
            new PacketDecoderException("Bad VarInt decoded");
    private static final int[] VAR_INT_LENGTHS = new int[33];

    static {
        for (int i = 0; i <= 32; ++i) {
            VAR_INT_LENGTHS[i] = (int) Math.ceil((31d - (i - 1)) / 7d);
        }
        VAR_INT_LENGTHS[32] = 1; // Special case for the number 0.
    }

    public static final int DEFAULT_MAX_STRING_BYTES = varIntBytes(ByteBufUtil.utf8MaxBytes(DEFAULT_MAX_STRING_SIZE))
                                                       + ByteBufUtil.utf8MaxBytes(DEFAULT_MAX_STRING_SIZE);

    private static PacketDecoderException badVarint() {
        return BAD_VARINT_CACHED;
    }

    /**
     * Reads a Minecraft-style VarInt from the specified {@code buf}.
     *
     * @param buf the buffer to read from
     * @return the decoded VarInt
     */
    public static int readVarInt(ByteBuf buf) {
        int readable = buf.readableBytes();
        if (readable == 0) {
            // special case for empty buffer
            throw badVarint();
        }

        // we can read at least one byte, and this should be a common case
        int k = buf.readByte();
        if ((k & 0x80) != 128) {
            return k;
        }

        // in case decoding one byte was not enough, use a loop to decode up to the next 4 bytes
        int maxRead = Math.min(MAXIMUM_VARINT_SIZE, readable);
        int i = k & 0x7F;
        for (int j = 1; j < maxRead; j++) {
            k = buf.readByte();
            i |= (k & 0x7F) << j * 7;
            if ((k & 0x80) != 128) {
                return i;
            }
        }
        throw badVarint();
    }

    /**
     * Returns the exact byte size of {@code value} if it were encoded as a VarInt.
     *
     * @param value the value to encode
     * @return the byte size of {@code value} if encoded as a VarInt
     */
    public static int varIntBytes(int value) {
        return VAR_INT_LENGTHS[Integer.numberOfLeadingZeros(value)];
    }

    /**
     * Writes a Minecraft-style VarInt to the specified {@code buf}.
     *
     * @param buf   the buffer to read from
     * @param value the integer to write
     */
    public static void writeVarInt(ByteBuf buf, int value) {
        // Peel the one and two byte count cases explicitly as they are the most common VarInt sizes
        // that the proxy will write, to improve inlining.
        if ((value & (0xFFFFFFFF << 7)) == 0) {
            buf.writeByte(value);
        } else if ((value & (0xFFFFFFFF << 14)) == 0) {
            int w = (value & 0x7F | 0x80) << 8 | (value >>> 7);
            buf.writeShort(w);
        } else {
            writeVarIntFull(buf, value);
        }
    }

    private static void writeVarIntFull(ByteBuf buf, int value) {
        // See https://steinborn.me/posts/performance/how-fast-can-you-write-a-varint/

        // This essentially is an unrolled version of the "traditional" VarInt encoding.
        if ((value & (0xFFFFFFFF << 7)) == 0) {
            buf.writeByte(value);
        } else if ((value & (0xFFFFFFFF << 14)) == 0) {
            int w = (value & 0x7F | 0x80) << 8 | (value >>> 7);
            buf.writeShort(w);
        } else if ((value & (0xFFFFFFFF << 21)) == 0) {
            int w = (value & 0x7F | 0x80) << 16 | ((value >>> 7) & 0x7F | 0x80) << 8 | (value >>> 14);
            buf.writeMedium(w);
        } else if ((value & (0xFFFFFFFF << 28)) == 0) {
            int w = (value & 0x7F | 0x80) << 24 | (((value >>> 7) & 0x7F | 0x80) << 16)
                    | ((value >>> 14) & 0x7F | 0x80) << 8 | (value >>> 21);
            buf.writeInt(w);
        } else {
            int w = (value & 0x7F | 0x80) << 24 | ((value >>> 7) & 0x7F | 0x80) << 16
                    | ((value >>> 14) & 0x7F | 0x80) << 8 | ((value >>> 21) & 0x7F | 0x80);
            buf.writeInt(w);
            buf.writeByte(value >>> 28);
        }
    }

    /**
     * Directly encodes a 21-bit Minecraft VarInt, ready to be written with {@link ByteBuf#writeMedium(int)}.
     * The upper 11 bits will be discarded.
     *
     * @param value the value to encode
     * @return the encoded value
     */
    public static int encode21BitVarInt(int value) {
        // See https://steinborn.me/posts/performance/how-fast-can-you-write-a-varint/
        return (value & 0x7F | 0x80) << 16 | ((value >>> 7) & 0x7F | 0x80) << 8 | (value >>> 14);
    }

    public static String readString(ByteBuf buf) {
        return readString(buf, DEFAULT_MAX_STRING_SIZE);
    }

    /**
     * Reads a VarInt length-prefixed UTF-8 string from the {@code buf}, making sure to not go over
     * {@code cap} size.
     *
     * @param buf the buffer to read from
     * @param cap the maximum size of the string, in UTF-8 character length
     * @return the decoded string
     */
    public static String readString(ByteBuf buf, int cap) {
        int length = readVarInt(buf);
        return readString(buf, cap, length);
    }

    private static String readString(ByteBuf buf, int cap, int length) {
        verifyPacket(length >= 0, "Got a negative-length string (%s)", length);
        // `cap` is interpreted as a UTF-8 character length. To cover the full Unicode plane, we must
        // consider the length of a UTF-8 character, which can be up to 3 bytes. We do an initial
        // sanity check and then check again to make sure our optimistic guess was good.
        verifyPacket(length <= cap * 3, "Bad string size (got %s, maximum is %s)", length, cap);
        verifyPacket(buf.isReadable(length),
                "Trying to read a string that is too long (wanted %s, only have %s)", length,
                buf.readableBytes());
        String str = buf.readString(length, StandardCharsets.UTF_8);
        verifyPacket(str.length() <= cap, "Got a too-long string (got %s, max %s)", str.length(), cap);
        return str;
    }

    /**
     * Determines the size of the written {@code str} if encoded as a VarInt-prefixed UTF-8 string.
     *
     * @param str the string to write
     * @return the encoded size
     */
    public static int stringSizeHint(CharSequence str) {
        int size = ByteBufUtil.utf8Bytes(str);
        return varIntBytes(size) + size;
    }

    /**
     * Writes the specified {@code str} to the {@code buf} with a VarInt prefix.
     *
     * @param buf the buffer to write to
     * @param str the string to write
     */
    public static void writeString(ByteBuf buf, CharSequence str) {
        int size = ByteBufUtil.utf8Bytes(str);
        writeVarInt(buf, size);
        buf.writeCharSequence(str, StandardCharsets.UTF_8);
    }

    private static void verifyPacket(boolean condition, String message, Object... args) {
        if (!condition) throw new PacketDecoderException(message.formatted(args));
    }

    /**
     * Reads an UUID from the {@code buf}.
     *
     * @param buf the buffer to read from
     * @return the UUID from the buffer
     */
    public static UUID readUuid(ByteBuf buf) {
        long msb = buf.readLong();
        long lsb = buf.readLong();
        return new UUID(msb, lsb);
    }

    public static void writeUuid(ByteBuf buf, UUID uuid) {
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
    }

    public static GsonComponentSerializer getJsonChatSerializer(ProtocolVersion version) {
        if (version.isAtLeast(ProtocolVersion.MINECRAFT_1_21_5)) {
            return MODERN_SERIALIZER;
        }

        if (version.isAtLeast(ProtocolVersion.MINECRAFT_1_20_3)) {
            return PRE_1_21_5_SERIALIZER;
        }

        if (version.isAtLeast(ProtocolVersion.MINECRAFT_1_16)) {
            return PRE_1_20_3_SERIALIZER;
        }

        return PRE_1_16_SERIALIZER;
    }
}

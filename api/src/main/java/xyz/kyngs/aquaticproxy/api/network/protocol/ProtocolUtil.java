package xyz.kyngs.aquaticproxy.api.network.protocol;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.LazilyParsedNumber;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.ByteBufUtil;
import net.kyori.adventure.nbt.*;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.json.JSONOptions;
import net.kyori.option.OptionSchema;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private static final BinaryTagType<? extends BinaryTag>[] BINARY_TAG_TYPES = new BinaryTagType[] {
            BinaryTagTypes.END, BinaryTagTypes.BYTE, BinaryTagTypes.SHORT, BinaryTagTypes.INT,
            BinaryTagTypes.LONG, BinaryTagTypes.FLOAT, BinaryTagTypes.DOUBLE,
            BinaryTagTypes.BYTE_ARRAY, BinaryTagTypes.STRING, BinaryTagTypes.LIST,
            BinaryTagTypes.COMPOUND, BinaryTagTypes.INT_ARRAY, BinaryTagTypes.LONG_ARRAY};

    static {
        for (int i = 0; i <= 32; ++i) {
            VAR_INT_LENGTHS[i] = (int) Math.ceil((31d - (i - 1)) / 7d);
        }
        VAR_INT_LENGTHS[32] = 1; // Special case for the number 0.
    }

    public static final int DEFAULT_MAX_STRING_BYTES = varIntBytes(ByteBufUtil.utf8MaxBytes(DEFAULT_MAX_STRING_SIZE))
                                                       + ByteBufUtil.utf8MaxBytes(DEFAULT_MAX_STRING_SIZE);

    private static PacketDecoderException badVarint() {
        return new PacketDecoderException("Bad VarInt decoded"); //BAD_VARINT_CACHED;
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

    public static Component readComponent(ByteBuf buf, ProtocolVersion version) {
        var json = deserializeNBTJSON(readBinaryTag(buf, version));
        return getJsonChatSerializer(version).deserialize(json.toString());
    }

    public static JsonElement deserializeNBTJSON(BinaryTag tag) {
        switch (tag.type().id()) {
            case 1://BinaryTagTypes.BYTE:
                return new JsonPrimitive(((ByteBinaryTag) tag).value());
            case 2://BinaryTagTypes.SHORT:
                return new JsonPrimitive(((ShortBinaryTag) tag).value());
            case 3://BinaryTagTypes.INT:
                return new JsonPrimitive(((IntBinaryTag) tag).value());
            case 4://BinaryTagTypes.LONG:
                return new JsonPrimitive(((LongBinaryTag) tag).value());
            case 5://BinaryTagTypes.FLOAT:
                return new JsonPrimitive(((FloatBinaryTag) tag).value());
            case 6://BinaryTagTypes.DOUBLE:
                return new JsonPrimitive(((DoubleBinaryTag) tag).value());
            case 7://BinaryTagTypes.BYTE_ARRAY:
                byte[] byteArray = ((ByteArrayBinaryTag) tag).value();

                JsonArray jsonByteArray = new JsonArray(byteArray.length);
                for (byte b : byteArray) {
                    jsonByteArray.add(new JsonPrimitive(b));
                }

                return jsonByteArray;
            case 8://BinaryTagTypes.STRING:
                return new JsonPrimitive(((StringBinaryTag) tag).value());
            case 9://BinaryTagTypes.LIST:
                ListBinaryTag items = (ListBinaryTag) tag;
                JsonArray jsonList = new JsonArray(items.size());

                for (BinaryTag subTag : items) {
                    jsonList.add(deserializeNBTJSON(subTag));
                }

                return jsonList;
            case 10://BinaryTagTypes.COMPOUND:
                CompoundBinaryTag compound = (CompoundBinaryTag) tag;
                JsonObject jsonObject = new JsonObject();

                compound.keySet().forEach(key -> {
                    // [{"text":"test1"},"test2"] can't be represented as a binary list tag
                    // it is represented by a list tag with two compound tags
                    // the second compound tag will have an empty key mapped to "test2"
                    // without this fix this would lead to an invalid json component:
                    // [{"text":"test1"},{"":"test2"}]
                    jsonObject.add(key.isEmpty() ? "text" : key, deserializeNBTJSON(compound.get(key)));
                });

                return jsonObject;
            case 11://BinaryTagTypes.INT_ARRAY:
                int[] intArray = ((IntArrayBinaryTag) tag).value();

                JsonArray jsonIntArray = new JsonArray(intArray.length);
                for (int i : intArray) {
                    jsonIntArray.add(new JsonPrimitive(i));
                }

                return jsonIntArray;
            case 12://BinaryTagTypes.LONG_ARRAY:
                long[] longArray = ((LongArrayBinaryTag) tag).value();

                JsonArray jsonLongArray = new JsonArray(longArray.length);
                for (long l : longArray) {
                    jsonLongArray.add(new JsonPrimitive(l));
                }

                return jsonLongArray;
            default:
                throw new IllegalArgumentException("Unknown NBT tag: " + tag);
        }
    }

    public static BinaryTag serializeNBTJSON(JsonElement json) {
        if (json instanceof JsonPrimitive jsonPrimitive) {
            if (jsonPrimitive.isNumber()) {
                Number number = json.getAsNumber();

                if (number instanceof Byte) {
                    return ByteBinaryTag.byteBinaryTag((Byte) number);
                } else if (number instanceof Short) {
                    return ShortBinaryTag.shortBinaryTag((Short) number);
                } else if (number instanceof Integer) {
                    return IntBinaryTag.intBinaryTag((Integer) number);
                } else if (number instanceof Long) {
                    return LongBinaryTag.longBinaryTag((Long) number);
                } else if (number instanceof Float) {
                    return FloatBinaryTag.floatBinaryTag((Float) number);
                } else if (number instanceof Double) {
                    return DoubleBinaryTag.doubleBinaryTag((Double) number);
                } else if (number instanceof LazilyParsedNumber) {
                    return IntBinaryTag.intBinaryTag(number.intValue());
                }
            } else if (jsonPrimitive.isString()) {
                return StringBinaryTag.stringBinaryTag(jsonPrimitive.getAsString());
            } else if (jsonPrimitive.isBoolean()) {
                return ByteBinaryTag.byteBinaryTag((byte) (jsonPrimitive.getAsBoolean() ? 1 : 0));
            } else {
                throw new IllegalArgumentException("Unknown JSON primitive: " + jsonPrimitive);
            }
        } else if (json instanceof JsonObject) {
            CompoundBinaryTag.Builder compound = CompoundBinaryTag.builder();

            for (Map.Entry<String, JsonElement> property : ((JsonObject) json).entrySet()) {
                compound.put(property.getKey(), serializeNBTJSON(property.getValue()));
            }

            return compound.build();
        } else if (json instanceof JsonArray) {
            List<JsonElement> jsonArray = ((JsonArray) json).asList();

            if (jsonArray.isEmpty()) {
                return ListBinaryTag.empty();
            }

            List<BinaryTag> tagItems = new ArrayList<>(jsonArray.size());
            BinaryTagType<? extends BinaryTag> listType = null;

            for (JsonElement jsonEl : jsonArray) {
                BinaryTag tag = serializeNBTJSON(jsonEl);
                tagItems.add(tag);

                if (listType == null) {
                    listType = tag.type();
                } else if (listType != tag.type()) {
                    listType = BinaryTagTypes.COMPOUND;
                }
            }

            switch (listType.id()) {
                case 1://BinaryTagTypes.BYTE:
                    byte[] bytes = new byte[jsonArray.size()];
                    for (int i = 0; i < bytes.length; i++) {
                        bytes[i] = jsonArray.get(i).getAsNumber().byteValue();
                    }

                    return ByteArrayBinaryTag.byteArrayBinaryTag(bytes);
                case 3://BinaryTagTypes.INT:
                    int[] ints = new int[jsonArray.size()];
                    for (int i = 0; i < ints.length; i++) {
                        ints[i] = jsonArray.get(i).getAsNumber().intValue();
                    }

                    return IntArrayBinaryTag.intArrayBinaryTag(ints);
                case 4://BinaryTagTypes.LONG:
                    long[] longs = new long[jsonArray.size()];
                    for (int i = 0; i < longs.length; i++) {
                        longs[i] = jsonArray.get(i).getAsNumber().longValue();
                    }

                    return LongArrayBinaryTag.longArrayBinaryTag(longs);
                case 10://BinaryTagTypes.COMPOUND:
                    tagItems.replaceAll(tag -> {
                        if (tag.type() == BinaryTagTypes.COMPOUND) {
                            return tag;
                        } else {
                            return CompoundBinaryTag.builder().put("", tag).build();
                        }
                    });
                    break;
            }

            return ListBinaryTag.listBinaryTag(listType, tagItems);
        }

        return EndBinaryTag.endBinaryTag();
    }

    public static BinaryTag readBinaryTag(ByteBuf buf, ProtocolVersion version) {
        BinaryTagType<?> type = BINARY_TAG_TYPES[buf.readByte()];
        if (!version.isAtLeast(ProtocolVersion.MINECRAFT_1_20_2)) {
            buf.skipBytes(buf.readUnsignedShort());
        }
        try {
            return type.read(new ByteBufInputStream(buf));
        } catch (IOException thrown) {
            throw new PacketDecoderException("Unable to parse BinaryTag, full error: " + thrown.getMessage());
        }
    }

    public static <T extends BinaryTag> void writeBinaryTag(ByteBuf buf, ProtocolVersion version,
                                                            T tag) {
        BinaryTagType<T> type = (BinaryTagType<T>) tag.type();
        buf.writeByte(type.id());
        try {
            if (!version.isAtLeast(ProtocolVersion.MINECRAFT_1_20_2)) {
                // Empty name
                buf.writeShort(0);
            }
            type.write(tag, new ByteBufOutputStream(buf));
        } catch (IOException e) {
            throw new PacketDecoderException("Unable to encode BinaryTag");
        }
    }

    public static void writeComponent(ByteBuf buf, Component component, ProtocolVersion version) {
        if (version.isAtLeast(ProtocolVersion.MINECRAFT_1_20_3)) {
            writeBinaryTag(buf, version, serializeNBTJSON(getJsonChatSerializer(version).serializeToTree(component)));
        } else {
            writeString(buf, getJsonChatSerializer(version).serialize(component));
        }
    }
}

package xyz.kyngs.aquaticproxy.api.network;

import io.netty.buffer.ByteBuf;

public interface PreProcessor {
    ByteBuf transform(ByteBuf buf);
}

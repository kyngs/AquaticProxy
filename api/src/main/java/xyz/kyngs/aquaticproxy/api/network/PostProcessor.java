package xyz.kyngs.aquaticproxy.api.network;

import io.netty.buffer.ByteBuf;

public interface PostProcessor {
    ByteBuf transform(ByteBuf buf);
}

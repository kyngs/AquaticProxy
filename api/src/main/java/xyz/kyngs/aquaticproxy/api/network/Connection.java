package xyz.kyngs.aquaticproxy.api.network;

import io.netty.buffer.ByteBuf;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolVersion;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.Packet;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;

public interface Connection {

    void handleFrame(ByteBuf data);

    void writeFrame(byte[] data);

    void writePacket(Packet packet);

    void setProtocolVersion(ProtocolVersion version);

    Lock getProtocolLock();

    default <T> T doLocked(Callable<T> callable) {
        var lock = getProtocolLock();
        lock.lock();
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    default void doLocked(Runnable runnable) {
        var lock = getProtocolLock();
        lock.lock();
        try {
            runnable.run();
        } finally {
            lock.unlock();
        }
    }
}

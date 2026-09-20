package xyz.kyngs.aquaticproxy.api.network;

import io.netty.buffer.ByteBuf;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolVersion;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.Packet;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

public interface Connection {

    void handleFrame(ByteBuf data);

    void writeFrame(byte[] data);

    void writePacket(Packet packet);

    ProtocolVersion getProtocolVersion();

    void setProtocolVersion(ProtocolVersion version);

    void disconnect(Component reason);

    default void disconnect() {
        disconnect(Component.empty());
    }

    boolean isConnected();

    ReadWriteLock getConnectionLock();

    @Nullable Connection getPairedConnection();

    default <T> T doLocked(Lock lock, Callable<T> callable) {
        lock.lock();
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    default void doLocked(Lock lock, Runnable runnable) {
        lock.lock();
        try {
            runnable.run();
        } finally {
            lock.unlock();
        }
    }

    default <T> T doWriteLocked(Callable<T> callable) {
        return doLocked(getConnectionLock().writeLock(), callable);
    }

    default void doWriteLocked(Runnable runnable) {
        doLocked(getConnectionLock().writeLock(), runnable);
    }

    default <T> T doReadLocked(Callable<T> callable) {
        return doLocked(getConnectionLock().readLock(), callable);
    }

    default void doReadLocked(Runnable runnable) {
        doLocked(getConnectionLock().readLock(), runnable);
    }
}

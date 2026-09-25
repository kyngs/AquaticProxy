package xyz.kyngs.aquaticproxy.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.kyngs.aquaticproxy.api.network.Connection;
import xyz.kyngs.aquaticproxy.api.network.NetworkFabric;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolUtil;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolVersion;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.Packet;
import xyz.kyngs.aquaticproxy.api.network.session.SessionHandler;
import xyz.kyngs.aquaticproxy.module.AquaticModuleManager;

import java.io.IOException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class AquaticConnection<S extends SessionHandler, PC extends Connection> implements Connection {
    private static final Logger LOGGER = LoggerFactory.getLogger(AquaticConnection.class);
    protected final NetworkFabric.Adapter<?> adapter;
    protected final AquaticNetworkModule networkModule;
    protected final AquaticModuleManager moduleManager;
    protected final ReentrantReadWriteLock connectionLock;
    protected ProtocolVersion protocolVersion;
    protected volatile S sessionHandler;
    protected volatile PC pairedConnection;

    protected AquaticConnection(NetworkFabric.Adapter<?> adapter, AquaticNetworkModule networkModule, AquaticModuleManager moduleManager) {
        this.adapter = adapter;
        this.networkModule = networkModule;
        this.moduleManager = moduleManager;
        this.sessionHandler = createInitialSessionHandler(); // Cannot pass in the constructor because it would not allow the subclass to pass itself to the session handler, which is needed for the session handler to be able to call back into the connection.
        this.protocolVersion = ProtocolVersion.UNKNOWN;
        this.connectionLock = new ReentrantReadWriteLock();
    }

    public NetworkFabric.Adapter<?> getAdapter() {
        return adapter;
    }

    @Override
    public ProtocolVersion getProtocolVersion() {
        return protocolVersion;
    }

    @Override
    public void setProtocolVersion(ProtocolVersion version) {
        this.protocolVersion = version;
    }

    @Override
    public boolean isConnected() {
        return adapter.isConnected();
    }

    @Override
    public ReadWriteLock getConnectionLock() {
        return connectionLock;
    }

    abstract protected S createInitialSessionHandler();

    public S getSessionHandler() {
        return sessionHandler;
    }

    public void switchProtocolState(S newHandler) {
        doWriteLocked(() -> {
            sessionHandler.deactivate();
            sessionHandler = newHandler;
            newHandler.activate();
        });
        LOGGER.info("Switched protocol state to {}", newHandler.getProtocolState());
    }

    @Override
    public void writeFrame(ByteBuf data) {
        try {
            adapter.writeFrame(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void writePacket(Packet packet) {
        var registration = networkModule.getPacketHandlerRegistry().getPublishedPacket(packet.getClass());
        if (registration == null) {
            throw new IllegalStateException("Packet " + packet.getClass().getName() + " is not registered");
        }
        var data = Unpooled.buffer();
        ProtocolUtil.writeVarInt(data, registration.protocolPacketIdMap().get(protocolVersion.version()));
        packet.encode(data, protocolVersion);
        writeFrame(data);
        data.release();
    }

    @Override
    public void disconnect() {
        try {
            adapter.close();

            if (pairedConnection != null && pairedConnection.isConnected()) {
                pairedConnection.disconnect();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @Nullable PC getPairedConnection() {
        return pairedConnection;
    }

    public void setPairedConnection(PC pairedConnection) {
        if (!connectionLock.writeLock().isHeldByCurrentThread()) {
            throw new IllegalStateException("Cannot set paired connection without holding the connection's write lock");
        }
        this.pairedConnection = pairedConnection;
    }
}

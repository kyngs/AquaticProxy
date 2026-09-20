package xyz.kyngs.aquaticproxy.network;

import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import net.kyori.adventure.text.Component;
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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AquaticConnection<S extends SessionHandler> implements Connection {
    private static final Logger LOGGER = LoggerFactory.getLogger(AquaticConnection.class);
    protected final NetworkFabric.ClientAdapter<?> adapter;
    protected final AquaticNetworkModule networkModule;
    protected final AquaticModuleManager moduleManager;
    protected final Lock protocolLock;
    protected ProtocolVersion protocolVersion;
    protected volatile S sessionHandler;

    protected AquaticConnection(NetworkFabric.ClientAdapter<?> adapter, AquaticNetworkModule networkModule, AquaticModuleManager moduleManager) {
        this.adapter = adapter;
        this.networkModule = networkModule;
        this.moduleManager = moduleManager;
        this.sessionHandler = createInitialSessionHandler(); // Cannot pass in the constructor because it would not allow the subclass to pass itself to the session handler, which is needed for the session handler to be able to call back into the connection.
        this.protocolVersion = ProtocolVersion.UNKNOWN;
        this.protocolLock = new ReentrantLock();

        sessionHandler.activate();
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
    public Lock getProtocolLock() {
        return protocolLock;
    }

    abstract protected S createInitialSessionHandler();

    public S getSessionHandler() {
        return sessionHandler;
    }

    public void switchProtocolState(S newHandler) {
        doLocked(() -> {
            sessionHandler.deactivate();
            sessionHandler = newHandler;
            newHandler.activate();
        });
        LOGGER.info("Switched protocol state to {}", newHandler.getProtocolState());
    }

    @Override
    public void writeFrame(byte[] data) {
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
        writeFrame(ByteBufUtil.getBytes(data));
    }

    @Override
    public void disconnect(Component reason) {
        try {
            adapter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

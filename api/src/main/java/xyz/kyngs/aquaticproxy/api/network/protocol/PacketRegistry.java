package xyz.kyngs.aquaticproxy.api.network.protocol;

import xyz.kyngs.aquaticproxy.api.ResourceOwner;
import xyz.kyngs.aquaticproxy.api.network.BackendConnection;
import xyz.kyngs.aquaticproxy.api.network.ClientConnection;
import xyz.kyngs.aquaticproxy.api.network.Connection;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.Packet;

import java.util.Map;
import java.util.function.Supplier;

public interface PacketRegistry<BC extends BackendConnection, CC extends ClientConnection> {

    <P extends Packet> PacketBuilder<P> buildPacket(ResourceOwner owner, Supplier<P> packetSupplier);

    <P extends Packet> PacketBuilder<P> buildWriteOnlyPacket(ResourceOwner owner, Class<P> clazz);

    <P extends Packet> void registerClientboundHandler(ResourceOwner owner, RegisteredPacket<P> packet, PacketHandler<P, BC> handler, int priority);

    default <P extends Packet> void registerClientboundHandler(ResourceOwner owner, Class<P> packet, PacketHandler<P, BC> handler, int priority) {
        registerClientboundHandler(owner, getRegisteredPacket(packet), handler, priority);
    }

    default <P extends Packet> void registerClientboundHandler(ResourceOwner owner, RegisteredPacket<P> packet, PacketHandler<P, BC> handler) {
        registerClientboundHandler(owner, packet, handler, 0);
    }

    default <P extends Packet> void registerClientboundHandler(ResourceOwner owner, Class<P> packet, PacketHandler<P, BC> handler) {
        registerClientboundHandler(owner, getRegisteredPacket(packet), handler, 0);
    }

    <P extends Packet> void registerServerboundHandler(ResourceOwner owner, RegisteredPacket<P> packet, PacketHandler<P, CC> handler, int priority);

    default <P extends Packet> void registerServerboundHandler(ResourceOwner owner, Class<P> packet, PacketHandler<P, CC> handler, int priority) {
        registerServerboundHandler(owner, getRegisteredPacket(packet), handler, priority);
    }

    default <P extends Packet> void registerServerboundHandler(ResourceOwner owner, RegisteredPacket<P> packet, PacketHandler<P, CC> handler) {
        registerServerboundHandler(owner, packet, handler, 0);
    }

    default <P extends Packet> void registerServerboundHandler(ResourceOwner owner, Class<P> packet, PacketHandler<P, CC> handler) {
        registerServerboundHandler(owner, getRegisteredPacket(packet), handler, 0);
    }

    <P extends Packet> RegisteredPacket<P> getRegisteredPacket(Class<P> packetClass);

    <P extends Packet> RegisteredPacket<P> getPublishedPacket(Class<P> packetClass);

    <P extends Packet> RegisteredHandler<P, BC>[] getPublishedClientboundHandlers(ProtocolState state, int protocolVersion, int packetId);

    <P extends Packet> RegisteredHandler<P, CC>[] getPublishedServerboundHandlers(ProtocolState state, int protocolVersion, int packetId);

    void publish();

    interface PacketBuilder<P extends Packet> {
        PacketBuilder<P> forState(ProtocolState state);

        PacketBuilder<P> forDirection(PacketDirection direction);

        PacketBuilder<P> forId(int protocolFrom, int protocolTo, int id);

        default PacketBuilder<P> forId(int protocol, int id) {
            forId(protocol, protocol, id);
            return this;
        }

        default PacketBuilder<P> forId(ProtocolVersion protocol, int id) {
            forId(protocol, protocol, id);
            return this;
        }


        default PacketBuilder<P> forId(ProtocolVersion from, ProtocolVersion to, int id) {
            forId(from.version(), to.version(), id);
            return this;
        }

        RegisteredPacket<P> register();
    }

    record RegisteredHandler<P extends Packet, C extends Connection>(ResourceOwner owner, PacketHandler<P, C> handler, RegisteredPacket<P> packet, int priority) implements Comparable<RegisteredHandler<?, ?>> {
        @Override
        public int compareTo(RegisteredHandler<?, ?> o) {
            return Integer.compare(o.priority, this.priority);
        }
    }

    record RegisteredPacket<P extends Packet>(ResourceOwner owner, Supplier<P> packetSupplier, Class<P> packetClass, ProtocolState state, PacketDirection packetDirection, Map<Integer, Integer> protocolPacketIdMap) {}
}

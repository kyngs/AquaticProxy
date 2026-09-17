package xyz.kyngs.aquaticproxy.network.protocol;

import xyz.kyngs.aquaticproxy.api.ResourceOwner;
import xyz.kyngs.aquaticproxy.api.network.Connection;
import xyz.kyngs.aquaticproxy.api.network.protocol.PacketDirection;
import xyz.kyngs.aquaticproxy.api.network.protocol.PacketHandler;
import xyz.kyngs.aquaticproxy.api.network.protocol.PacketRegistry;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolState;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.Packet;
import xyz.kyngs.aquaticproxy.network.AquaticBackendConnection;
import xyz.kyngs.aquaticproxy.network.AquaticClientConnection;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class AquaticPacketRegistry implements PacketRegistry<AquaticBackendConnection, AquaticClientConnection> {

    private final Map<Class<?>, RegisteredPacket<?>> registeredPackets = new ConcurrentHashMap<>();
    // state, protocol version, packet id, handlers[]
    private final Map<ProtocolState, Map<Integer, Map<Integer, Collection<RegisteredHandler<?, AquaticBackendConnection>>>>> registeredClientboundHandlers = new ConcurrentHashMap<>();
    private final Map<ProtocolState, Map<Integer, Map<Integer, Collection<RegisteredHandler<?, AquaticClientConnection>>>>> registeredServerboundHandlers = new ConcurrentHashMap<>();

    // state, protocol version, packet id, handlers[]
    private Map<ProtocolState, Map<Integer, Map<Integer, RegisteredHandler<?, AquaticBackendConnection>[]>>> publishedClientboundHandlers;
    private Map<ProtocolState, Map<Integer, Map<Integer, RegisteredHandler<?, AquaticClientConnection>[]>>> publishedServerboundHandlers;
    private Map<Class<?>, RegisteredPacket<?>> publishedPackets;

    @Override
    public <P extends Packet> PacketBuilder<P> buildPacket(ResourceOwner owner, Supplier<P> packetSupplier) {
        return new AquaticPacketBuilder<>(owner, packetSupplier, (Class<P>) packetSupplier.get().getClass());
    }

    @Override
    public <P extends Packet> PacketBuilder<P> buildWriteOnlyPacket(ResourceOwner owner, Class<P> clazz) {
        return new AquaticPacketBuilder<>(owner, () -> {
            throw new UnsupportedOperationException("This packet can not be serialized");
        }, clazz);
    }

    @Override
    public <P extends Packet> void registerClientboundHandler(ResourceOwner owner, RegisteredPacket<P> packet, PacketHandler<P, AquaticBackendConnection> handler, int priority) {
        if (packet.packetDirection() != PacketDirection.CLIENTBOUND) {
            throw new IllegalArgumentException("Cannot register a clientbound handler for a packet that is not clientbound");
        }
        registerHandler(registeredClientboundHandlers, owner, packet, handler, priority);
    }

    @Override
    public <P extends Packet> void registerServerboundHandler(ResourceOwner owner, RegisteredPacket<P> packet, PacketHandler<P, AquaticClientConnection> handler, int priority) {
        if (packet.packetDirection() != PacketDirection.SERVERBOUND) {
            throw new IllegalArgumentException("Cannot register a serverbound handler for a packet that is not serverbound");
        }
        registerHandler(registeredServerboundHandlers, owner, packet, handler, priority);
    }

    private <P extends Packet, C extends Connection> void registerHandler(Map<ProtocolState, Map<Integer, Map<Integer, Collection<RegisteredHandler<?, C>>>>> registry,
                                                                          ResourceOwner owner, RegisteredPacket<P> packet, PacketHandler<P, C> handler, int priority) {
        var packetRegistry = registry.computeIfAbsent(packet.state(), _ -> new HashMap<>());

        packet.protocolPacketIdMap().forEach((protocolVersion, packetId) -> {
            var handlers = packetRegistry.computeIfAbsent(protocolVersion, v -> new HashMap<>())
                    .computeIfAbsent(packetId, _ -> new ArrayList<>());
            handlers.add(new RegisteredHandler<>(owner, handler, packet, priority));
        });
    }

    @Override
    public <P extends Packet> RegisteredPacket<P> getRegisteredPacket(Class<P> packetClass) {
        return (RegisteredPacket<P>) registeredPackets.get(packetClass);
    }

    @Override
    public <P extends Packet> RegisteredPacket<P> getPublishedPacket(Class<P> packetClass) {
        return (RegisteredPacket<P>) publishedPackets.get(packetClass);
    }

    @Override
    public <P extends Packet> RegisteredHandler<P, AquaticBackendConnection>[] getPublishedClientboundHandlers(ProtocolState state, int protocolVersion, int packetId) {
        return getRegisteredHandlers(publishedClientboundHandlers, state, protocolVersion, packetId);
    }

    @Override
    public <P extends Packet> RegisteredHandler<P, AquaticClientConnection>[] getPublishedServerboundHandlers(ProtocolState state, int protocolVersion, int packetId) {
        return getRegisteredHandlers(publishedServerboundHandlers, state, protocolVersion, packetId);
    }

    private <P extends Packet, C extends Connection> RegisteredHandler<P, C>[] getRegisteredHandlers(Map<ProtocolState, Map<Integer, Map<Integer, RegisteredHandler<?, C>[]>>> registry, ProtocolState state, int protocolVersion, int packetId) {
        var versions = registry.get(state);

        if (versions == null) return null;

        var packetIds = versions.get(protocolVersion);

        if (packetIds == null) return null;

        return (RegisteredHandler<P, C>[]) packetIds.get(packetId);
    }

    private <P extends Packet> RegisteredPacket<P> registerPacket(ResourceOwner owner, Supplier<P> packetSupplier, Class<P> packetClass, Map<Integer, Integer> protocolPacketIdMap, ProtocolState state, PacketDirection direction) {
        RegisteredPacket<P> registeredPacket = new RegisteredPacket<>(owner, packetSupplier, packetClass, state, direction, protocolPacketIdMap);
        registeredPackets.put(packetClass, registeredPacket);
        return registeredPacket;
    }

    public void publish() {
        publishedClientboundHandlers = publishHandlers(registeredClientboundHandlers);
        publishedServerboundHandlers = publishHandlers(registeredServerboundHandlers);

        publishedPackets = Map.copyOf(registeredPackets);
    }

    private <C extends Connection> Map<ProtocolState, Map<Integer, Map<Integer, RegisteredHandler<?, C>[]>>> publishHandlers(Map<ProtocolState, Map<Integer, Map<Integer, Collection<RegisteredHandler<?, C>>>>> registry) {
        var publishedHandlers = new HashMap<ProtocolState, Map<Integer, Map<Integer, RegisteredHandler<?, C>[]>>>();

        registry.forEach((state, versions) -> {
            versions.forEach((version, packets) -> {
                packets.forEach((packetId, handlers) -> {
                    var handlersList = new ArrayList<>(handlers);
                    Collections.sort(handlersList);
                    publishedHandlers.computeIfAbsent(state, _ -> new HashMap<>())
                            .computeIfAbsent(version, _ -> new HashMap<>())
                            .put(packetId, handlersList.toArray(RegisteredHandler[]::new));
                });
            });
        });

        //todo: make submaps immutable too
        return Collections.unmodifiableMap(publishedHandlers);
    }

    public class AquaticPacketBuilder<P extends Packet> implements PacketBuilder<P> {
        private final ResourceOwner owner;
        private final Supplier<P> packetSupplier;
        private final Class<P> packetClass;
        private final Map<Integer, Integer> protocolPacketIdMap = new HashMap<>();
        private ProtocolState state;
        private PacketDirection direction;

        private AquaticPacketBuilder(ResourceOwner owner, Supplier<P> packetSupplier, Class<P> packetClass) {
            this.owner = owner;
            this.packetSupplier = packetSupplier;
            this.packetClass = packetClass;
        }

        @Override
        public PacketBuilder<P> forState(ProtocolState state) {
            this.state = state;
            return this;
        }

        @Override
        public PacketBuilder<P> forDirection(PacketDirection direction) {
            this.direction = direction;
            return this;
        }

        @Override
        public PacketBuilder<P> forId(int protocolFrom, int protocolTo, int id) {
            for (int protocol = protocolFrom; protocol <= protocolTo; protocol++) {
                protocolPacketIdMap.put(protocol, id);
            }
            return this;
        }

        @Override
        public RegisteredPacket<P> register() {
            return registerPacket(owner, packetSupplier, packetClass, protocolPacketIdMap, state, direction);
        }
    }

}

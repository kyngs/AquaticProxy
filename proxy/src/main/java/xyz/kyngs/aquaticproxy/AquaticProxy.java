package xyz.kyngs.aquaticproxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.kyngs.aquaticproxy.api.Proxy;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.BuiltinPackets;
import xyz.kyngs.aquaticproxy.network.AquaticNetworkManager;
import xyz.kyngs.aquaticproxy.network.BuiltinPacketHandlers;
import xyz.kyngs.aquaticproxy.status.StatusManager;

import java.net.BindException;

public class AquaticProxy implements Proxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(AquaticProxy.class);

    private final AquaticNetworkManager networkManager;

    public AquaticProxy() throws BindException {
        networkManager = new AquaticNetworkManager();
        var registry = networkManager.getPacketHandlerRegistry();
        BuiltinPackets.registerBuiltinPackets(this, registry);
        BuiltinPacketHandlers.registerBuiltinPacketHandlers(this, registry);

        var statusManager = new StatusManager(registry, this);

        networkManager.getPacketHandlerRegistry().publish();
        networkManager.bind("localhost", 25565);
    }

    @Override
    public String getIdentifier() {
        return "AquaticProxy";
    }
}

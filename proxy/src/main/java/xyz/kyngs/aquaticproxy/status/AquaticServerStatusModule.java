package xyz.kyngs.aquaticproxy.status;

import net.kyori.adventure.text.Component;
import xyz.kyngs.aquaticproxy.api.network.ClientConnection;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolVersion;
import xyz.kyngs.aquaticproxy.api.status.ServerStatus;
import xyz.kyngs.aquaticproxy.api.status.ServerStatusModule;

public class AquaticServerStatusModule implements ServerStatusModule {

    public AquaticServerStatusModule() {
    }

    @Override
    public void load() {
    }

    @Override
    public ServerStatus getServerStatus(ProtocolVersion version) {
        return new ServerStatus(
                version.version(),
                version.getNewestFriendlyName(),
                0,
                10,
                new ServerStatus.SamplePlayer[0],
                Component.text("AquaticProxy"),
                null
        );
    }

    @Override
    public ServerStatus getServerStatus(ClientConnection connection) {
        return getServerStatus(connection.getProtocolVersion());
    }
}

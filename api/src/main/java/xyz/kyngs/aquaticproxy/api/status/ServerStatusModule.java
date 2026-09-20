package xyz.kyngs.aquaticproxy.api.status;

import net.kyori.adventure.key.Key;
import xyz.kyngs.aquaticproxy.api.module.Module;
import xyz.kyngs.aquaticproxy.api.module.ModuleKey;
import xyz.kyngs.aquaticproxy.api.network.ClientConnection;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolVersion;

public interface ServerStatusModule extends Module {
    ModuleKey<ServerStatusModule> KEY = new ModuleKey<>(Key.key("aquaticproxy", "server_ping"), ServerStatusModule.class);

    ServerStatus getServerStatus(ProtocolVersion version);

    ServerStatus getServerStatus(ClientConnection connection);
}

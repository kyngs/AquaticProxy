package xyz.kyngs.aquaticproxy.api.network;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;
import xyz.kyngs.aquaticproxy.api.backend.BackendServer;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolState;
import xyz.kyngs.aquaticproxy.api.network.session.BackendSessionHandler;

public interface BackendConnection extends Connection {
    void switchProtocolState(BackendSessionHandler newHandler);

    BackendSessionHandler getSessionHandler();

    @Nullable ClientConnection getPairedConnection();

    BackendServer getServer();

    default void disconnect(Component reason) {}
}

package xyz.kyngs.aquaticproxy.api.network;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;
import xyz.kyngs.aquaticproxy.api.backend.BackendServer;
import xyz.kyngs.aquaticproxy.api.network.session.BackendSessionHandler;

import java.util.concurrent.CompletableFuture;

public interface BackendConnection extends Connection {
    void switchProtocolState(BackendSessionHandler newHandler);

    BackendSessionHandler getSessionHandler();

    @Nullable ClientConnection getPairedConnection();

    BackendServer getServer();

    CompletableFuture<ConnectionResult> getConnectionFuture();

    record ConnectionResult(boolean success, @Nullable Component reason) {}
}

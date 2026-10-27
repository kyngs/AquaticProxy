package xyz.kyngs.aquaticproxy.api.player;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;
import xyz.kyngs.aquaticproxy.api.backend.BackendConnectionException;
import xyz.kyngs.aquaticproxy.api.backend.BackendServer;
import xyz.kyngs.aquaticproxy.api.network.BackendConnection;
import xyz.kyngs.aquaticproxy.api.network.ClientConnection;
import xyz.kyngs.aquaticproxy.api.util.GameProfile;

import java.util.UUID;
import java.util.concurrent.Future;

public interface Player extends Audience {

    UUID getId();

    GameProfile getProfile();

    default String getUsername() {
        return getProfile().username();
    }

    ClientConnection getConnection();

    // successful connect = reached at least configuration phase
    BackendConnection connectToBackend(BackendServer server) throws BackendConnectionException;

    default BackendConnection getBackendConnection() {
        return getConnection().getPairedConnection();
    }

    default BackendServer getBackendServer() {
        if (getBackendConnection() == null) {
            return null;
        }
        return getBackendConnection().getServer();
    }

    default void disconnect() {
        disconnect(Component.empty());
    }

    void disconnect(Component reason);

    void finalizeDisconnection();
}

package xyz.kyngs.aquaticproxy.api.player;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;
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

    BackendConnectionResult connectToBackend(BackendServer server);

    default BackendConnection getBackendConnection() {
        return getConnection().getPairedConnection();
    }

    default BackendServer getBackendServer() {
        if (getBackendConnection() == null) {
            return null;
        }
        return getBackendConnection().getServer();
    }

    record BackendConnectionResult(BackendConnection connection, boolean success, @Nullable Component reason) {}

}

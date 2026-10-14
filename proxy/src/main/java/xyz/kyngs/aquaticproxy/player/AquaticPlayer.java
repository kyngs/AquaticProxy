package xyz.kyngs.aquaticproxy.player;

import net.kyori.adventure.text.Component;
import xyz.kyngs.aquaticproxy.api.backend.BackendServer;
import xyz.kyngs.aquaticproxy.api.network.BackendConnection;
import xyz.kyngs.aquaticproxy.api.network.ClientConnection;
import xyz.kyngs.aquaticproxy.api.network.NetworkModule;
import xyz.kyngs.aquaticproxy.api.player.Player;
import xyz.kyngs.aquaticproxy.api.util.GameProfile;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class AquaticPlayer implements Player {

    private final UUID uuid;
    private final GameProfile profile;
    private final ClientConnection connection;
    private final NetworkModule networkModule;
    private BackendConnection pendingConnection;

    public AquaticPlayer(GameProfile profile, ClientConnection connection, NetworkModule networkModule) {
        this.uuid = profile.uuid();
        this.profile = profile;
        this.connection = connection;
        this.networkModule = networkModule;
    }

    @Override
    public ClientConnection getConnection() {
        return connection;
    }

    @Override
    public BackendConnectionResult connectToBackend(BackendServer server) {
        if (pendingConnection != null) {
            pendingConnection.disconnect(Component.text("Connection cancelled due to new connection request."));
            pendingConnection = null;
        }

        pendingConnection = networkModule.openBackendConnection(server, connection);

        BackendConnection.LoginResult loginResult;

        try {
            loginResult = pendingConnection.getLoginFuture().get();

            if (!loginResult.success()) {
                pendingConnection.disconnect(loginResult.reason());
                return new BackendConnectionResult(pendingConnection, false, loginResult.reason());
            }
        } catch (InterruptedException _) {
            Thread.currentThread().interrupt();
            pendingConnection.disconnect(Component.text("Connection cancelled due to thread interruption."));
            return new BackendConnectionResult(pendingConnection, false, Component.text("Connection cancelled due to thread interruption."));
        } catch (ExecutionException e) {
            return new BackendConnectionResult(pendingConnection, false, Component.text("Connection failed due to an error: " + e.getCause().getMessage()));
        }

        connection.doWriteLocked(() -> connection.switchPairedConnection(pendingConnection));

        return new BackendConnectionResult(pendingConnection, true, null);
    }

    @Override
    public UUID getId() {
        return uuid;
    }

    @Override
    public GameProfile getProfile() {
        return profile;
    }

    @Override
    public String toString() {
        return "AquaticPlayer{" +
               "uuid=" + uuid +
               ", profile=" + profile +
               ", connection=" + connection +
               '}';
    }
}

package xyz.kyngs.aquaticproxy.player;

import net.kyori.adventure.text.Component;
import xyz.kyngs.aquaticproxy.api.backend.BackendConnectionException;
import xyz.kyngs.aquaticproxy.api.backend.BackendServer;
import xyz.kyngs.aquaticproxy.api.network.BackendConnection;
import xyz.kyngs.aquaticproxy.api.network.ClientConnection;
import xyz.kyngs.aquaticproxy.api.network.NetworkModule;
import xyz.kyngs.aquaticproxy.api.player.Player;
import xyz.kyngs.aquaticproxy.api.player.PlayerModule;
import xyz.kyngs.aquaticproxy.api.util.GameProfile;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class AquaticPlayer implements Player {

    private final UUID uuid;
    private final GameProfile profile;
    private final ClientConnection connection;
    private final NetworkModule networkModule;
    private final PlayerModule playerModule;
    private BackendConnection pendingConnection;

    public AquaticPlayer(GameProfile profile, ClientConnection connection, NetworkModule networkModule, PlayerModule playerModule) {
        this.uuid = profile.uuid();
        this.profile = profile;
        this.connection = connection;
        this.networkModule = networkModule;
        this.playerModule = playerModule;
    }

    @Override
    public ClientConnection getConnection() {
        return connection;
    }

    @Override
    public BackendConnection connectToBackend(BackendServer server) {
        try {
            if (pendingConnection != null) {
                pendingConnection.disconnect(Component.text("Connection cancelled due to new connection request."));
                pendingConnection = null;
            }

            pendingConnection = networkModule.openBackendConnection(server, connection);

            BackendConnection.ConnectionResult connectionResult;

            connectionResult = pendingConnection.getConnectionFuture().get();

            if (!connectionResult.success()) {
                pendingConnection.disconnect(connectionResult.reason());
                throw new BackendConnectionException(connectionResult.reason());
            }

            connection.doWriteLocked(() -> connection.switchPairedConnection(pendingConnection));

            return pendingConnection;
        } catch ( BackendConnectionException e) {
            throw e;
        } catch (RuntimeException | ExecutionException | InterruptedException e) {
            throw new BackendConnectionException(e);
        }
    }

    @Override
    public void disconnect(Component reason) {
        connection.disconnect(reason);
    }

    @Override
    public void finalizeDisconnection() {
        if (pendingConnection != null) {
            pendingConnection.disconnect(Component.text("Connection cancelled due to player disconnect."));
        }

        playerModule.unregisterPlayer(this);
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
        return profile.username() + " (" + uuid + ")";
    }
}

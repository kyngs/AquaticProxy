package xyz.kyngs.aquaticproxy.player;

import xyz.kyngs.aquaticproxy.api.network.ClientConnection;
import xyz.kyngs.aquaticproxy.api.player.Player;
import xyz.kyngs.aquaticproxy.api.util.GameProfile;

import java.util.UUID;

public class AquaticPlayer implements Player {

    private final UUID uuid;
    private final GameProfile profile;
    private final ClientConnection connection;

    public AquaticPlayer(GameProfile profile, ClientConnection connection) {
        this.uuid = profile.uuid();
        this.profile = profile;
        this.connection = connection;
    }

    @Override
    public ClientConnection getConnection() {
        return connection;
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

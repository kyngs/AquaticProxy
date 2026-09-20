package xyz.kyngs.aquaticproxy.player;

import xyz.kyngs.aquaticproxy.api.network.ClientConnection;
import xyz.kyngs.aquaticproxy.api.player.Player;
import xyz.kyngs.aquaticproxy.api.player.PlayerModule;
import xyz.kyngs.aquaticproxy.api.util.GameProfile;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AquaticPlayerModule implements PlayerModule {
    private final Map<UUID, Player> players;

    public AquaticPlayerModule() {
        this.players = new ConcurrentHashMap<>();;
    }

    @Override
    public Player registerPlayer(GameProfile profile, ClientConnection connection) {
        var player = new AquaticPlayer(profile, connection);

        players.put(profile.uuid(), player);

        return player;
    }
}

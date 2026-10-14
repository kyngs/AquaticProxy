package xyz.kyngs.aquaticproxy.player;

import xyz.kyngs.aquaticproxy.api.module.ModuleManager;
import xyz.kyngs.aquaticproxy.api.network.ClientConnection;
import xyz.kyngs.aquaticproxy.api.player.Player;
import xyz.kyngs.aquaticproxy.api.player.PlayerModule;
import xyz.kyngs.aquaticproxy.api.util.GameProfile;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AquaticPlayerModule implements PlayerModule {
    private final Map<UUID, Player> players;
    private final ModuleManager moduleManager;

    public AquaticPlayerModule(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
        this.players = new ConcurrentHashMap<>();;
    }

    @Override
    public Player registerPlayer(GameProfile profile, ClientConnection connection) {
        var player = new AquaticPlayer(profile, connection, moduleManager.getNetworkModule());

        players.put(profile.uuid(), player);

        return player;
    }
}

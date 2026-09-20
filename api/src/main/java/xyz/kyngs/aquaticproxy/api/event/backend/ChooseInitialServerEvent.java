package xyz.kyngs.aquaticproxy.api.event.backend;

import net.kyori.adventure.key.Key;
import xyz.kyngs.aquaticproxy.api.backend.BackendServer;
import xyz.kyngs.aquaticproxy.api.event.Event;
import xyz.kyngs.aquaticproxy.api.event.EventKey;
import xyz.kyngs.aquaticproxy.api.network.ClientConnection;
import xyz.kyngs.aquaticproxy.api.player.Player;

public class ChooseInitialServerEvent implements Event {

    public static final EventKey<ChooseInitialServerEvent> KEY = new EventKey<>(Key.key("aquaticproxy", "choose_initial_server"), ChooseInitialServerEvent.class);
    private final Player player;
    private BackendServer server;

    public ChooseInitialServerEvent(BackendServer server, Player player) {
        this.server = server;
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public BackendServer getServer() {
        return server;
    }

    public void setServer(BackendServer server) {
        this.server = server;
    }

    @Override
    public EventKey<? extends Event> getKey() {
        return KEY;
    }
}

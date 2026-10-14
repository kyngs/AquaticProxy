package xyz.kyngs.aquaticproxy.api.event.backend;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Nullable;
import xyz.kyngs.aquaticproxy.api.backend.BackendServer;
import xyz.kyngs.aquaticproxy.api.event.AbstractPlayerEvent;
import xyz.kyngs.aquaticproxy.api.event.AbstractPlayerResultedEvent;
import xyz.kyngs.aquaticproxy.api.event.Event;
import xyz.kyngs.aquaticproxy.api.event.EventKey;
import xyz.kyngs.aquaticproxy.api.network.ClientConnection;
import xyz.kyngs.aquaticproxy.api.player.Player;

public class ChooseInitialServerEvent extends AbstractPlayerResultedEvent<BackendServer> {

    public static final EventKey<ChooseInitialServerEvent> KEY = new EventKey<>(Key.key("aquaticproxy", "choose_initial_server"), ChooseInitialServerEvent.class);

    public ChooseInitialServerEvent(Player player) {
        super(player);
    }

    public ChooseInitialServerEvent(Player player, @Nullable BackendServer server) {
        super(player, server);
    }

    public BackendServer getServer() {
        return getResult();
    }

    public void setServer(BackendServer server) {
        setResult(server);
    }

    @Override
    public EventKey<? extends Event> getKey() {
        return KEY;
    }
}

package xyz.kyngs.aquaticproxy.api.event.backend;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Nullable;
import xyz.kyngs.aquaticproxy.api.backend.BackendConnectionException;
import xyz.kyngs.aquaticproxy.api.backend.BackendServer;
import xyz.kyngs.aquaticproxy.api.event.AbstractPlayerResultedEvent;
import xyz.kyngs.aquaticproxy.api.event.Event;
import xyz.kyngs.aquaticproxy.api.event.EventKey;
import xyz.kyngs.aquaticproxy.api.player.Player;

public class ChooseInitialServerEvent extends AbstractPlayerResultedEvent<BackendServer> {

    public static final EventKey<ChooseInitialServerEvent> KEY = new EventKey<>(Key.key("aquaticproxy", "choose_initial_server"), ChooseInitialServerEvent.class);

    @Nullable
    private final BackendConnectionException previousAttempt;

    public ChooseInitialServerEvent(Player player, @Nullable BackendConnectionException previousAttempt) {
        super(player);
        this.previousAttempt = previousAttempt;
    }

    public ChooseInitialServerEvent(Player player, @Nullable BackendServer server, @Nullable BackendConnectionException previousAttempt) {
        super(player, server);
        this.previousAttempt = previousAttempt;
    }

    public BackendServer getServer() {
        return getResult();
    }

    public void setServer(BackendServer server) {
        setResult(server);
    }

    public @Nullable BackendConnectionException getPreviousAttempt() {
        return previousAttempt;
    }

    @Override
    public EventKey<? extends Event> getKey() {
        return KEY;
    }
}

package xyz.kyngs.aquaticproxy.api.event.backend;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import xyz.kyngs.aquaticproxy.api.backend.BackendServer;
import xyz.kyngs.aquaticproxy.api.event.AbstractPlayerResultedEvent;
import xyz.kyngs.aquaticproxy.api.event.Event;
import xyz.kyngs.aquaticproxy.api.event.EventKey;
import xyz.kyngs.aquaticproxy.api.player.Player;

public class KickedFromServerEvent extends AbstractPlayerResultedEvent<KickedFromServerEvent.Result> {
    public static final EventKey<KickedFromServerEvent> KEY = new EventKey<>(Key.key("aquaticproxy", "kicked_from_server"), KickedFromServerEvent.class);

    private final boolean connected;

    public KickedFromServerEvent(Player player, boolean connected) {
        super(player);
        this.connected = connected;
    }

    public KickedFromServerEvent(Player player, Result result, boolean connected) {
        super(player, result);
        this.connected = connected;
    }

    @Override
    public EventKey<? extends Event> getKey() {
        return KEY;
    }

    public boolean isConnected() {
        return connected;
    }

    public void kick(Component reason) {
        setResult(new Kick(reason));
    }

    public void redirect(BackendServer server) {
        setResult(new Redirect(server));
    }

    public void none() {
        setResult(new None());
    }

    @Override
    public void setResult(Result result) {
        if (connected && result instanceof None) {
            throw new IllegalArgumentException("Cannot set result to None if the player is not connected to a server.");
        }
        super.setResult(result);
    }

    public sealed interface Result permits Kick, Redirect, None {
    }

    public record Kick(Component reason) implements Result {
    }

    public record Redirect(BackendServer server) implements Result {
    }

    public record None() implements Result {
    }
}

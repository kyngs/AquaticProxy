package xyz.kyngs.aquaticproxy.api.event;

import xyz.kyngs.aquaticproxy.api.player.Player;

public abstract class AbstractPlayerResultedEvent<R> extends AbstractResultedEvent<R> implements PlayerEvent {
    protected final Player player;

    public AbstractPlayerResultedEvent(Player player) {
        this.player = player;
    }

    public AbstractPlayerResultedEvent(Player player, R result) {
        super(result);
        this.player = player;
    }

    @Override
    public Player getPlayer() {
        return player;
    }
}

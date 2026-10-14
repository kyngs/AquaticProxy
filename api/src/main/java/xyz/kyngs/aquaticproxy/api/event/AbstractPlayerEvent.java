package xyz.kyngs.aquaticproxy.api.event;

import xyz.kyngs.aquaticproxy.api.player.Player;

public abstract class AbstractPlayerEvent implements PlayerEvent {
    protected final Player player;

    public AbstractPlayerEvent(Player player) {
        this.player = player;
    }

    @Override
    public Player getPlayer() {
        return player;
    }
}

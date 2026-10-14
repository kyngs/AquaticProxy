package xyz.kyngs.aquaticproxy.api.event;

import xyz.kyngs.aquaticproxy.api.player.Player;

public interface PlayerEvent extends Event {
    Player getPlayer();
}

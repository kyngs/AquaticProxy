package xyz.kyngs.aquaticproxy.api.network.protocol.packet.play;

import net.kyori.adventure.text.Component;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.shared.DisconnectPacket;

public class PlayDisconnectPacket extends DisconnectPacket {
    public PlayDisconnectPacket(Component reason) {
        super(reason);
    }

    public PlayDisconnectPacket() {
    }
}

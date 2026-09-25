package xyz.kyngs.aquaticproxy.network.session.client;

import net.kyori.adventure.text.Component;
import xyz.kyngs.aquaticproxy.api.network.protocol.PacketHandler;
import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolState;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.configuration.FinishConfigurationPacket;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.play.AcknowledgeConfigurationPacket;
import xyz.kyngs.aquaticproxy.api.network.protocol.packet.play.PlayDisconnectPacket;
import xyz.kyngs.aquaticproxy.api.network.session.ClientSessionHandler;
import xyz.kyngs.aquaticproxy.api.player.Player;
import xyz.kyngs.aquaticproxy.module.AquaticModuleManager;
import xyz.kyngs.aquaticproxy.network.AquaticClientConnection;

public class PlaySessionHandler implements ClientSessionHandler {

    private final AquaticClientConnection connection;
    private final Player player;
    private final AquaticModuleManager moduleManager;

    public PlaySessionHandler(AquaticClientConnection connection, Player player, AquaticModuleManager moduleManager) {
        this.connection = connection;
        this.player = player;
        this.moduleManager = moduleManager;
    }

    @Override
    public ProtocolState getProtocolState() {
        return ProtocolState.PLAY;
    }

    @Override
    public void activate() {

    }

    @Override
    public void sendDisconnectReason(Component reason) {
        connection.writePacket(new PlayDisconnectPacket(reason));
    }

    @Override
    public void deactivate() {

    }

    public void switchToConfiguration() {
        var pc = connection.getPairedConnection();
        if (pc != null) pc.writePacket(new AcknowledgeConfigurationPacket());
        connection.switchProtocolState(new PlaySessionHandler(connection, player, moduleManager));
    }

    @Override
    public PacketHandler.Result handle(AcknowledgeConfigurationPacket packet) {
        switchToConfiguration();
        return PacketHandler.Result.CANCELLED;
    }
}

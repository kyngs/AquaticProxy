package xyz.kyngs.aquaticproxy.api.network;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.Nullable;
import xyz.kyngs.aquaticproxy.api.network.session.ClientSessionHandler;
import xyz.kyngs.aquaticproxy.api.player.Player;

import java.util.Set;

public interface ClientConnection extends Connection {

    void switchProtocolState(ClientSessionHandler newHandler);

    ClientSessionHandler getSessionHandler();

    @Nullable BackendConnection getPairedConnection();

    default Set<ByteBuf> getQueuedConfigurationFrames() {
        return Set.of();
    }

    @Nullable Player getPlayer();
}

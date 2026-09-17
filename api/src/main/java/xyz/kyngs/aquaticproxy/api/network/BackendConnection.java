package xyz.kyngs.aquaticproxy.api.network;

import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolState;
import xyz.kyngs.aquaticproxy.api.network.session.BackendSessionHandler;

public interface BackendConnection extends Connection {
    void switchProtocolState(BackendSessionHandler newHandler);

    BackendSessionHandler getSessionHandler();
}

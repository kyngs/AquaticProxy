package xyz.kyngs.aquaticproxy.api.network.session;

import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolState;

public interface SessionHandler {
    ProtocolState getProtocolState();

    void activate();

    void deactivate();
}

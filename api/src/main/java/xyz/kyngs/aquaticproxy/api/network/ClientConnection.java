package xyz.kyngs.aquaticproxy.api.network;

import xyz.kyngs.aquaticproxy.api.network.session.ClientSessionHandler;

public interface ClientConnection extends Connection {

    void switchProtocolState(ClientSessionHandler newHandler);

    ClientSessionHandler getSessionHandler();

}

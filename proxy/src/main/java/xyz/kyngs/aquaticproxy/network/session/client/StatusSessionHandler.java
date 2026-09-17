package xyz.kyngs.aquaticproxy.network.session.client;

import xyz.kyngs.aquaticproxy.api.network.protocol.ProtocolState;
import xyz.kyngs.aquaticproxy.api.network.session.ClientSessionHandler;

public class StatusSessionHandler implements ClientSessionHandler {

    public StatusSessionHandler() {
        super();
    }

    @Override
    public ProtocolState getProtocolState() {
        return ProtocolState.STATUS;
    }

    @Override
    public void activate() {
    }

    @Override
    public void deactivate() {
    }

}

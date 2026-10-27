package xyz.kyngs.aquaticproxy.api.network.protocol;

public enum ProtocolState {
    HANDSHAKE(0),
    STATUS(1),
    LOGIN(1),
    CONFIGURATION(2),
    PLAY(3);

    private final int order;

    ProtocolState(int order) {
        this.order = order;
    }

    public boolean isAtLeast(ProtocolState other) {
        return this.order >= other.order;
    }
}

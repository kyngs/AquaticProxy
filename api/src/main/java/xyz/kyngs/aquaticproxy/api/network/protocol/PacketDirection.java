package xyz.kyngs.aquaticproxy.api.network.protocol;

public enum PacketDirection {
    CLIENTBOUND,
    SERVERBOUND;

    public PacketDirection opposite() {
        return this == CLIENTBOUND ? SERVERBOUND : CLIENTBOUND;
    }
}

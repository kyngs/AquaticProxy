package xyz.kyngs.aquaticproxy.api;

public interface Proxy extends ResourceOwner {

    void stop();

    State getState();

    enum State {
        STARTING,
        RUNNING,
        STOPPING,
    }
}

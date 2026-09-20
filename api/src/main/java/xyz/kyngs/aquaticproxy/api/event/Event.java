package xyz.kyngs.aquaticproxy.api.event;

public interface Event {
    EventKey<? extends Event> getKey();
}

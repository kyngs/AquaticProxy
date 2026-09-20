package xyz.kyngs.aquaticproxy.api.event;

public interface ResultedEvent<R> extends Event {
    R getResult();

    void setResult(R result);
}

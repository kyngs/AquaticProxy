package xyz.kyngs.aquaticproxy.api.event;

public abstract class AbstractResultedEvent<R> implements ResultedEvent<R> {

    private R result;

    public AbstractResultedEvent() {
    }

    public AbstractResultedEvent(R result) {
        this.result = result;
    }

    @Override
    public R getResult() {
        return result;
    }

    @Override
    public void setResult(R result) {
        this.result = result;
    }
}

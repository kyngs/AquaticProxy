package xyz.kyngs.aquaticproxy.api.plugin;

import xyz.kyngs.aquaticproxy.api.ResourceOwner;

import java.util.Objects;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class RegisteredPlugin {
    private final PluginDescription description;
    private final Plugin instance;
    private final ReentrantReadWriteLock stateLock;
    private final ResourceOwner parent;
    private State state;

    public RegisteredPlugin(ResourceOwner parent, PluginDescription description, Plugin instance, State state) {
        this.description = description;
        this.instance = instance;
        this.state = state;
        this.stateLock = new ReentrantReadWriteLock();
        this.parent = parent;
    }

    public PluginDescription getDescription() {
        return description;
    }

    public Plugin getInstance() {
        return instance;
    }

    public State getState() {
        try {
            stateLock.readLock().lock();
            return state;
        } finally {
            stateLock.readLock().unlock();
        }
    }

    public void setState(State state) {
        if (!stateLock.writeLock().isHeldByCurrentThread()) {
            throw new IllegalStateException("State lock must be held by the current thread to set state.");
        }
        this.state = state;
    }

    public ReentrantReadWriteLock getStateLock() {
        return stateLock;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (RegisteredPlugin) obj;
        return Objects.equals(this.description, that.description) &&
               Objects.equals(this.instance, that.instance) &&
               Objects.equals(this.state, that.state) &&
               Objects.equals(this.stateLock, that.stateLock);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, instance, state, stateLock);
    }

    @Override
    public String toString() {
        return "RegisteredPlugin[" +
               "description=" + description + ", " +
               "instance=" + instance + ", " +
               "state=" + state + ", " +
               "stateLock=" + stateLock + ']';
    }

    public ResourceOwner getParent() {
        return parent;
    }

    public enum State {
        INSTANTIATED,
        LOADED,
        ENABLED,
    }
}

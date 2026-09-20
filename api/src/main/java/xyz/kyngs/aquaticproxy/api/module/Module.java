package xyz.kyngs.aquaticproxy.api.module;

public interface Module {
    default void load() {}

    default void enable() {}

    default void disable() {}
}

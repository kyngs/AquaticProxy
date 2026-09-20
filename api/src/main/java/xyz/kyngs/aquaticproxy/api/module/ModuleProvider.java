package xyz.kyngs.aquaticproxy.api.module;

import xyz.kyngs.aquaticproxy.api.ResourceOwner;

import java.util.function.Supplier;

public record ModuleProvider<M extends Module>(ModuleKey<? extends M> key, ResourceOwner owner, Supplier<M> provider, int priority) implements Comparable<ModuleProvider<?>> {
    @Override
    public int compareTo(ModuleProvider<?> o) {
        return Integer.compare(this.priority, o.priority);
    }
}

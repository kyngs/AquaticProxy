package xyz.kyngs.aquaticproxy.api.event;

import net.kyori.adventure.key.Key;
import xyz.kyngs.aquaticproxy.api.ResourceOwner;
import xyz.kyngs.aquaticproxy.api.module.Module;
import xyz.kyngs.aquaticproxy.api.module.ModuleKey;

import java.util.function.Consumer;

public interface EventModule extends Module {

    ModuleKey<EventModule> KEY = new ModuleKey<>(Key.key("aquaticproxy", "event"), EventModule.class);

    default <E extends Event> void subscribe(ResourceOwner owner, EventKey<E> event, Consumer<E> listener) {
        subscribe(owner, event, listener, EventPriority.NORMAL);
    }

    <E extends Event> void subscribe(ResourceOwner owner, EventKey<E> event, Consumer<E> listener, EventPriority order);

    <E extends Event> void fire(EventKey<E> key, E event);

    default <E extends Event> void fireAndForget(EventKey<E> key, E event) {
        Thread.startVirtualThread(() -> fire(key, event));
    }

}

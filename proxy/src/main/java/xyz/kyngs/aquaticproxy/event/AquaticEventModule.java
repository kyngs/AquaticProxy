package xyz.kyngs.aquaticproxy.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.kyngs.aquaticproxy.AquaticProxy;
import xyz.kyngs.aquaticproxy.api.ResourceOwner;
import xyz.kyngs.aquaticproxy.api.event.Event;
import xyz.kyngs.aquaticproxy.api.event.EventKey;
import xyz.kyngs.aquaticproxy.api.event.EventModule;
import xyz.kyngs.aquaticproxy.api.event.EventPriority;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class AquaticEventModule implements EventModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(AquaticEventModule.class);

    private final Map<EventKey<?>, List<EventSubscriber<?>>> subscribers = new ConcurrentHashMap<>();
    private final AtomicBoolean enabled = new AtomicBoolean(false);

    public AquaticEventModule(AquaticProxy proxy) {

    }

    @Override
    public void disable() {
        enabled.set(false);
    }

    @Override
    public void enable() {
        subscribers.values().forEach(list -> list.sort(Comparator.naturalOrder()));
        enabled.set(true);
    }

    @Override
    public <E extends Event> void subscribe(ResourceOwner owner, EventKey<E> event, Consumer<E> listener, EventPriority order) {
        var subscriber = new EventSubscriber<>(owner, listener, order);
        var list = subscribers.computeIfAbsent(event, _ -> new CopyOnWriteArrayList<>());
        list.add(subscriber);
        if (enabled.get()) {
            list.sort(Comparator.naturalOrder());
        }
    }

    @Override
    public <E extends Event> void fire(E event) {
        var list = subscribers.get(event.getKey());
        if (list == null) {
            return;
        }
        for (var subscriber : list) {
            try {
                ((Consumer<E>) subscriber.listener()).accept(event);
            } catch (Exception e) {
                LOGGER.error("Error while firing event {} to subscriber owned by {}: {}", event.getKey(), subscriber.owner(), e.getMessage(), e);
            }
        }
    }

    public record EventSubscriber<E extends Event>(ResourceOwner owner, Consumer<E> listener, EventPriority order) implements Comparable<EventSubscriber<?>> {
        @Override
        public int compareTo(EventSubscriber<?> o) {
            return Integer.compare(this.order.priority(), o.order.priority());
        }
    }
}

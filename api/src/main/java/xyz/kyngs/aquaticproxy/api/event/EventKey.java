package xyz.kyngs.aquaticproxy.api.event;

import net.kyori.adventure.key.Key;
import xyz.kyngs.aquaticproxy.api.module.Module;

public record EventKey<E extends Event>(Key key, Class<? extends E> eventClazz) {

}

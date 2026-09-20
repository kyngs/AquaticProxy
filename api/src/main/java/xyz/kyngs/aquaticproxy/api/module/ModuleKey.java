package xyz.kyngs.aquaticproxy.api.module;

import net.kyori.adventure.key.Key;

public record ModuleKey<M extends Module>(Key key, Class<? extends M> moduleInterface) {
    
}

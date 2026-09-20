package xyz.kyngs.aquaticproxy.api.plugin;

import xyz.kyngs.aquaticproxy.api.ResourceOwner;
import xyz.kyngs.aquaticproxy.api.module.ModuleManager;

public interface Plugin extends ResourceOwner {

    void load(ModuleManager moduleManager);

    void enable();

    void disable();

    void unload(ModuleManager moduleManager);

}

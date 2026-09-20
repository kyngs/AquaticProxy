package xyz.kyngs.aquaticproxy.api.module;

import xyz.kyngs.aquaticproxy.api.ResourceOwner;

import java.util.List;
import java.util.function.Supplier;

public interface ModuleManager {

    <M extends Module> List<ModuleProvider<M>> getRegisteredProviders(ModuleKey<M> key);

    <M extends Module> ModuleProvider<M> registerProvider(ResourceOwner owner, ModuleKey<M> key, int priority, Supplier<M> provider);

    <M extends Module> M getModule(ModuleKey<M> key);

}

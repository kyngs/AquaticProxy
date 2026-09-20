package xyz.kyngs.aquaticproxy.module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.kyngs.aquaticproxy.api.ResourceOwner;
import xyz.kyngs.aquaticproxy.api.module.Module;
import xyz.kyngs.aquaticproxy.api.module.ModuleKey;
import xyz.kyngs.aquaticproxy.api.module.ModuleManager;
import xyz.kyngs.aquaticproxy.api.module.ModuleProvider;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

public class AquaticModuleManager implements ModuleManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(AquaticModuleManager.class);

    private final Map<ModuleKey<?>, List<ModuleProvider<?>>> registeredProviders = new ConcurrentHashMap<>();
    private final Map<ModuleKey<?>, Module> loadedModules = new HashMap<>();

    @Override
    public <M extends Module> List<ModuleProvider<M>> getRegisteredProviders(ModuleKey<M> key) {
        return (List<ModuleProvider<M>>) (List<?>) registeredProviders.getOrDefault(key, List.of());
    }

    @Override
    public <M extends Module> ModuleProvider<M> registerProvider(ResourceOwner owner, ModuleKey<M> key, int priority, Supplier<M> supplier) {
        var provider = new ModuleProvider<>(key, owner, supplier, priority);
        registeredProviders.computeIfAbsent(key, _ -> new CopyOnWriteArrayList<>()).add(provider);
        return provider;
    }

    @Override
    public <M extends Module> M getModule(ModuleKey<M> key) {
        return (M) loadedModules.get(key);
    }

    public void loadModules() {
        registeredProviders.forEach((key, providers) -> {
            if (providers.isEmpty()) {
                return;
            }
            providers.sort(Comparator.naturalOrder());
            for (int i = providers.size() - 1; i >= 0; i--) {
                try {
                    var module = providers.get(i).provider().get();
                    if (module != null) {
                        loadedModules.put(key, module);
                        break;
                    }
                } catch (Exception e) {
                    LOGGER.error("Failed to load module for key {} from provider owned by {}: {}", key, providers.get(i).owner(), e.getMessage(), e);
                }
            }
        });

        loadedModules.forEach((key, module) -> {
            try {
                module.load();
            } catch (Exception e) {
                LOGGER.error("Failed to load module for key {}: {}", key, e.getMessage(), e);
            }
        });
    }

    public void enableModules() {
        loadedModules.forEach((key, module) -> {
            try {
                module.enable();
            } catch (Exception e) {
                LOGGER.error("Failed to enable module for key {}: {}", key, e.getMessage(), e);
            }
        });
    }

    public void disableModules() {
        loadedModules.forEach((key, module) -> {
            try {
                module.disable();
            } catch (Exception e) {
                LOGGER.error("Failed to disable module for key {}: {}", key, e.getMessage(), e);
            }
        });
    }

    public void unloadModules() {
        loadedModules.clear();
    }

}

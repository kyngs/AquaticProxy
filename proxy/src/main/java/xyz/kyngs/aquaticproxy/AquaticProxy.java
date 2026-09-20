package xyz.kyngs.aquaticproxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.kyngs.aquaticproxy.api.Proxy;
import xyz.kyngs.aquaticproxy.api.event.EventModule;
import xyz.kyngs.aquaticproxy.api.status.ServerStatusModule;
import xyz.kyngs.aquaticproxy.api.network.NetworkModule;
import xyz.kyngs.aquaticproxy.event.AquaticEventModule;
import xyz.kyngs.aquaticproxy.module.AquaticModuleManager;
import xyz.kyngs.aquaticproxy.status.AquaticServerStatusModule;
import xyz.kyngs.aquaticproxy.network.AquaticNetworkModule;
import xyz.kyngs.aquaticproxy.plugin.AquaticPluginManager;

import java.net.BindException;
import java.util.concurrent.atomic.AtomicReference;

public class AquaticProxy implements Proxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(AquaticProxy.class);

    private final AquaticModuleManager moduleManager;
    private final AquaticPluginManager pluginManager;
    private final AtomicReference<State> state;

    public AquaticProxy() {
        this.state = new AtomicReference<>(State.STARTING);

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdownHook));

        moduleManager = new AquaticModuleManager();
        pluginManager = new AquaticPluginManager(moduleManager);

        registerBuiltinModules();

        pluginManager.discoverPlugins(this);
        pluginManager.loadPlugins(this);

        moduleManager.loadModules();

        pluginManager.enablePlugins(this);

        moduleManager.enableModules();

        this.state.set(State.RUNNING);

        try {
            moduleManager.getModule(NetworkModule.KEY).bind("localhost", 25565);
        } catch (BindException e) {
            LOGGER.error("Failed to bind network module to localhost:25565. Is another server running on this port?", e);
            stop();
            return;
        }

        startDummyThread();

        LOGGER.info("AquaticProxy started successfully.");
    }

    private void shutdownHook() {
        if (getState() == State.STOPPING) {
            return;
        }
        LOGGER.info("Interrupt signal received.");

        stop(false);
    }

    @Override
    public void stop() {
        stop(true);
    }

    public void stop(boolean exit) {
        if (!state.compareAndSet(State.RUNNING, State.STOPPING)) {
            LOGGER.warn("Ignoring stop request because the proxy is not started or is already stopping.");
            return;
        }

        moduleManager.disableModules();

        pluginManager.disablePlugins(this);

        moduleManager.unloadModules();

        pluginManager.unloadPlugins(this);

        LOGGER.info("AquaticProxy exiting. Goodbye!");

        if (exit) {
            System.exit(0);
        }
    }

    @Override
    public String getIdentifier() {
        return "AquaticProxy";
    }

    @Override
    public State getState() {
        return state.get();
    }

    private void startDummyThread() {
        Thread dummyThread = new Thread(() -> {
            while (getState() == State.RUNNING) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        dummyThread.setDaemon(false);
        dummyThread.start();
    }

    private void registerBuiltinModules() {
        moduleManager.registerProvider(this, NetworkModule.KEY, 0, () -> new AquaticNetworkModule(this, moduleManager));
        moduleManager.registerProvider(this, EventModule.KEY, 0, () -> new AquaticEventModule(this));
        moduleManager.registerProvider(this, ServerStatusModule.KEY, 0, AquaticServerStatusModule::new);
    }
}

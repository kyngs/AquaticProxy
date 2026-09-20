package xyz.kyngs.aquaticproxy.api.network;

import org.jetbrains.annotations.Nullable;
import xyz.kyngs.aquaticproxy.api.backend.BackendServer;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;

public interface NetworkFabric {

    void bind(String bindAddress, int port) throws BindException;

    void close();

    BackendConnection openBackendConnection(BackendServer server, @Nullable ClientConnection pairedClient) throws IOException;

    interface Adapter<T> {
        void writeFrame(byte[] data) throws IOException;

        void close() throws IOException;

        boolean isConnected();

        InetSocketAddress getRemoteAddress();
    }

    interface ClientAdapter<T> extends Adapter<T> {
    }

    interface BackendAdapter<T> extends Adapter<T> {
        BackendServer getServer();
    }
}

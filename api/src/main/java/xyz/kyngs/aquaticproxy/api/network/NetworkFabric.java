package xyz.kyngs.aquaticproxy.api.network;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;

public interface NetworkFabric {

    void bind(String bindAddress, int port) throws BindException;

    interface ClientAdapter<T> {
        void writeFrame(byte[] data) throws IOException;

        void close() throws IOException;

        InetSocketAddress getRemoteAddress();
    }
}

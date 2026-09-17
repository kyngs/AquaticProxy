package xyz.kyngs.aquaticproxy.network.server.socket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.kyngs.aquaticproxy.api.network.NetworkFabric;
import xyz.kyngs.aquaticproxy.api.network.NetworkManager;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class SocketNetworkFabric implements NetworkFabric {

    private static final Logger LOGGER = LoggerFactory.getLogger(SocketNetworkFabric.class);
    private static final int MAX_PACKET_SIZE = 2_097_151;

    private final ServerSocket socket;
    private final Thread socketThread;
    private final AtomicBoolean started = new AtomicBoolean(false);
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final NetworkManager networkManager;

    public SocketNetworkFabric(NetworkManager networkManager) {
        this.networkManager = networkManager;
        try {
            socket = new ServerSocket();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        socketThread = new Thread(this::acceptConnections, "SocketServer thread");
    }

    private void acceptConnections() {
        while (running.get()) {
            try {
                var client = socket.accept();
                Thread.ofVirtual()
                        .name(client.getInetAddress().getHostAddress() + ":" + client.getPort() + " client thread")
                        .start(() -> initializeClient(client));
            } catch (IOException e) {
                LOGGER.error("Error accepting connection", e);
            }
        }
    }

    private void initializeClient(Socket client) {
        var connection = networkManager.registerClientConnection(new SocketClientAdapter(client));
        while (!client.isClosed()) {
            try {
                var packet = readPacket(client.getInputStream());
                LOGGER.info("received packet from {}: {}", client.getRemoteSocketAddress(), packet);
                connection.handleFrame(packet);
            } catch (EOFException e) {
                try {
                    client.close();
                } catch (IOException ex) {
                    LOGGER.error("Error closing client socket", ex);
                }
                break;
            } catch (IOException e) {
                LOGGER.error("Error reading from client", e);
                try {
                    client.close();
                } catch (IOException ex) {
                    LOGGER.error("Error closing client socket", ex);
                }
            }
        }
        LOGGER.info("Client {} disconnected", client.getRemoteSocketAddress());
    }

    @Override
    public void bind(String bindAddress, int port) throws BindException {
        if (!started.compareAndSet(false, true)) {
            throw new IllegalStateException("Server has already been attempted to start");
        }

        try {
            socket.bind(new InetSocketAddress(bindAddress, port));
        } catch (BindException e) {
            throw e;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        running.set(true);

        socketThread.start();
    }

    public class SocketClientAdapter implements NetworkFabric.ClientAdapter<Socket> {

        private final Socket client;

        public SocketClientAdapter(Socket client) {
            this.client = client;
        }

        @Override
        public void writeFrame(byte[] data) throws IOException {
            var out = client.getOutputStream();
            var length = data.length;

            if (length > MAX_PACKET_SIZE) {
                throw new IOException("Packet length is invalid: " + length);
            }

            // Write the length as a VarInt
            while (true) {
                if ((length & ~0x7F) == 0) {
                    out.write(length);
                    break;
                } else {
                    out.write((length & 0x7F) | 0x80);
                    length >>>= 7;
                }
            }

            out.write(data);
            out.flush();
        }

        @Override
        public void close() throws IOException {
            client.close();
        }

        @Override
        public InetSocketAddress getRemoteAddress() {
            return new InetSocketAddress(client.getInetAddress(), client.getPort());
        }
    }

    private static ByteBuf readPacket(InputStream in) throws IOException {
        var length = readPacketLength(in);

        if (length < 0 || length > MAX_PACKET_SIZE) {
            throw new IOException("Packet length is invalid: " + length);
        }

        var data = new byte[length];
        var read = 0;

        while (read < length) {
            var currentRead = in.read(data, read, length - read);

            if (currentRead == -1) {
                throw new EOFException();
            }

            read += currentRead;
        }

        return Unpooled.wrappedBuffer(data);
    }

    private static int readPacketLength(InputStream in) throws IOException {
        var value = 0;
        var position = 0;

        // Packet lengths specifically are limited to 3 VarInt bytes.
        for (int i = 0; i < 3; i++) {
            var current = in.read();

            if (current == -1) {
                throw new EOFException();
            }

            value |= (current & 0x7F) << position;

            if ((current & 0x80) == 0) {
                return value;
            }

            position += 7;
        }

        throw new IOException("Packet length VarInt is too large");
    }
}

package com.carmatechnologies.tcp.handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Queue;

public class ReadHandler implements Handler<SelectionKey> {
    private final Map<SocketChannel, Queue<ByteBuffer>> pendingData;

    public ReadHandler(final Map<SocketChannel, Queue<ByteBuffer>> pendingData) {
        this.pendingData = pendingData;
    }

    @Override
    public void handle(final SelectionKey key) throws IOException {
        final SocketChannel socket = (SocketChannel) key.channel();
        final ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
        int read = socket.read(buffer);
        if (read == -1) {
            socket.close();
            pendingData.remove(socket);
        } else {
            pendingData.get(socket).add(buffer);
            key.interestOps(SelectionKey.OP_WRITE);
        }
    }
}

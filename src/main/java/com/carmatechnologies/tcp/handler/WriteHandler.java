package com.carmatechnologies.tcp.handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Queue;

public class WriteHandler implements Handler<SelectionKey> {
    private final Map<SocketChannel, Queue<ByteBuffer>> pendingData;

    public WriteHandler(final Map<SocketChannel, Queue<ByteBuffer>> pendingData) {
        this.pendingData = pendingData;
    }

    @Override
    public void handle(final SelectionKey key) throws IOException {
        final SocketChannel socket = (SocketChannel) key.channel();
        final Queue<ByteBuffer> queue = pendingData.get(socket);

        while (!queue.isEmpty()) {
            final ByteBuffer buffer = queue.peek();
            int written = socket.write(buffer);
            if (written == -1) {
                socket.close();
                pendingData.remove(socket);
                return;
            }
            if (!buffer.hasRemaining()) {
                queue.remove();
            } else {
                return;
            }
        }

        key.interestOps(SelectionKey.OP_READ);
    }
}

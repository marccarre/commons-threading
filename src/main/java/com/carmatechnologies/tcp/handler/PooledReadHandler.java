package com.carmatechnologies.tcp.handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;

public class PooledReadHandler implements Handler<SelectionKey> {
    private final Map<SocketChannel, Queue<ByteBuffer>> pendingData;
    private ExecutorService pool;
    private Queue<SocketChannel> toWrite;

    public PooledReadHandler(final Map<SocketChannel, Queue<ByteBuffer>> pendingData, final ExecutorService pool, final Queue<SocketChannel> toWrite) {
        this.pendingData = pendingData;
        this.pool = pool;
        this.toWrite = toWrite;
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
            pool.submit(() -> {
                // Do stuff with buffer
                pendingData.get(socket).add(buffer);
                toWrite.add(socket);
                // Race condition if done in a different thread from the polling thread.
                // key.interestOps(SelectionKey.OP_WRITE);
                // so instead we enqueue the socket for it to be changed in the main thread.
                key.selector().wakeup();
            });
        }
    }
}

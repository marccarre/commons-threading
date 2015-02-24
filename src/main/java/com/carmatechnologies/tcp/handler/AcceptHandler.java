package com.carmatechnologies.tcp.handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AcceptHandler implements Handler<SelectionKey> {
    private final Map<SocketChannel, Queue<ByteBuffer>> pendingData;

    public AcceptHandler(final Map<SocketChannel, Queue<ByteBuffer>> pendingData) {
        this.pendingData = pendingData;
    }

    @Override
    public void handle(final SelectionKey key) throws IOException {
        final ServerSocketChannel serverSocket = (ServerSocketChannel) key.channel();
        final SocketChannel socket = serverSocket.accept();
        socket.configureBlocking(false);
        socket.register(key.selector(), SelectionKey.OP_READ);
        // pendingData.put(socket, new LinkedList<>());
        pendingData.put(socket, new ConcurrentLinkedQueue<>());
    }
}

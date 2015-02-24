package com.carmatechnologies.tcp.handler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NIOThreadedServerSelector {
    public static void main(final String[] args) throws IOException {
        final ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress(8080));
        serverSocket.configureBlocking(false);

        final Selector selector = Selector.open();
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);

        final int workerThreads = Integer.getInteger("WORKER_THREADS", Runtime.getRuntime().availableProcessors());
        ExecutorService pool = Executors.newFixedThreadPool(workerThreads);

        final Map<SocketChannel, Queue<ByteBuffer>> pendingData = new ConcurrentHashMap<>();
        final ConcurrentLinkedQueue<SocketChannel> toWrite = new ConcurrentLinkedQueue<>();
        final Handler<SelectionKey> acceptHandler = new AcceptHandler(pendingData);
        final Handler<SelectionKey> readHandler = new PooledReadHandler(pendingData, pool, toWrite);
        final Handler<SelectionKey> writeHandler = new WriteHandler(pendingData);

        while (true) {
            selector.select();
            SocketChannel toWriteSocket;
            while ((toWriteSocket = toWrite.poll()) != null) {
                toWriteSocket.register(selector, SelectionKey.OP_WRITE);
            }

            final Set<SelectionKey> keys = selector.selectedKeys();
            for (final Iterator<SelectionKey> iterator = keys.iterator(); iterator.hasNext(); ) {
                final SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isValid()) {
                    if (key.isAcceptable()) {
                        acceptHandler.handle(key);
                    } else if (key.isReadable()) {
                        readHandler.handle(key);
                    } else if (key.isWritable()) {
                        writeHandler.handle(key);
                    }
                }
            }
        }
    }
}

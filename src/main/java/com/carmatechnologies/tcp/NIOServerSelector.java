package com.carmatechnologies.tcp;

import com.carmatechnologies.tcp.handler.AcceptHandler;
import com.carmatechnologies.tcp.handler.Handler;
import com.carmatechnologies.tcp.handler.ReadHandler;
import com.carmatechnologies.tcp.handler.TransmogrifyChannelHandler;
import com.carmatechnologies.tcp.handler.WriteHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class NIOServerSelector {
    public static void main(final String[] args) throws IOException {
        final ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress(8080));
        serverSocket.configureBlocking(false);

        final Selector selector = Selector.open();
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);

        final Map<SocketChannel, Queue<ByteBuffer>> pendingData = new HashMap<>();
        final Handler<SelectionKey> acceptHandler = new AcceptHandler(pendingData);
        final Handler<SelectionKey> readHandler = new ReadHandler(pendingData);
        final Handler<SelectionKey> writeHandler = new WriteHandler(pendingData);

        while (true) {
            selector.select();
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

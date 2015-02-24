package com.carmatechnologies.tcp;

import com.carmatechnologies.tcp.handler.BlockingChannelHandler;
import com.carmatechnologies.tcp.handler.Handler;
import com.carmatechnologies.tcp.handler.PrintingHandler;
import com.carmatechnologies.tcp.handler.ThreadedHandler;
import com.carmatechnologies.tcp.handler.TransmogrifyChannelHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.Executors;

public class NIONonBlockingServer {
    public static void main(final String[] args) throws IOException {
        final ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress(8080));
        serverSocket.configureBlocking(false);

        final Handler<SocketChannel> handler = new TransmogrifyChannelHandler();

        final Collection<SocketChannel> sockets = new LinkedList<SocketChannel>(); // We remove items in the middle.
        while (true) {
            final SocketChannel s = serverSocket.accept();
            if (s != null) {
                s.configureBlocking(false);
                sockets.add(s);
                System.out.println("Connecting to " + s);
            }

            for (final Iterator<SocketChannel> iterator = sockets.iterator(); iterator.hasNext(); ) {
                final SocketChannel socket = iterator.next();
                if (socket.isOpen()) {
                    handler.handle(socket);
                } else {
                    iterator.remove();
                }
            }
        }
    }
}

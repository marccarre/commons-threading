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
import java.util.concurrent.Executors;

public class NIOBlockingServer {
    public static void main(final String[] args) throws IOException {
        final ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress(8080));

        final Handler<SocketChannel> handler =
                new ThreadedHandler<>(
                    new PrintingHandler<>(
                        new BlockingChannelHandler(
                            new TransmogrifyChannelHandler()
                        )
                    ),
                    Executors.newFixedThreadPool(100)
                );

        while (true) {
            final SocketChannel socket = serverSocket.accept();
            handler.handle(socket);
        }
    }
}

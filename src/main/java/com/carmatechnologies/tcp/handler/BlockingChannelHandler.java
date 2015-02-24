package com.carmatechnologies.tcp.handler;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class BlockingChannelHandler implements Handler<SocketChannel> {
    private final Handler<SocketChannel> handler;

    public BlockingChannelHandler(final Handler<SocketChannel> handler) {
        this.handler = handler;
    }

    @Override
    public void handle(final SocketChannel socketChannel) throws IOException {
        while (socketChannel.isOpen()) {
            handler.handle(socketChannel);
        }
    }
}

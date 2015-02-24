package com.carmatechnologies.tcp.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class TransmogrifyChannelHandler implements Handler<SocketChannel> {
    @Override
    public void handle(final SocketChannel socketChannel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocateDirect(80);

        int read = socketChannel.read(buffer);
        if (read == -1) {
            socketChannel.close();
        } else if (read != 0) {
            transmogrify(buffer);
            socketChannel.write(buffer);
        }
    }

    private void transmogrify(final ByteBuffer buffer) {
        buffer.flip();
        for (int i = 0; i < buffer.limit(); ++i) {
            buffer.put(i, (byte) transmogrify(buffer.get(i)));
        }
    }

    private int transmogrify(final int data) {
        return Character.isLetter(data) ? data ^ ' ' : data;
    }
}

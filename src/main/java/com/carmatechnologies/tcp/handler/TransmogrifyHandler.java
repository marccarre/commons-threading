package com.carmatechnologies.tcp.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TransmogrifyHandler implements Handler<Socket> {
    @Override
    public void handle(final Socket socket) throws IOException {
        try (
            final InputStream in = socket.getInputStream();
            final OutputStream out = socket.getOutputStream();
        ) {
            int data;
            while ((data = in.read()) != -1) {
                out.write(transmogrify(data));
            }
        }
    }

    private int transmogrify(final int data) {
        return Character.isLetter(data) ? data ^ ' ' : data;
    }
}

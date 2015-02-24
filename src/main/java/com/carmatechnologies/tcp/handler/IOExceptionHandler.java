package com.carmatechnologies.tcp.handler;

import java.io.Closeable;
import java.io.IOException;

public class IOExceptionHandler<S extends Closeable> implements Handler<S> {
    private final Handler<S> handler;

    public IOExceptionHandler(final Handler<S> handler) {
        this.handler = handler;
    }

    @Override
    public void handle(final S s) {
        try {
            handler.handle(s);
        } catch (IOException e) {
            System.err.println("Error with connection " + s + ": " + e);
            closeQuietly(s);
        }
    }

    private void closeQuietly(final S s) {
        if (s != null) {
            try {
                s.close();
            } catch (IOException ignored) {
            }
        }
    }
}

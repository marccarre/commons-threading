package com.carmatechnologies.tcp.handler;

import java.io.IOException;

public class PrintingHandler<S> implements Handler<S> {
    private final Handler<S> handler;

    public PrintingHandler(final Handler<S> handler) {
        this.handler = handler;
    }

    @Override
    public void handle(final S s) throws IOException {
        try {
            System.out.println("Connecting to " + s);
            handler.handle(s);
        } finally {
            System.out.println("Disconnecting from " + s);
        }
    }
}

package com.carmatechnologies.tcp.handler;

import java.io.Closeable;
import java.util.concurrent.ExecutorService;

public class ThreadedHandler<S extends Closeable> extends IOExceptionHandler<S> {
    private final ExecutorService pool;

    public ThreadedHandler(final Handler<S> handler, final ExecutorService pool) {
        super(handler);
        this.pool = pool;
    }

    @Override
    public void handle(final S s) {
        // POOR WAY, DO NOT DO THIS: new Thread(() -> ThreadedHandler.super.handle(s)).start();
        pool.submit(() -> ThreadedHandler.super.handle(s));
    }
}

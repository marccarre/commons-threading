package com.carmatechnologies.tcp.handler;

import java.io.IOException;

public interface Handler<S> {
    void handle(final S s) throws IOException;
}

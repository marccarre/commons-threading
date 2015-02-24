package com.carmatechnologies.tcp;

import com.carmatechnologies.tcp.handler.Handler;
import com.carmatechnologies.tcp.handler.PrintingHandler;
import com.carmatechnologies.tcp.handler.ThreadedHandler;
import com.carmatechnologies.tcp.handler.TransmogrifyHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

public class SingleThreadedServer {

    public static void main(final String[] args) throws IOException {
        final ServerSocket serverSocket = new ServerSocket(8080);
        final Handler<Socket> handler =
                new ThreadedHandler<>(
                    new PrintingHandler<>(
                        new TransmogrifyHandler()
                    ),
                    Executors.newFixedThreadPool(100)
                );

        while (true) {
            final Socket socket = serverSocket.accept();
            handler.handle(socket);
        }
    }
}

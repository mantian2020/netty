package io.netty.example.reactor.multithread;


import io.netty.example.reactor.SelfRunable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;


/**
 * @author hqg
 * @ClassName: Acceptor
 * @Description: TODO
 * @date 2021/7/6 17:34
 */
public class Acceptor implements SelfRunable {

    ServerSocketChannel serverSocket;
    Selector selector;
    String name;

    public Acceptor(String name, ServerSocketChannel serverSocket,Selector selector) {
        this.name = name;
        this.serverSocket = serverSocket;
        this.selector = selector;
    }

    @Override
    public void run() {
        try {
            SocketChannel socket = this.serverSocket.accept();
            if (socket != null) {
                new Handler("handler_" + ((InetSocketAddress)socket.getLocalAddress()).getPort(), selector,socket);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return this.name;
    }
}

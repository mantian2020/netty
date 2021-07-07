package io.netty.example.reactor.multireactor;


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

    int next = 0;
    String name;
    SubReactor[] subReactorPool;
    ServerSocketChannel serverSocket;

    public Acceptor(String name, ServerSocketChannel serverSocket,SubReactor[] subReactorPool) {
        this.name = name;
        this.serverSocket = serverSocket;
        this.subReactorPool = subReactorPool;
    }

    @Override
    public void run() {
        try {
            SocketChannel socket = this.serverSocket.accept();
            if (socket != null) {
                new Handler("handler", subReactorPool[next].getSelector(),socket);
            }
            if (++next == subReactorPool.length) {next=0;}

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return this.name;
    }
}

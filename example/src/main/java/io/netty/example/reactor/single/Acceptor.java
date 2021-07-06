package io.netty.example.reactor.single;


import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.Selector;


/**
 * @author hqg
 * @ClassName: Acceptor
 * @Description: TODO
 * @date 2021/7/6 17:34
 */
public class Acceptor implements Runnable {

    ServerSocketChannel serverSocket;
    Selector selector;

    public Acceptor(ServerSocketChannel serverSocket,Selector selector) {
        this.serverSocket = serverSocket;
        this.selector = selector;
    }

    @Override
    public void run() {
        try {
            SocketChannel socket = this.serverSocket.accept();
            if (socket != null) {
                new Handler(selector,socket);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

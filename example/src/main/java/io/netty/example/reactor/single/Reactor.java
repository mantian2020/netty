package io.netty.example.reactor.single;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author hqg
 * @ClassName: Reactor
 * @Description: TODO
 * @date 2021/7/6 17:13
 */
public class Reactor implements Runnable {

    int port;
    Selector selector;
    ServerSocketChannel serverSocket;


    public Reactor(int port) throws IOException {
        this.port = port;

        // 创建serverSocket对象
        serverSocket = ServerSocketChannel.open();
        // 绑定端口
        serverSocket.socket().bind(new InetSocketAddress(port));
        // 配置非阻塞
        serverSocket.configureBlocking(false);

        // 创建selector对象
        selector = Selector.open();
        // serversocket注册到selector上，帮忙监听accpet事件
        serverSocket.register(selector, SelectionKey.OP_ACCEPT, new Acceptor(serverSocket,selector));

        /** 还可以使用 SPI provider，来创建selector和serversocket对象
        SelectorProvider p = SelectorProvider.provider();
        selector = p.openSelector();
        serverSocket = p.openServerSocketChannel();
        */
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                System.out.println("start select event...");
                selector.select();
                Set selectedKeys = selector.selectedKeys();
                Iterator it = selectedKeys.iterator();
                while (it.hasNext()) {
                    dispatch((SelectionKey)it.next());
                }
                selectedKeys.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void dispatch(SelectionKey key) {
        Runnable r = (Runnable) key.attachment();
        if (r != null) {
            r.run();
        }
    }


    public static void main(String[] args) throws IOException, InterruptedException {

        Thread thread = new Thread(new Reactor(2021));
        thread.start();

        synchronized (Reactor.class) {
            Reactor.class.wait();
        }


    }
}

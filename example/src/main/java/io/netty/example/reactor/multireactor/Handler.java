package io.netty.example.reactor.multireactor;


import io.netty.example.reactor.SelfRunable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author hqg
 * @ClassName: Handler
 * @Description: TODO
 * @date 2021/7/6 17:38
 */
public class Handler implements SelfRunable {

    String name;
    Selector selector;
    SocketChannel socket;
    SelectionKey sk;

    ByteBuffer input = ByteBuffer.allocate(1024);
    ByteBuffer output = ByteBuffer.allocate(1024);
    static final int READING = 0, SENDING = 1,  PROCESSING = 3;
    volatile int state = READING;

    static ExecutorService poolExecutor = Executors.newFixedThreadPool(5);

    public Handler(String name, Selector selector, SocketChannel socket) throws IOException {
        this.selector = selector;
        this.socket = socket;
        this.name = name;

        this.socket.configureBlocking(false);
        sk = this.socket.register(this.selector,0);
        sk.attach(this);
        sk.interestOps(SelectionKey.OP_READ);
        selector.wakeup();
    }

    @Override
    public void run() {
        try{
            System.out.println("state:" + state);
            if (state == READING) {
                read();
            } else if (state == SENDING) {
                send();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    synchronized void read() throws IOException {
        socket.read(input);
        if (inputIsComplete()) {
            state = PROCESSING;
           poolExecutor.execute(new Processer());
        }
    }

    synchronized void processAndHandOff() {
        System.out.println("processAndHandOff=========");
        process();
        state = SENDING; // or rebind attachment
        sk.interestOps(SelectionKey.OP_WRITE);
        selector.wakeup();
        System.out.println("processAndHandOff finish ! =========");
    }

    private void send() throws IOException {
        System.out.println("start send ...");
        socket.write(output);
        socket.close();
        System.out.println("start send finish!");
        if (outputIsComplete()) sk.cancel();
    }

    boolean inputIsComplete() { return true;}

    boolean outputIsComplete() {return true;}

    void process(){
        String msg = new String(input.array());
        System.out.println("读取内容：" + msg);
        output.put(msg.getBytes());
        output.flip();
    }

    @Override
    public String getName() {
        return this.name;
    }

    class Processer implements Runnable {
        public void run() { processAndHandOff(); }
    }
}

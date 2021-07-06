package io.netty.example.reactor;

import java.io.IOException;
import java.net.Socket;

/**
 * @author hqg
 * @ClassName: BioClient
 * @Description: TODO
 * @date 2021/7/6 15:42
 */
public class BioClient implements Runnable {

    private String host;
    private int serverPort;
    public BioClient(String host, int serverPort){
        this.host = host;
        this.serverPort = serverPort;
    }

    @Override
    public void run() {
        Socket socket = null;
        try {
            socket = new Socket(host, serverPort);

            int clientPort = socket.getPort();
            String msg = String.format("current socket port is %s", clientPort);

            System.out.println("发送内容：" + msg);

            socket.getOutputStream().write(msg.getBytes());
            socket.getOutputStream().flush();

            int maxLen = 1024;
            byte[] contextBytes = new byte[maxLen];
            int realLen;
            String message = "";
            //程序执行到这里，会一直等待服务器返回信息(注意，前提是in和out都不能close，如果close了就收不到服务器的反馈了)
            while((realLen = socket.getInputStream().read(contextBytes, 0, maxLen)) != -1) {
                message += new String(contextBytes , 0 , realLen);
            }
            System.out.println("响应内容：" + message);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(new BioClient("localhost",2021));
        thread.start();
        synchronized (BioClient.class) {
            BioClient.class.wait();
        }
    }
}

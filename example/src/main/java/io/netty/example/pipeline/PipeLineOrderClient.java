package io.netty.example.pipeline;

import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.example.base.AbstractNettyClient;
import io.netty.example.base.Client;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.net.InetSocketAddress;


/**
 * @author hqg
 * @ClassName: PipeLineOrderServer
 * @Description: TODO
 * @date 2021/6/10 19:28
 */
public class PipeLineOrderClient extends AbstractNettyClient {

    @Override
    public void initChannelInitializer(SocketChannel ch) {
        ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
        ch.pipeline().addLast(new StringDecoder());
        ch.pipeline().addLast(new CommonHandler());
    }

    public static void main(String[] args) {
        Client client = new PipeLineOrderClient();
        client.start();
    }

    @ChannelHandler.Sharable
    class CommonHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            InetSocketAddress ipSocket = (InetSocketAddress)ctx.channel().remoteAddress();
            String clientIp = ipSocket.getAddress().getHostAddress();
            System.out.println("客户端ip地址:" + clientIp + ",端口:" + ipSocket.getPort());
            final ChannelFuture f = ctx.writeAndFlush(Unpooled.wrappedBuffer("abc23\nasd\n".getBytes()));
            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) {
                    System.out.println("发送消息完成！");
                }
            });
        }

        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println(msg.toString());
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }
}

package io.netty.example.pipeline;

import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.example.base.AbstractNettyServer;
import io.netty.example.base.Server;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;


/**
 * @author hqg
 * @ClassName: PipeLineOrderServer
 * @Description: TODO
 * @date 2021/6/10 19:28
 */
public class PipeLineOrderServer extends AbstractNettyServer {

    @Override
    public void initChannelInitializer(SocketChannel ch) {
        ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
        ch.pipeline().addLast(new StringDecoder());
        ch.pipeline().addLast(new OutBoundHandler1());
        ch.pipeline().addLast(new OutBoundHandler2());
        ch.pipeline().addLast(new InBoundHandler1());
        ch.pipeline().addLast(new InBoundHandler2());

    }

    public static void main(String[] args) {
        Server server = new PipeLineOrderServer();
        server.start();
    }

    @ChannelHandler.Sharable
    class InBoundHandler1 extends ChannelInboundHandlerAdapter {
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {

        }
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("InBoundHandler1-channelRead:" + msg.toString());
            ctx.fireUserEventTriggered(msg);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }

    @ChannelHandler.Sharable
    class InBoundHandler2 extends ChannelInboundHandlerAdapter {
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {

        }
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("InBoundHandler2-channelRead:" + msg.toString());
            ctx.writeAndFlush(Unpooled.wrappedBuffer("hello world!\n".getBytes()));
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            System.out.println("InBoundHandler2-userEventTriggered:" + evt.toString());
            ctx.writeAndFlush(Unpooled.wrappedBuffer("hello world!\n".getBytes()));
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }

    class OutBoundHandler1 extends ChannelOutboundHandlerAdapter{

        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            System.out.println("OutBoundHandler1=====");
            ctx.write(msg, promise);
        }
    }

    class OutBoundHandler2 extends ChannelOutboundHandlerAdapter{

        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            System.out.println("OutBoundHandler2=====");
            ctx.write(msg, promise);
        }
    }
}

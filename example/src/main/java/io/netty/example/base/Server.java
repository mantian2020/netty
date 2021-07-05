package io.netty.example.base;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;

import java.net.InetSocketAddress;

/**
 * @author hqg
 * @ClassName: Server
 * @Description: TODO
 * @date 2021/6/10 14:29
 */
public interface Server extends Service {

    ChannelHandler Common_Handler = new CommonHandler();

    @ChannelHandler.Sharable
    class CommonHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            InetSocketAddress ipSocket = (InetSocketAddress)ctx.channel().remoteAddress();
            String clientIp = ipSocket.getAddress().getHostAddress();
            System.out.println("客户端ip地址:" + clientIp + ",端口:" + ipSocket.getPort());
            final ByteBuf time = ctx.alloc().buffer(4); // (2)
//            time.writeBytes("欢迎您来到北京做客怨崖国际".getBytes());

            time.writeBytes("hello,world".getBytes());
            final ChannelFuture f = ctx.writeAndFlush(time);
            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) {
                    System.out.println("发送消息完成！");
                }
            }); // (4)
        }
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf in = (ByteBuf) msg;
            System.out.println(in.toString(io.netty.util.CharsetUtil.US_ASCII));
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }

}

package io.netty.example.base;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author hqg
 * @ClassName: AbstractNettyServer
 * @Description: TODO
 * @date 2021/6/10 14:30
 */
public abstract class AbstractNettyClient implements Client {

    private int port = 8080;

    @Override
    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        try {
            Bootstrap b = new Bootstrap(); // (2)
            b.group(bossGroup)
                    .channel(NioSocketChannel.class) // (3)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .handler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            initChannelInitializer(ch);
                        }});

            // Start the client.
            ChannelFuture f = b.connect("127.0.0.1", port).sync(); // (5)

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
        }
    }

    public abstract void initChannelInitializer(SocketChannel ch);
}

package netty.configcenter.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import netty.configcenter.event.MessageEvent;
import netty.configcenter.listener.MessageChangedListener;

/**
 * @Author：zeqi
 * @Date: Created in 23:09 29/1/18.
 * @Description:
 */
public class ConfigClient {

    public static void main(String[] args) {
        EventLoopGroup workerGroup = new NioEventLoopGroup();


        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup).channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            ObjectDecoder objectDecoder = new ObjectDecoder(1024 * 1024,
                                    ClassResolvers.weakCachingConcurrentResolver(this
                                            .getClass().getClassLoader()));

                            ch.pipeline().addLast(
                                    new ObjectEncoder(),objectDecoder,
                                    new ConfigClientHandler(new MessageChangedListener() {
                                @Override
                                public void messageChanged(MessageEvent event) {
                                    System.out.println("config changed.new config: " + event.getMessage());
                                }
                            }));
                        }
                    });

            // Start the client.
            ChannelFuture f = bootstrap.connect("localhost",8023).sync();

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            workerGroup.shutdownGracefully();
        }
    }


}

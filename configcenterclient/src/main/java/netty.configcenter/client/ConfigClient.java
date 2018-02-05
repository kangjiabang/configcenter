package netty.configcenter.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import netty.configcenter.handler.ConfigClientHandler;
import netty.configcenter.listener.MessageChangedListener;
import netty.configcenter.model.ConfigItem;

import java.util.concurrent.TimeUnit;

/**
 * @Author：zeqi
 * @Date: Created in 23:09 29/1/18.
 * @Description:
 */
public class ConfigClient implements  Runnable{


    private ConfigItem configItem;

    private ConfigClientHandler configClientHandler;


    public ConfigClient(String module,String subModule,String key) {
        configItem = new ConfigItem(module,subModule,key,null);
        configClientHandler = new ConfigClientHandler(configItem);

        Thread t = new Thread(this);
        t.start();
    }


    /**
     * 添加监听器
     * @param listener
     */
    public void addListener(MessageChangedListener listener) {
        configClientHandler.addLister(listener);
    }

    /**
     * 运行客户端
     */
    @Override
    public  void run() {
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
                                    configClientHandler);
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

    /**
     *
     * @return
     */
    public String getValue(int timeOut, TimeUnit unit) {
        if (configItem.getValue() == null) {
            try {
                Thread.sleep(unit.toMillis(timeOut));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return configItem.getValue();
    }

}

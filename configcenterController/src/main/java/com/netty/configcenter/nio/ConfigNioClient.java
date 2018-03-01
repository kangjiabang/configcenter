package com.netty.configcenter.nio;

import com.netty.configcenter.handler.ConfigControllerHandler;
import com.netty.configcenter.model.ConfigItem;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @Author：zeqi
 * @Date: Created in 17:13 8/2/18.
 * @Description:
 */
@Slf4j
public class ConfigNioClient {

    private ConfigItem configItem;

    private EventLoopGroup workerGroup;

    private Bootstrap bootstrap;

    private Channel channel;


    private String[] ipAndPort;

    private String zkHost = "localhost:2181";


    public ConfigNioClient(ConfigItem configItem,String[] ipAndPort) {

        this.configItem = configItem;
        this.ipAndPort = ipAndPort;

        this.openAndConnect();
    }

    /**
     * 重新连接服务器
     */
    public void reConnect() {
        //先关闭通道
        this.doCloseChannel();
        //再次初始化
        // initAgain();
        //再次连接
        this.doConnect();
    }

    /**
     * 运行客户端
     */
    public  void openAndConnect() {

        //打开通道
        doOpen();

        //连接通道
        doConnect();
    }


    /**
     * 打开BootStrap
     */
    private void doOpen() {

        try {
            workerGroup = new NioEventLoopGroup(1);

            bootstrap = new Bootstrap();
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
                                    new ConfigControllerHandler(configItem));
                        }
                    });

        } catch (Exception e) {
            log.error("fail to open.",e);
        }
    }

    /**
     * 开始连接操作
     */
    private void doConnect() {
        try {

            if (log.isDebugEnabled()) {
                log.debug("server info " + Arrays.toString(ipAndPort));
            }

            // Start the client.
            ChannelFuture f = bootstrap.connect(ipAndPort[0],Integer.parseInt(ipAndPort[1]));

            boolean ret = f.awaitUninterruptibly(3000, TimeUnit.MILLISECONDS);

            if (ret && f.isSuccess()) {
                channel = f.channel();
            }

        } catch (Throwable e) {
            log.error("fail to connect Server." + e);
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * 关闭client
     */
    public void doClose() {

        try {
            log.debug("the client is going to closeZk.");
            if (channel != null) {
                channel.close();
            }


            if (bootstrap != null) {
                workerGroup.shutdownGracefully();
            }

        } catch (Throwable e) {
            log.error("fail to closeZk client. ",e);
        }
    }

    /**
     * 关闭client
     */
    public void doCloseChannel() {

        try {
            log.debug("the channel is going to closeZk.");
            if (channel != null) {
                channel.close();
            }
        } catch (Throwable e) {
            log.error("fail to closeZk channel. ",e);
        }
    }


    /**
     * 设置zkHost地址
     * @param zkHost
     */
    public void setZkHost(String zkHost) {
        this.zkHost = zkHost;
    }
}

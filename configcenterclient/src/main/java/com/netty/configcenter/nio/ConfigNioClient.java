package com.netty.configcenter.nio;

import com.netty.configcenter.cache.CacheManager;
import com.netty.configcenter.config.ServerConfig;
import com.netty.configcenter.context.ListenerContext;
import com.netty.configcenter.event.ServerDisConnectEvent;
import com.netty.configcenter.handler.ConfigClientHandler;
import com.netty.configcenter.listener.MessageConfigListener;
import com.netty.configcenter.model.ConfigItem;
import com.netty.configcenter.zookeeper.ZookeeperServiceClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * @Author：zeqi
 * @Date: Created in 17:13 8/2/18.
 * @Description:
 */
@Slf4j
public class ConfigNioClient {

    private CacheManager cacheManager;

    private ConfigItem configItem;

    private EventLoopGroup workerGroup;

    private Bootstrap bootstrap;

    private Channel channel;

    private ServerConfig serverConfig;

    private ListenerContext listenerContext;

    private String zkHost = "localhost:2181";


    public ConfigNioClient(ConfigItem configItem,CacheManager cacheManager) {

        this.configItem = configItem;

        //缓存管理器实例化
        this.cacheManager = cacheManager;

        listenerContext = new ListenerContext();

        listenerContext.addListener(new MessageConfigListener<ServerDisConnectEvent>() {
            @Override
            public void messageChanged(ServerDisConnectEvent event) {
                if (log.isDebugEnabled()) {
                    log.debug("reconnect server");
                }
                reConnect();
            }
        });

        serverConfig = new ServerConfig(zkHost);

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
                                    new ConfigClientHandler(configItem,cacheManager,listenerContext));
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
            //sever形式为localhost:8082
            String server = serverConfig.getValidServer();

            if (StringUtils.isEmpty(server)) {

                log.error("no config server found");
                this.doClose();
                return;
            }
            if (log.isDebugEnabled()) {
                log.debug("server info " + server);
            }

            String[] hostInfo = server.split(":");

            // Start the client.
            ChannelFuture f = bootstrap.connect(hostInfo[0],Integer.parseInt(hostInfo[1]));

            boolean ret = f.awaitUninterruptibly(3000, TimeUnit.MILLISECONDS);

            if (ret && f.isSuccess()) {
                channel = f.channel();
            }

            // Wait until the connection is closed.
            //f.channel().closeFuture().sync();
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

            //发出事件关闭事件
            listenerContext.fireClientClose();

            //关闭zk
            serverConfig.close();
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
     * 添加监听器
     * @param listener
     */
    public void addListener(MessageConfigListener listener) {
        listenerContext.addListener(listener);
    }



    /**
     * 设置zkHost地址
     * @param zkHost
     */
    public void setZkHost(String zkHost) {
        this.zkHost = zkHost;
    }
}

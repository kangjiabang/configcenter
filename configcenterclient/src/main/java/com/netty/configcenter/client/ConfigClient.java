package com.netty.configcenter.client;

import com.netty.configcenter.CacheManager;
import com.netty.configcenter.config.ServerConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.extern.slf4j.Slf4j;
import com.netty.configcenter.handler.ConfigClientHandler;
import com.netty.configcenter.listener.MessageChangedListener;
import com.netty.configcenter.model.ConfigItem;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * @Author：zeqi
 * @Date: Created in 23:09 29/1/18.
 * @Description:
 */
@Slf4j
public class ConfigClient {


    private CacheManager cacheManager;

    private ConfigItem configItem;

    private ConfigClientHandler configClientHandler;

    private  EventLoopGroup workerGroup;

    private Bootstrap bootstrap;

    private Channel channel;

    private ServerConfig serverConfig;

    public ConfigClient(String module,String subModule,String key) {
        configItem = new ConfigItem(module,subModule,key,null);

        //缓存管理器实例化
        cacheManager = new CacheManager();

        configClientHandler = new ConfigClientHandler(configItem,cacheManager);

        serverConfig = new ServerConfig("localhost:2181");
        this.runClient();
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
    public  void runClient() {
        workerGroup = new NioEventLoopGroup();

        try {
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
                                    configClientHandler);
                        }
                    });

            //sever形式为localhost:8082
            String server = serverConfig.getValidServer();

            if (StringUtils.isEmpty(server)) {

                log.error("no config server found");
                return;
            }
            if (log.isDebugEnabled()) {
                log.debug("server info " + server);
            }

            String[] hostInfo = server.split(":");

            // Start the client.
            ChannelFuture f = bootstrap.connect(hostInfo[0],Integer.parseInt(hostInfo[1]));

            boolean ret = f.awaitUninterruptibly(3000,TimeUnit.MILLISECONDS);

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
            if (channel != null) {
                channel.close();
            }
            if (bootstrap != null) {
                workerGroup.shutdownGracefully();
            }
        } catch (Throwable e) {
            log.error("fail to close client. ",e);
        }
    }

    /**
     * 获取配置项值
     * @return
     */
    public String getValue(int timeOut, TimeUnit unit) {

        String cachedValue = cacheManager.getCache(configItem);

        if (cachedValue != null) {
            log.debug("get value from cache." + cachedValue);
            return cachedValue;
        }

        //等待结果，可以用httpclient替换
        while (configItem.getValue() == null) {
            try {
                Thread.sleep(unit.toMillis(timeOut));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (configItem != null) {
            cacheManager.setCache(configItem,configItem.getValue());
        }

        return configItem.getValue();
    }

}

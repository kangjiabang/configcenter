/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.netty.configcenter.server;

import com.netty.configcenter.constant.Constants;
import com.netty.configcenter.network.IpUtils;
import com.netty.configcenter.zookeeper.ZookeeperService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Service;

/**
 * Server that accept the path of a file an echo back its content.
 */
@Slf4j
@Service
public class ConfigServer implements InitializingBean, ApplicationListener<ContextClosedEvent> {

    static final boolean SSL = System.getProperty("ssl") != null;
    // Use the same default port with the telnet example so that we can use the telnet client example to access it.
    static final int PORT = Integer.parseInt(System.getProperty("port", SSL ? "8992" : "8022"));

    @Autowired
    private ZookeeperService zookeeperService;

    @Autowired
    private ChannelManager channelManager;

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    private  Channel channel;

    private ServerBootstrap b;

    @Value("${zk.server}")
    private String zookeeperHost;

    public void runServer() throws Exception {
        // Configure SSL.
        final SslContext sslCtx;
        if (SSL) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } else {
            sslCtx = null;
        }

        // Configure the server.
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();


        try {
            b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {


                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            if (sslCtx != null) {
                                p.addLast(sslCtx.newHandler(ch.alloc()));
                            }

                            ObjectDecoder objectDecoder = new ObjectDecoder(1024 * 1024,
                                    ClassResolvers.weakCachingConcurrentResolver(this
                                            .getClass().getClassLoader()));

                            p.addLast(
                                    new ObjectEncoder(), objectDecoder,
                                    //new ChunkedWriteHandler(),
                                    new ConfigServerHandler(channelManager));
                        }
                    });

            // Start the server.
            ChannelFuture f = b.bind(PORT);
            f.syncUninterruptibly();
            channel = f.channel();


            //注册服务
            zookeeperService.createNodeRecursively(Constants.PATH_SERVER_LIST,"");
            zookeeperService.createNodeSeqEphemeral(Constants.PATH_SERVER_NODE_PATH, IpUtils.getLocalHostAddress() + ":" + PORT);
            /*// Wait until the server socket is closed.
            f.channel().closeFuture().sync();*/
        } finally {
            /*// Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();*/
        }
    }

    /**
     * 关闭server
     */
    public void doClose() {

        try {
            log.debug("the server is start to close.");

            if (channel != null) {
                channel.close();
            }
            if (b != null) {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }

            zookeeperService.closeZk();
        } catch (Throwable e) {
            log.error("fail to close Server. ",e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            this.runServer();
        } catch (Exception e) {
            log.error("afterPropertiesSet error. ",e);
        }
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
          this.doClose();
    }
}

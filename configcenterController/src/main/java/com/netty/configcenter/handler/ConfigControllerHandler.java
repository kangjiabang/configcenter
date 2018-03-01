package com.netty.configcenter.handler;

import com.netty.configcenter.channel.ConfigItemChannel;
import com.netty.configcenter.common.OpCode;
import com.netty.configcenter.context.ListenerContext;
import com.netty.configcenter.model.ConfigItem;
import com.netty.configcenter.model.Packet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author：zeqi
 * @Date: Created in 23:18 29/1/18.
 * @Description:
 */
@Slf4j
public class ConfigControllerHandler extends ChannelInboundHandlerAdapter {

    private ConfigItem configItem = null;


    public ConfigControllerHandler(ConfigItem configItem) {

        //设置ConfigItem
        this.configItem = configItem;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {

        if (log.isDebugEnabled()) {

            log.debug("channel is registered");
        }

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("get message from server." + msg);
        }

        Packet packet = Packet.builder().configItem(configItem).header(OpCode.CONFIG_CHANGED).build();

        ctx.writeAndFlush(packet);


    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


    /**
     * 获取配置项的值
     *
     * @return
     */
    public ConfigItem getConfigItem() {
        return configItem;
    }
}

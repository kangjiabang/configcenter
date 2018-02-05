/*
 * Copyright 2014 The Netty Project
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

import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;
import com.netty.configcenter.common.OpCode;
import com.netty.configcenter.model.ConfigItem;
import com.netty.configcenter.model.Packet;

@Slf4j
public class ConfigServerHandler extends SimpleChannelInboundHandler<Object> {


    /**
     * channel管理器
     */
    private  ChannelManager channelManager;

    protected ConfigServerHandler(ChannelManager channelManager) {
        this.channelManager = channelManager;
        System.out.println("ConfigServerHandler()");

    }



    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {

        if (log.isDebugEnabled()) {
            log.debug("client is registered:");
        }
        /*channelManager.addChannel(ctx.channel());
        channelManager.processChannel();*/
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (log.isDebugEnabled()) {
            log.debug("client is Active:");
        }
        ctx.writeAndFlush("HELLO: Client is connected.\n");

    }



    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

        Thread.sleep(1000);

        Packet packet = (Packet) msg;

        switch (packet.getHeader()) {
            case OpCode.HEARTBEAT: {
                if (log.isDebugEnabled()) {
                    log.debug(((Packet) msg).getMessage());
                }

                Packet pingPacket = Packet.builder().message("get ping from server.").
                        header(OpCode.HEARTBEAT).build();

                if (log.isDebugEnabled()) {
                    log.debug("send packet to client.");
                }
                //System.out.println("send packet to client.");
                ctx.writeAndFlush(pingPacket);
                break;
            }
            case OpCode.FIRST_REGISTER: {

                //首次注册，将信息返回
                ConfigItem configItem = new ConfigItem();
                configItem.setKey("whiteList");
                configItem.setModule("loan");
                configItem.setSubModule("magina");
                configItem.setValue("127.0.0.1");

                Packet sendPacket = Packet.builder().configItem(configItem).header(OpCode.FIRST_REGISTER).build();

                ctx.writeAndFlush(sendPacket);

                channelManager.addChannel(packet.getConfigItem(),ctx.channel());
            }
        }
        //System.out.println("message from client:" + msg.toString());

        //ctx.writeAndFlush("OK: ");

    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();

        if (ctx.channel().isActive()) {
            ctx.writeAndFlush("ERR: " +
                    cause.getClass().getSimpleName() + ": " +
                    cause.getMessage() + '\n').addListener(ChannelFutureListener.CLOSE);
        }
    }
}


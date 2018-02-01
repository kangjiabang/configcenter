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

package netty.configcenter.server;

import io.netty.channel.*;
import netty.configcenter.enums.HeaderEnum;
import netty.configcenter.model.Packet;

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

        /*channelManager.addChannel(ctx.channel());
        channelManager.processChannel();*/
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush("HELLO: Client is connected.\n");

    }



    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

        Thread.sleep(1000);

        Packet packet = (Packet) msg;

        if (packet.getHeader() == HeaderEnum.HEARTBEAT.getCode()) {
            System.out.println("recevie client heartbeat");
        } if (packet.getHeader() == HeaderEnum.FIRST_REGISTER.getCode()) {
            System.out.println("recevie client config");
            channelManager.addChannel(packet.getConfigItem(),ctx.channel());
        }
        System.out.println("message from client:" + msg.toString());

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


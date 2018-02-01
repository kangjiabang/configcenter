package netty.configcenter.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import netty.configcenter.channel.ConfigItemChannel;
import netty.configcenter.handler.HeartBeatHandler;
import netty.configcenter.task.HeartBeatTask;
import netty.configcenter.common.OpCode;
import netty.configcenter.listener.MessageChangedListener;
import netty.configcenter.model.ConfigItem;
import netty.configcenter.model.Packet;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author：zeqi
 * @Date: Created in 23:18 29/1/18.
 * @Description:
 */
public class ConfigClientHandler extends ChannelInboundHandlerAdapter {


    /**
     * 监听器上下文
     */
    private ListenerContext listenerContext = null;

    private HeartBeatTask task;

    private ConfigItemChannel configChannel;

    private  AtomicBoolean isRegiesterConfig = new AtomicBoolean(false);

    HeartBeatHandler heartBeatHandler = new HeartBeatHandler();

    public void addLister(MessageChangedListener listener) {
        listenerContext.addListener(listener);
    }

    public ConfigClientHandler(MessageChangedListener listener) {

        listenerContext = new ListenerContext();

        listenerContext.addListener(listener);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        configChannel = new ConfigItemChannel(ctx.channel());
        new Thread( new HeartBeatTask(configChannel)).start();

        System.out.println("channel is registered");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //处理心跳
        heartBeatHandler.handle(configChannel,msg);
       // System.out.println("message from server:" + msg);

        //如果首次连接配置中心，发送注册信息
        if (isRegiesterConfig.compareAndSet(false,true)) {
            ConfigItem configItem = new ConfigItem();
            configItem.setKey("whiteList");
            configItem.setModule("loan");
            configItem.setSubModule("magina");
            Packet packet = Packet.builder().configItem(configItem).header(OpCode.FIRST_REGISTER).build();

            ctx.writeAndFlush(packet);
        }
        //如果是packet
        if (msg instanceof Packet) {

            Packet packet = (Packet) msg;
            int header = packet.getHeader();
            switch(header) {
                case OpCode.CONFIG_CHANGED:
                    listenerContext.fireMessageChaned(packet.getConfigItem());
                    break;

                case OpCode.HEARTBEAT:
                        System.out.println(packet.getMessage());
                    break;
                case OpCode.FIRST_REGISTER:
                    System.out.println(packet.getConfigItem());
                    break;
            }
        }

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

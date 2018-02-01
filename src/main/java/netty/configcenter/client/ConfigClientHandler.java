package netty.configcenter.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import netty.configcenter.enums.HeaderEnum;
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

    private  AtomicBoolean isRegiesterConfig = new AtomicBoolean(false);

    public void addLister(MessageChangedListener listener) {
        listenerContext.addListener(listener);
    }

    public ConfigClientHandler(MessageChangedListener listener) {
        listenerContext = new ListenerContext();
        listenerContext.addListener(listener);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        System.out.println("message from server:" + msg);

        //如果首次连接配置中心，发送注册信息
        if (isRegiesterConfig.compareAndSet(false,true)) {
            ConfigItem configItem = new ConfigItem();
            configItem.setKey("whiteList");
            configItem.setModule("loan");
            configItem.setSubModule("magina");
            Packet packet = Packet.builder().configItem(configItem).header(HeaderEnum.FIRST_REGISTER.getCode()).build();

            ctx.writeAndFlush(packet);
        }
        //如果是packet
        if (msg instanceof Packet) {

            Packet packet = (Packet) msg;
            int header = packet.getHeader();
            if (header == HeaderEnum.CONFIG_CHANGED.getCode()) {
                listenerContext.fireMessageChaned(packet.getConfigItem());
            }
        }

}

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

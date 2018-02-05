package netty.configcenter.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import netty.configcenter.CacheManager;
import netty.configcenter.channel.ConfigItemChannel;
import netty.configcenter.context.ListenerContext;
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
@Slf4j
public class ConfigClientHandler extends ChannelInboundHandlerAdapter {

    /**
     * 监听器上下文
     */
    private ListenerContext listenerContext = null;

    private HeartBeatTask task;

    private ConfigItemChannel configChannel;

    private CacheManager cacheManager;

    private AtomicBoolean isRegiesterConfig = new AtomicBoolean(false);

    HeartBeatHandler heartBeatHandler = new HeartBeatHandler();

    private ConfigItem configItem = null;

    public void addLister(MessageChangedListener listener) {
        listenerContext.addListener(listener);
    }

    public ConfigClientHandler(ConfigItem configItem, CacheManager cacheManager) {

        listenerContext = new ListenerContext();
        this.cacheManager = cacheManager;
        //设置ConfigItem
        this.configItem = configItem;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        configChannel = new ConfigItemChannel(ctx.channel());
        new Thread(new HeartBeatTask(configChannel)).start();
        if (log.isDebugEnabled()) {

            log.debug("channel is registered");
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //处理心跳
        heartBeatHandler.handle(configChannel, msg);
        // System.out.println("message from server:" + msg);

        //如果首次连接配置中心，发送注册信息
        if (configItem != null && isRegiesterConfig.compareAndSet(false, true)) {

            Packet packet = Packet.builder().configItem(configItem).header(OpCode.FIRST_REGISTER).build();

            ctx.writeAndFlush(packet);
        }
        //如果是packet
        if (msg instanceof Packet) {

            Packet packet = (Packet) msg;
            int header = packet.getHeader();
            switch (header) {
                case OpCode.CONFIG_CHANGED:
                    if (log.isDebugEnabled()) {

                        log.debug("get changed value from server:" + packet);
                    }
                    synchronized (cacheManager) {
                        String cachedValue = cacheManager.getCache(packet.getConfigItem());

                        //如果缓存值为空或者缓存的值发生改变，则设置缓存
                        if (isNeedRefreshCache(packet.getConfigItem().getValue(), cachedValue)) {
                            //通知lisenter事件
                            listenerContext.fireMessageChaned(packet.getConfigItem());
                            log.debug("start to set cache." + packet.getConfigItem().getValue());
                            cacheManager.setCache(packet.getConfigItem(), packet.getConfigItem().getValue());
                        }

                    }

                    break;

                case OpCode.HEARTBEAT:
                    if (log.isDebugEnabled()) {

                        log.debug(packet.getMessage());
                    }
                    break;
                case OpCode.FIRST_REGISTER:
                    if (log.isDebugEnabled()) {

                        log.debug("get value from server:" + packet);
                    }

                    configItem.setValue(packet.getConfigItem().getValue());
                    break;
            }
        }

    }

    /**
     * 如果缓存值为空或者缓存的值发生改变，则设置缓存
     * @param newValue
     * @param cachedValue
     * @return
     */
    private boolean isNeedRefreshCache(String  newValue , String cachedValue) {
        return cachedValue == null ||
                (cachedValue != null && !cachedValue.equals(newValue));
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

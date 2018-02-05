package netty.configcenter.server;

import io.netty.channel.Channel;

import lombok.extern.slf4j.Slf4j;
import netty.configcenter.channel.ConfigItemChannel;
import netty.configcenter.common.OpCode;
import netty.configcenter.model.ConfigItem;
import netty.configcenter.model.Packet;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author：zeqi
 * @Date: Created in 20:27 31/1/18.
 * @Description:
 */
@Slf4j
@Service
public class ChannelManager {


    /**
     * channel列表，同一个configItem可能会有多个channel获取
     */
    private ConcurrentHashMap<ConfigItem,Set<ConfigItemChannel>> configVoChannelConcurrentHashMap = null;

    public ChannelManager() {
        configVoChannelConcurrentHashMap = new ConcurrentHashMap<>();

        //processChannel();
    }

    /**
     * 添加channel
     * @param configItem
     * @param channel
     */
    public  synchronized  void addChannel(ConfigItem configItem, Channel channel) {


        if (configVoChannelConcurrentHashMap.get(configItem) == null) {
            Set<ConfigItemChannel> configItemChannelList = new HashSet<ConfigItemChannel>();
            configItemChannelList.add(new ConfigItemChannel(channel));

            configVoChannelConcurrentHashMap.putIfAbsent(configItem,configItemChannelList);
        } else {

            Set<ConfigItemChannel> configItemChannels = configVoChannelConcurrentHashMap.get(configItem);
            configItemChannels.add(new ConfigItemChannel(channel));
        }


    }


    /**
     * 配置项发生改变时调用的接口
     * @param configItem
     */
    public void messageChanged(ConfigItem configItem) {

        if (log.isDebugEnabled()) {
            log.debug("start to modify configItem." + configItem);
        }
        //查找channel列表
        Set<ConfigItemChannel> configItemChannels = configVoChannelConcurrentHashMap.get(configItem);
        //修改配置项值
        if (configItemChannels != null && !configItemChannels.isEmpty()) {

            for (ConfigItemChannel configItemChannel: configItemChannels) {
                configItemChannel.getChannel().writeAndFlush(
                        Packet.builder().configItem(configItem).header(OpCode.CONFIG_CHANGED).build());
            }
        }


    }

    public void processChannel() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Map.Entry<ConfigItem,ConfigItemChannel>[] entrys = configVoChannelConcurrentHashMap.entrySet().toArray(new Map.Entry[0]);


                    if (entrys.length > 0) {
                        Channel channel = entrys[new Random().nextInt(entrys.length)].getValue().getChannel();



                        System.out.println("selected:" + channel.toString());

                        ConfigItem configItem = new ConfigItem();
                        configItem.setKey("whiteList");
                        configItem.setModule("loan");
                        configItem.setSubModule("magina");
                        configItem.setValue("127.0.0.1");

                        Packet packet = Packet.builder().configItem(configItem).header(OpCode.CONFIG_CHANGED).build();

                        System.out.println("send packet to client.");
                        channel.writeAndFlush(packet);
                        break;
                    }

                }
            }
        });
        t.start();
    }
}

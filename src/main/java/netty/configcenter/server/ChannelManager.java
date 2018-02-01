package netty.configcenter.server;

import io.netty.channel.Channel;

import netty.configcenter.channel.ConfigItemChannel;
import netty.configcenter.common.OpCode;
import netty.configcenter.model.ConfigItem;
import netty.configcenter.model.Packet;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author：zeqi
 * @Date: Created in 20:27 31/1/18.
 * @Description:
 */
public class ChannelManager {


    /**
     * channel列表
     */
    private ConcurrentHashMap<ConfigItem,ConfigItemChannel> configVoChannelConcurrentHashMap = null;

    public ChannelManager() {
        configVoChannelConcurrentHashMap = new ConcurrentHashMap<>();

        processChannel();
    }

    /**
     * 添加配置项
     * @param configItem
     * @param channel
     */
    public  void addChannel(ConfigItem configItem, Channel channel) {

        configVoChannelConcurrentHashMap.putIfAbsent(configItem,new ConfigItemChannel(channel));

    }


    public void messageChanged(ConfigItem configItem) {

        //查找channel


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
                       // channel.writeAndFlush(packet);
                        break;
                    }

                }
            }
        });
        t.start();
    }
}

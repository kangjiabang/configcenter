package com.netty.configcenter.context;

import com.alibaba.fastjson.JSON;
import com.netty.configcenter.event.MessageEvent;
import com.netty.configcenter.event.ServerDisConnectEvent;
import com.netty.configcenter.listener.MessageChangedListener;
import com.netty.configcenter.listener.ServerDisconnectListener;
import com.netty.configcenter.model.ConfigItem;

/**
 * @Author：zeqi
 * @Date: Created in 20:58 31/1/18.
 * @Description:
 */
public class ListenerContext {

    private MessageChangedListener listener;

    private ServerDisconnectListener disConnectListener;

    public ListenerContext() {
    }

    public void addListener(MessageChangedListener listener) {
        this.listener = listener;
    }

    public void addServerDisconnectListener(ServerDisconnectListener listener) {
        this.disConnectListener = listener;
    }

    public void fireMessageChaned(ConfigItem item) {

        MessageEvent event = new MessageEvent(JSON.toJSONString(item),"1","0");

        if (listener != null) {
            listener.messageChanged(event);
        }
    }

    /**
     * 服务器断连
     */
    public void fireServerDisconnect() {

        ServerDisConnectEvent event = new ServerDisConnectEvent();

        if (listener != null) {
            disConnectListener.messageChanged(event);
        }
    }
}

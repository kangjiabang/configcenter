package com.netty.configcenter.context;

import com.alibaba.fastjson.JSON;
import com.netty.configcenter.event.ConfigEvent;
import com.netty.configcenter.event.MessageChangedEvent;
import com.netty.configcenter.event.ServerDisConnectEvent;
import com.netty.configcenter.listener.MessageConfigListener;
import com.netty.configcenter.listener.ServerDisconnectListener;
import com.netty.configcenter.model.ConfigItem;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author：zeqi
 * @Date: Created in 20:58 31/1/18.
 * @Description:
 */
public class ListenerContext {

    private List<MessageConfigListener> listenerList;

    private ServerDisconnectListener disConnectListener;

    public ListenerContext() {
        listenerList = new ArrayList<>();
    }

    public void addListener(MessageConfigListener listener) {
        listenerList.add(listener);
    }

    /**
     * 找到适合相应事件的listeners并且通知执行
     * @param item
     */
    public void fireMessageChaned(ConfigItem item) {

        MessageChangedEvent event = new MessageChangedEvent(JSON.toJSONString(item),"1","0");

        List<MessageConfigListener> listenersToNotify = findListeners(event);

        for (MessageConfigListener messageConfigListener: listenersToNotify) {
            messageConfigListener.messageChanged(event);
        }

    }

    /**
     * 根据事件类型找到适配的Listeners
     * @param event
     * @return
     */
    private List<MessageConfigListener> findListeners(ConfigEvent event) {

        List<MessageConfigListener> choosedListeners = new ArrayList<>();

        for (MessageConfigListener messageConfigListener: listenerList) {

            Class<?> clazz = messageConfigListener.getClass();

            //types[0] 形如 com.netty.configcenter.listener.MessageConfigListener<com.netty.configcenter.event.MessageChangedEvent>
            Type[] types = clazz.getGenericInterfaces();

            if (types[0] instanceof  ParameterizedType) {

                Type[] subTypes = ((ParameterizedType) types[0]).getActualTypeArguments();

                //形如 class com.netty.configcenter.event.MessageChangedEvent
                Class clazzEvent = (Class)subTypes[0];

                //如果传入的event和listener泛型中定义的Event的类型相同，
                // 比如传入MessageChangedEvent，Listener为MessageConfigListener<MessageChangedEvent>，则为需要通知的监听器
                if (clazzEvent.isAssignableFrom(event.getClass())) {
                    choosedListeners.add(messageConfigListener);
                }

            }
        }
        return choosedListeners;
    }

    /**
     * 服务器断连
     */
    public void fireServerDisconnect() {

        ServerDisConnectEvent event = new ServerDisConnectEvent(this);

        List<MessageConfigListener> listenersToNotify = findListeners(event);

        for (MessageConfigListener messageConfigListener: listenersToNotify) {
            messageConfigListener.messageChanged(event);
        }

    }

    /**
     * 客户端关闭
     */
    public void fireClientClose() {
        //TODO
    }
}

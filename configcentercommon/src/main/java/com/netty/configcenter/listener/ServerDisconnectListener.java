package com.netty.configcenter.listener;

import com.netty.configcenter.event.MessageEvent;
import com.netty.configcenter.event.ServerDisConnectEvent;

/**
 * @Author：zeqi
 * @Date: Created in 20:48 31/1/18.
 * @Description:
 */
public interface ServerDisconnectListener {


    void  messageChanged(ServerDisConnectEvent event);
}

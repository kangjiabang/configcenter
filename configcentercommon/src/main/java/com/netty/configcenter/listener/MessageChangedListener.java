package com.netty.configcenter.listener;

import com.netty.configcenter.event.MessageEvent;

/**
 * @Author：zeqi
 * @Date: Created in 20:48 31/1/18.
 * @Description:
 */
public interface MessageChangedListener {


    void  messageChanged(MessageEvent event);
}

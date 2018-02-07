package com.netty.configcenter.listener;

import com.netty.configcenter.event.ConfigEvent;

/**
 * @Author：zeqi
 * @Date: Created in 20:48 31/1/18.
 * @Description:
 */
public interface MessageConfigListener<T extends ConfigEvent> {


    void  messageChanged(T event);
}

package com.netty.configcenter.event;

/**
 * @Author：zeqi
 * @Date: Created in 20:51 31/1/18.
 * @Description:
 */
public abstract class ConfigEvent {

    private Object source;

    private final long timestamp;

    public ConfigEvent(Object source) {
      this.source = source;
      timestamp = System.currentTimeMillis();
    }


    public long getTimestamp() {
        return timestamp;
    }
}

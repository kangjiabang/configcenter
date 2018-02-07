package com.netty.configcenter.cache;

import com.netty.configcenter.model.ConfigItem;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author：zeqi
 * @Date: Created in 21:56 5/2/18.
 * @Description:
 */
public class CacheManager {

    /**
     * 缓存配置信息
     */
    private ConcurrentHashMap<ConfigItem,String> cache = new ConcurrentHashMap<>();


    public  String getCache(ConfigItem item) {
        return cache.get(item);
    }

    public void setCache(ConfigItem item, String value) {
        cache.put(item, value);
    }
}

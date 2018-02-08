package com.netty.configcenter.client;

import com.netty.configcenter.cache.CacheManager;
import com.netty.configcenter.context.ListenerContext;
import com.netty.configcenter.nio.ConfigNioClient;
import com.netty.configcenter.utils.PathUtils;
import com.netty.configcenter.zookeeper.ZookeeperServiceClient;
import lombok.extern.slf4j.Slf4j;
import com.netty.configcenter.listener.MessageConfigListener;
import com.netty.configcenter.model.ConfigItem;
import org.springframework.util.StringUtils;

/**
 * @Author：zeqi
 * @Date: Created in 23:09 29/1/18.
 * @Description:
 */
@Slf4j
public class ConfigClient {


    private CacheManager cacheManager;

    private ConfigItem configItem;

    private String zkHost = "localhost:2181";

    private ZookeeperServiceClient zookeeperServiceClient;

    private ConfigNioClient configNioClient;

    public ConfigClient(String module,String subModule,String key) {

        configItem = new ConfigItem(module,subModule,key,null);

        zookeeperServiceClient = new ZookeeperServiceClient(zkHost);

        //缓存管理器实例化
        cacheManager = new CacheManager();

        zookeeperServiceClient = new ZookeeperServiceClient(zkHost);

        configNioClient = new ConfigNioClient(configItem,cacheManager);

    }

    /**
     * 添加监听器
     * @param listener
     */
    public void addListener(MessageConfigListener listener) {
        configNioClient.addListener(listener);
    }


    /**
     * 获取配置项值
     * @return
     */
    public String getValue() {

        String cachedValue = cacheManager.getCache(configItem);

        if (cachedValue != null) {
            log.debug("get value from cache." + cachedValue);
            return cachedValue;
        }

        //首次获取配置项值时，从zk处拉取
        String path = PathUtils.buildConfigData(configItem.getModule(),configItem.getSubModule(),configItem.getKey());

        String value = zookeeperServiceClient.getData(path);

        //zk处值不为空时，刷新缓存
        if (configItem != null && !StringUtils.isEmpty(value)) {
            configItem.setValue(value);
            cacheManager.setCache(configItem,value);
        }

        return value;
    }


    /**
     * 设置zkHost地址
     * @param zkHost
     */
    public void setZkHost(String zkHost) {
        this.configNioClient.setZkHost(zkHost);
    }
}

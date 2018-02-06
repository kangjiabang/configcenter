package com.netty.configcenter.config;

import com.netty.configcenter.zookeeper.ZookeeperService;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;

/**
 * @Author：zeqi
 * @Date: Created in 17:41 6/2/18.
 * @Description:
 */
public class ServerConfig {


    /**
     * zookeeper 相关操作
     */
    private ZookeeperService zookeeperService;


    public ServerConfig(String zkHost) {
        this.zookeeperService = new ZookeeperService(zkHost);
    }


    /**
     * 返回任一一个ConfigServer 的host:port
     * @return
     */
    public String getValidServer() {
        List<String> serverList = this.zookeeperService.getChildrenData(zookeeperService.PATH_SERVER_LIST);

        if (!CollectionUtils.isEmpty(serverList)) {
            return serverList.get(0);
        }
        return null;
    }


}

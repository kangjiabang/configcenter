package com.netty.configcenter.config;

import com.netty.configcenter.constant.Constants;
import com.netty.configcenter.model.ConfigItem;
import com.netty.configcenter.utils.PathUtils;
import com.netty.configcenter.zookeeper.ZookeeperServiceClient;
import org.springframework.util.CollectionUtils;

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
    private ZookeeperServiceClient zookeeperService;


    public ServerConfig(String zkHost) {
        this.zookeeperService = new ZookeeperServiceClient(zkHost);
    }


    /**
     * 返回任一一个ConfigServer 的host:port
     * @return
     */
    public String getValidServer() {
        List<String> serverList = this.zookeeperService.getChildrenData(Constants.PATH_SERVER_LIST);

        if (!CollectionUtils.isEmpty(serverList)) {
            return serverList.get(0);
        }
        return null;
    }


    /**
     * 注册到zookeeper上节点连接信息
     * @param server
     */
    public void registerZkNodeInfo(ConfigItem configItem, String server) {
        String nodeServerPath = PathUtils.buildNodeServerMappingPath(configItem.getModule(),
                configItem.getSubModule(),configItem.getKey());
        //递归创建节点
        zookeeperService.createNodeRecursively(nodeServerPath,server);

    }

    /**
     * 关闭zk
     */
    public void close() {
        zookeeperService.closeZk();
    }


}

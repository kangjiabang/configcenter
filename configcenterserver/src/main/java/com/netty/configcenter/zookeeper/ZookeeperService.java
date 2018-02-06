package com.netty.configcenter.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author：zeqi
 * @Date: Created in 14:12 6/2/18.
 * @Description:
 */
@Slf4j
@Service
public class ZookeeperService  implements InitializingBean ,Watcher {

    private static ZooKeeper zookeeper;

    @Value("${zk.server}")
    private String connectString;

    public static final String PATH_PREFIX = "/config/center/data";

    public static final String PATH_SERVER_LIST = "/config/center/data/serverlist";

    public static final String PATH_SERVER_NODE_PATH = "/config/center/data/serverlist/server_seq_";

    /**
     * 创建持久化序列节点
     * @param path
     * @param value
     */
    public void createSeqNode(String path,String value) {
        try {
            zookeeper.create(path,value.getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);

        } catch (KeeperException e) {
            log.error("createSeqNode error.",e);

        } catch (InterruptedException e) {
            log.error("createSeqNode error.",e);
        }

    }

    /**
     * 创建持久化节点
     * @param path
     * @param value
     */
    public void createNode(String path,String value) {
        try {
            zookeeper.create(path,value.getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        } catch (KeeperException e) {
            log.error("createNode error.",e);

        } catch (InterruptedException e) {
            log.error("createNode error.",e);
        }

    }

    /**
     * 获取子节点
     * @param path
     * @return
     */
    public List<String> getChildren(String path) {
        try {

            List<String> childList = zookeeper.getChildren(path,false);

            return childList;
        } catch (Exception e) {
            log.error("getChildren error.",e);
        }
        return null;
    }

    /**
     * 获取子节点数据列表
     * @param path
     * @return
     */
    public List<String> getChildrenData(String path) {
        try {
            List<String> children = this.getChildren(path);

            if (CollectionUtils.isEmpty(children)) {
                return null;
            }
            List<String> dataList = new ArrayList<>();;
            for (String subPath : children) {

                String fullPath;
                //如果是根路径
                if (subPath.equals("/")) {
                    fullPath = PATH_PREFIX + path + "/" + subPath;

                } else {
                    fullPath = PATH_PREFIX + "/" + path + "/" + subPath;
                }
                dataList.add(this.getData(fullPath));
            }

            return dataList;

        } catch (Exception e) {
            log.error("getChildrenData error.",e);
        }
        return null;
    }

    /**
     * 获取节点数据
     * @param fullPath
     * @return
     */
    public String getData(String fullPath) {
        try {

            byte[] data = zookeeper.getData(fullPath,false,null);

            return new String(data, Charset.forName("utf-8"));
        } catch (Exception e) {
            log.error("getData error.",e);
        }
        return null;
    }

    public void createNodeEphemeral(String path,String value) {
        try {
            zookeeper.create(path,value.getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);


        } catch (KeeperException e) {
            log.error("createNodeEphemeral error.",e);
        } catch (InterruptedException e) {
            log.error("createNodeEphemeral error.",e);
        }

    }
    @Override
    public void afterPropertiesSet() throws Exception {
        zookeeper = new ZooKeeper(connectString,3000,this);
    }


    @Override
    public void process(WatchedEvent event) {

    }
}

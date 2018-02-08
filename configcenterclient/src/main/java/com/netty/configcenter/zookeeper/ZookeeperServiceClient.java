package com.netty.configcenter.zookeeper;

import com.netty.configcenter.factory.ZookeeperFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author：zeqi
 * @Date: Created in 14:12 6/2/18.
 * @Description:
 */
@Slf4j
public class ZookeeperServiceClient  {

    private static ZooKeeper zookeeper;

    private String connectString;


    public ZookeeperServiceClient(String zkHost) {

        this.connectString = zkHost;
        getZooKeeperFromFactory();
    }
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
                if (path.equals("/")) {
                    fullPath = path + "/" + subPath;

                } else {
                    fullPath = path + "/" + subPath;
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

            //节点不存在，返回null
            if (zookeeper.exists(fullPath,false) == null) {
                return null;
            }

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
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);


        } catch (KeeperException e) {
            log.error("createNodeEphemeral error.",e);
        } catch (InterruptedException e) {
            log.error("createNodeEphemeral error.",e);
        }

    }

    /**
     * 获取Zookeeper实例
     */
    public void getZooKeeperFromFactory() {

        try {
            zookeeper = ZookeeperFactory.getZooKeeper(connectString);
        } catch (IOException e) {

            log.error("fail to connect zookeeper.zkHost{}", connectString, e);
        }

    }

    /**
     * zk关闭
     */
    public void closeZk() {

        try {
            if (zookeeper != null) {
                zookeeper.close();
            }
        } catch (Exception e) {
            log.error("fail to closeZk zookeeper." ,e);
        }
    }



}

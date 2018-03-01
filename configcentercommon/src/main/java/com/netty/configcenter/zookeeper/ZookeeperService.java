package com.netty.configcenter.zookeeper;

import com.netty.configcenter.constant.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
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
public class ZookeeperService implements InitializingBean, Watcher {

    private static ZooKeeper zookeeper;

    @Value("${zk.server}")
    private String connectString;

    /**
     * 创建持久化序列节点
     *
     * @param path
     * @param value
     */
    public void createSeqNode(String path, String value) {
        try {
            zookeeper.create(path, value.getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);

        } catch (KeeperException e) {
            log.error("createSeqNode error.", e);

        } catch (InterruptedException e) {
            log.error("createSeqNode error.", e);
        }

    }

    /**
     * 创建持久化节点
     *
     * @param path
     * @param value
     */
    public void createNode(String path, String value) {
        try {
            zookeeper.create(path, value.getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        } catch (KeeperException e) {
            log.error("createNode error.", e);

        } catch (InterruptedException e) {
            log.error("createNode error.", e);
        }

    }


    /**
     * 递归的创建持久化节点，如果父节点不存在，就创建
     *
     * @param path
     * @param value
     */
    public void createNodeRecursively(String path, String value) {

        // 例如：path：/config/center/data/serverlist
        try {
            String[] nodes = null;
            if (path.startsWith("/")) {
                //去掉节点前的"/"
                path = path.substring(1);
            }
            //按照"/" 分割
            nodes = path.split("/");

            String pathToCreate = "";
            for (int i = 0; i < nodes.length; i++) {
                pathToCreate = pathToCreate + "/" + nodes[i];
                //如果不存在，创建节点
                if (zookeeper.exists(pathToCreate, false) == null) {
                    //如果是最后一个节点，需要设置节点的值
                    if (i == nodes.length - 1) {
                        this.createNode(pathToCreate, value);
                    } else {
                        this.createNode(pathToCreate, "");
                    }
                }

            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    /**
     * 获取子节点
     *
     * @param path
     * @return
     */
    public List<String> getChildren(String path) {
        try {

            List<String> childList = zookeeper.getChildren(path, false);

            return childList;
        } catch (Exception e) {
            log.error("getChildren error.", e);
        }
        return null;
    }

    /**
     * 获取子节点数据列表
     *
     * @param path
     * @return
     */
    public List<String> getChildrenData(String path) {
        try {
            List<String> children = this.getChildren(path);

            if (CollectionUtils.isEmpty(children)) {
                return null;
            }
            List<String> dataList = new ArrayList<>();
            ;
            for (String subPath : children) {

                String fullPath;
                //如果是根路径
                if (subPath.equals("/")) {
                    fullPath = Constants.PATH_PREFIX + path + "/" + subPath;

                } else {
                    fullPath = Constants.PATH_PREFIX + "/" + path + "/" + subPath;
                }
                dataList.add(this.getData(fullPath));
            }

            return dataList;

        } catch (Exception e) {
            log.error("getChildrenData error.", e);
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


    /**
     * 判断是否存在
     * @param path
     */
    public boolean exists(String path) {
        try {

            Stat stat = zookeeper.exists(path,false);
            if (stat != null)  {
               return true;
            }

        } catch (Exception e) {
            log.error("exists method error.path:{}",path ,e);
        }
        return false;

    }

    /**
     * 判断是否存在
     * @param path
     */
    public Stat existsAndReturnStat(String path) {
        try {

            Stat stat = zookeeper.exists(path,false);
            return stat;

        } catch (Exception e) {
            log.error("exists method error.path:{}",path ,e);
        }
        return null;

    }

    /**
     * 设置数据值
     * @param path
     * @param value
     */
    public void setData(String path,String value) {
        try {

            Stat stat = existsAndReturnStat(path);

            if (stat != null)  {
                zookeeper.setData(path, value.getBytes(), stat.getVersion());
            }

        } catch (Exception e) {
            log.error("fail to set data." ,e);
        }

    }

    /**
     * 创建临时序列化节点
     *
     * @param path
     * @param value
     */
    public void createNodeSeqEphemeral(String path, String value) {
        try {
            zookeeper.create(path, value.getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);


        } catch (KeeperException e) {
            log.error("createNodeSeqEphemeral error.", e);
        } catch (InterruptedException e) {
            log.error("createNodeSeqEphemeral error.", e);
        }

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        zookeeper = new ZooKeeper(connectString, 3000, this);
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
            log.error("fail to closeZk zookeeper.", e);
        }
    }

    @Override
    public void process(WatchedEvent event) {

    }
}

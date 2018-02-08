package com.netty;

import org.apache.zookeeper.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @Author：zeqi
 * @Date: Created in 15:28 6/2/18.
 * @Description:
 */
public class ZookeeperTest {


    private ZooKeeper zookeeper;

    private static final String PATH_PREFIX = "/config/center/data/serverlist";


    @Before
    public void initZookeeper() {
        try {
            zookeeper = new ZooKeeper("localhost:2181", 3000, new Watcher() {
                @Override
                public void process(WatchedEvent event) {

                }

                ;
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testZookeeperCreateNode() {
        createNode(PATH_PREFIX + "/" + "server1", "127.0.0.1:8082");

    }

    @Test
    public void testZookeeperCreateNodeRecursively() {

        this.createNodeRecursively("/test/mytest/node","testdata");
    }

    @Test
    public void testZookeeperCreatePersistentSequentialNode() {
        createPersistentSequentialNode(PATH_PREFIX + "/" + "server_seq", "127.0.0.1:8082");

    }


    @Test
    public void testZookeeperCreatePersistentSequentialNode2() {
        createPersistentSequentialNode(PATH_PREFIX + "/" + "server_seq", "127.0.0.1:8082");

    }

    @Test
    public void testZookeeperCreatePersistentSequentialNode3() {
        createPersistentSequentialNode(PATH_PREFIX + "/" + "server_seq", "127.0.0.1:8082");

    }


    @Test
    public void testZookeeperGetChildren() {
        List<String> children = this.getChildren(PATH_PREFIX);

        for (String path : children) {
            String fullPath = "";
            if (path.equals("/")) {
                fullPath = PATH_PREFIX + path;

            } else {
                fullPath = PATH_PREFIX + "/" + path;
            }
            System.out.println(this.getData(fullPath));
        }

    }

    public void createNode(String path, String value) {
        try {

            zookeeper.create(path, value.getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createPersistentSequentialNode(String path, String value) {
        try {

            zookeeper.create(path, value.getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public List<String> getChildren(String path) {
        try {

            List<String> stringList = zookeeper.getChildren(path, false);


            System.out.println(stringList);

            return stringList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getData(String path) {
        try {

            byte[] data = zookeeper.getData(path, false, null);

            return new String(data, Charset.forName("utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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

}

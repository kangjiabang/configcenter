package com.netty.configcenter.factory;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author：zeqi
 * @Date: Created in 16:38 8/2/18.
 * @Description:
 */
@Slf4j
public class ZookeeperFactory {


    private static ConcurrentHashMap<String, ZooKeeper> concurrentHashMap = new ConcurrentHashMap(1);

    public ZookeeperFactory() {

    }

    /**
     * 获取zk实例
     * @param zkHost
     * @return
     * @throws IOException
     */
    public static ZooKeeper getZooKeeper(String zkHost) throws IOException {

        try {
            if (concurrentHashMap.get(zkHost) == null) {

                synchronized (concurrentHashMap) {

                    if (concurrentHashMap.get(zkHost) == null) {

                        concurrentHashMap.putIfAbsent(zkHost, new ZooKeeper(zkHost, 3000, new Watcher() {
                            @Override
                            public void process(WatchedEvent event) {
                                //do noting
                            }
                        }));
                    }

                }

            }
            return concurrentHashMap.get(zkHost);
        } catch (IOException e) {

            throw e;
        }

    }

}

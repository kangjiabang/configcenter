package com.netty;

import com.netty.configcenter.ConfigServerApplication;
import com.netty.configcenter.zookeeper.ZookeeperServiceClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @Author：zeqi
 * @Date: Created in 14:47 6/2/18.
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes= ConfigServerApplication.class)
public class ZookeeperServiceTest {

    @Autowired
    private ZookeeperServiceClient zookeeperService;
    @Test
    public void testZookeeperCreateNode() {
        zookeeperService.createNode(zookeeperService.PATH_PREFIX+ "/"+ "serverlist/server","127.0.0.1:2083");
    }

    @Test
    public void testZookeeperCreateSeqNode() {
        zookeeperService.createSeqNode(zookeeperService.PATH_PREFIX+ "/"+"serverlist/server_seq_","127.0.0.1:2083");
    }


    @Test
    public void testZookeeperGetChildrenData() {
        List<String> listData = zookeeperService.getChildrenData(zookeeperService.PATH_PREFIX+ "/"+"serverlist");
        System.out.println(listData);
    }

    @Test
    public void testZookeeperEphemeral() {
        zookeeperService.createNodeEphemeral(zookeeperService.PATH_PREFIX+ "/"+"serverlist/server1","127.0.0.1");

    }
}

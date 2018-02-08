package com.netty;

import com.netty.configcenter.ConfigServerApplication;
import com.netty.configcenter.constant.Constants;
import com.netty.configcenter.zookeeper.ZookeeperService;
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
    private ZookeeperService zookeeperService;
    @Test
    public void testZookeeperCreateNode() {
        zookeeperService.createNode(Constants.PATH_PREFIX+ "/"+ "serverlist/server","127.0.0.1:2083");
    }

    @Test
    public void testZookeeperCreateSeqNode() {
        zookeeperService.createSeqNode(Constants.PATH_PREFIX+ "/"+"serverlist/server_seq_","127.0.0.1:2083");
    }


    @Test
    public void testZookeeperGetChildrenData() {
        List<String> listData = zookeeperService.getChildrenData(Constants.PATH_PREFIX+ "/"+"serverlist");
        System.out.println(listData);
    }

    @Test
    public void testZookeeperEphemeral() {
        zookeeperService.createNodeSeqEphemeral(Constants.PATH_PREFIX+ "/"+"serverlist/server1","127.0.0.1");

    }
}

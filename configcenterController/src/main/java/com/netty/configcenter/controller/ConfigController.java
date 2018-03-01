package com.netty.configcenter.controller;

import com.netty.configcenter.constant.Constants;
import com.netty.configcenter.exception.NodeExistsException;
import com.netty.configcenter.exception.NodeNotExistsException;
import com.netty.configcenter.exception.NodeValueNotChangedException;
import com.netty.configcenter.model.ConfigItem;
import com.netty.configcenter.nio.ConfigNioClient;
import com.netty.configcenter.utils.PathUtils;
import com.netty.configcenter.zookeeper.ZookeeperService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

/**
 * @Author：zeqi
 * @Date: Created in 16:47 5/2/18.
 * @Description:
 */
@RestController
@RequestMapping("/config")
@Slf4j
public class ConfigController {


   // @Autowired
    //private ChannelManager channelManager;

    @Autowired
    private ZookeeperService zookeeperService;

    @RequestMapping(value = "/modify", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public boolean modifyConfig(@RequestParam(value = "module", required = true) String module,
                                @RequestParam(value = "subModule", required = true) String subModule, @RequestParam(value = "key", required = true) String key,
                                @RequestParam(value = "value", required = true) String value) {


        try {
            ConfigItem configItem = ConfigItem.builder().
                    module(module).subModule(subModule)
                    .key(key).value(value).build();

            String path = PathUtils.buildConfigData(module, subModule, key);


            //首先查询是否存在
            try {
                checkModifyConditions(path, value);
            } catch (NodeNotExistsException  e) {

                log.error("configNode not exists.module:{},subModule:{},key:{},e", module, subModule, key,e);
                return false;
            } catch (NodeValueNotChangedException e) {
                log.error("configNode  value not changed.module:{},subModule:{},key:{},e",
                        module, subModule, key,e);
                return false;
            }

            //如果节点存在，修改值
            zookeeperService.setData(path, value);

            //通知对应的服务端配置项发生改变
            notifyServerValueChanged(configItem);
            //channelManager.messageChanged(configItem);


            return true;
        } catch (Exception e) {
            log.error("modifyConfig error.", e);
            return false;
        }


    }

    /**
     * 通知相应的configServer改变值
     * @param configItem
     */
    private void notifyServerValueChanged(ConfigItem configItem) {

        if (configItem == null) {
            return;
        }
        String nodeServerPath = PathUtils.buildNodeServerMappingPath(configItem.getModule(),configItem.getSubModule(),configItem.getKey());


        String serverAddress = zookeeperService.getData(nodeServerPath);

        //服务器地址不为空
        if (StringUtils.isNotEmpty(serverAddress)) {
            String[] ipAndPort = StringUtils.split(serverAddress,":");
            //do notify server
            doNotifyServer(ipAndPort,configItem);
        }
    }

    /**
     * 建立socket连接，通知服务端消息改变
     * @param ipAndPort
     * @param configItem
     */
    private void doNotifyServer(String[] ipAndPort, ConfigItem configItem) {
        log.info("start to notify server:{},value:{}", Arrays.toString(ipAndPort),configItem);

        //通知Server配置项值发生改变
        new ConfigNioClient(configItem,ipAndPort);
    }

    /**
     * 是否具有修改的条件
     *
     * @param path
     * @return
     */
    private void checkModifyConditions(String path, String newValue) {

        if (!zookeeperService.exists(path)) {

            throw new NodeNotExistsException("configNode not exists.");
        }
        String oldValue = zookeeperService.getData(path);

        if (newValue.equals(oldValue)) {

            throw new NodeValueNotChangedException("node value not changed.");
        }
    }

    /**
     * 是否具有修改的条件
     *
     * @param path
     * @return
     */
    private void checkCreateConditions(String path) {

        if (zookeeperService.exists(path)) {

            throw new NodeExistsException("configNode exists.");
        }

    }


    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public boolean createConfig(@RequestParam(value = "module", required = true) String module,
                                @RequestParam(value = "subModule", required = true) String subModule,
                                @RequestParam(value = "key", required = true) String key,
                                @RequestParam(value = "value", required = true) String value) {

        try {

            String path = PathUtils.buildConfigData(module, subModule, key);

            //首先查询是否存在
            try {
                checkCreateConditions(path);
            } catch (NodeExistsException e) {
                log.error("configNode already exists.module:{},subModule:{},key:{},e", module, subModule, key,e);
                return false;
            }

            //如果节点不存在，直接创建，并设置值
            zookeeperService.createNodeRecursively(PathUtils.buildConfigData(module, subModule, key), value);
            return true;
        } catch (Exception e) {
            log.error("createConfig error.", e);
            return false;
        }


    }


}

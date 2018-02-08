package com.netty.configcenter.controller;

import com.netty.configcenter.constant.Constants;
import com.netty.configcenter.exception.NodeExistsException;
import com.netty.configcenter.exception.NodeNotExistsException;
import com.netty.configcenter.exception.NodeValueNotChangedException;
import com.netty.configcenter.server.ChannelManager;
import com.netty.configcenter.model.ConfigItem;
import com.netty.configcenter.utils.PathUtils;
import com.netty.configcenter.zookeeper.ZookeeperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author：zeqi
 * @Date: Created in 16:47 5/2/18.
 * @Description:
 */
@RestController
@RequestMapping("/config")
@Slf4j
public class ConfigController {


    @Autowired
    private ChannelManager channelManager;

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

            //刷新缓存
            channelManager.messageChanged(configItem);


            return true;
        } catch (Exception e) {
            log.error("modifyConfig error.", e);
            return false;
        }


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

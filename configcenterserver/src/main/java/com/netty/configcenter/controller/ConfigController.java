package com.netty.configcenter.controller;

import com.netty.configcenter.server.ChannelManager;
import com.netty.configcenter.model.ConfigItem;
import com.netty.configcenter.zookeeper.ZookeeperService;
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
public class ConfigController {


    @Autowired
    private ChannelManager channelManager;

    @Autowired
    private ZookeeperService zookeeperService;

    @RequestMapping(value = "/modify", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public boolean  modifyConfig(@RequestParam(value = "module",required = true) String module,
    @RequestParam(value="subModule",required = true) String subModule,@RequestParam(value="key",required = true) String key,
    @RequestParam(value="value",required = true) String value)  {


        try {
            ConfigItem configItem = ConfigItem.builder().
                    module(module).subModule(subModule)
                    .key(key).value(value).build();

            //zookeeperService.createNode();
            channelManager.messageChanged(configItem);
            return true;
        } catch (Exception e) {
            return false;
        }


    }


}

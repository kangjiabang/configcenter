package com.netty.configcenter;

import com.netty.configcenter.client.ConfigClient;
import com.netty.configcenter.event.MessageChangedEvent;
import com.netty.configcenter.listener.MessageConfigListener;

import java.util.concurrent.TimeUnit;

/**
 * @Author：zeqi
 * @Date: Created in 18:16 2/2/18.
 * @Description:
 */
public class MainClient {
    public static void main(String[] args) {
        ConfigClient client = new ConfigClient("loan", "magina", "whiteList");

        client.addListener(new MessageConfigListener<MessageChangedEvent>() {

           @Override
           public void messageChanged(MessageChangedEvent event) {
               System.out.println("listener:" + event.getMessage());
           }
       }

        );

        System.out.println("config value:" + client.getValue());


        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("get config value again:" + client.getValue());

    }
}

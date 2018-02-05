package netty.configcenter.client;

import netty.configcenter.event.MessageEvent;
import netty.configcenter.listener.MessageChangedListener;

import java.util.concurrent.TimeUnit;

/**
 * @Author：zeqi
 * @Date: Created in 18:16 2/2/18.
 * @Description:
 */
public class MainClient {
    public static void main(String[] args) {
        ConfigClient client = new ConfigClient("loan", "magina", "whiteList");

        client.addListener(new MessageChangedListener() {

           @Override
           public void messageChanged(MessageEvent event) {
               System.out.println("listener:" + event.getMessage());
           }
       }

        );

        System.out.println("value:" + client.getValue(5, TimeUnit.SECONDS));


        /*for (int i=0;i<1;i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                        //ConfigClient client = new ConfigClient("loan","magina","whiteList");
                }
            }).start();
        }*/


    }
}

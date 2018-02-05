package netty.configcenter.handler;


import netty.configcenter.channel.ConfigItemChannel;

/**
 * @Author：zeqi
 * @Date: Created in 23:57 1/2/18.
 * @Description:
 */
public interface ConfigHandler {

    /**
     * 处理信息
     * @param configItemChannel
     * @param msg
     */
     void handle(ConfigItemChannel configItemChannel, Object msg);
}

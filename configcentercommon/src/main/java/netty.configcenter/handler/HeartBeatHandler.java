package netty.configcenter.handler;

import lombok.extern.slf4j.Slf4j;
import netty.configcenter.channel.ConfigItemChannel;
import netty.configcenter.common.OpCode;
import netty.configcenter.model.Packet;

/**
 * @Author：zeqi
 * @Date: Created in 23:56 1/2/18.
 * @Description:
 */
@Slf4j
public class HeartBeatHandler implements ConfigHandler {

    /**
     * 处理心跳信息
     * @param configItemChannel
     * @param msg
     */
    @Override
    public void handle(ConfigItemChannel configItemChannel,Object msg) {
        if (msg instanceof Packet) {
            Packet packet = (Packet)msg;

            if (log.isDebugEnabled()) {
                log.debug("set last read time");
            }

            //set Time for  heatbeatTask
            configItemChannel.setAttribute(ConfigItemChannel.LAST_READ_TIME,System.currentTimeMillis());
            if (packet.getHeader() == OpCode.HEARTBEAT) {
                    //do nothing
            }
        } else {
            //do nothing
            //throw new RuntimeException("message is not instance of Packet and can not dealed");
        }
    }
}

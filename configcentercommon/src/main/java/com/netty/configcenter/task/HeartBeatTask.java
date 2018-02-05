package com.netty.configcenter.task;

import com.netty.configcenter.channel.ConfigItemChannel;
import com.netty.configcenter.model.Packet;
import lombok.extern.slf4j.Slf4j;
import com.netty.configcenter.common.OpCode;

/**
 * @Author：zeqi
 * @Date: Created in 18:35 1/2/18.
 * @Description:
 */
@Slf4j
public class HeartBeatTask implements Runnable {

    private ConfigItemChannel channel;

    private long lastRead;

    public HeartBeatTask(ConfigItemChannel channel) {
        this.channel = channel;
    }

    @Override
    public void run() {

        while (true) {
            try {
                Thread.sleep(1000);

                long now = System.currentTimeMillis();
                Long lastRead = (Long) channel.getAttribute(ConfigItemChannel.LAST_READ_TIME);

                if (log.isDebugEnabled()) {
                    log.debug("last read:" + lastRead);
                }

                if (lastRead != null) {
                    //判断是否超时
                    long timeAck = now - lastRead;

                    if (log.isDebugEnabled()) {
                        log.debug("time spent since last read:" + timeAck);
                    }

                }
                if (lastRead != null && now - lastRead > 5000) {

                    Packet packet = Packet.builder().message("send ping to Server").
                            header(OpCode.HEARTBEAT).build();
                    channel.getChannel().writeAndFlush(packet);
                }
                if (lastRead != null && now - lastRead > 1000 * 1000) {

                    if (log.isDebugEnabled()) {
                        log.debug("disconnected :");
                    }

                    channel.getChannel().close();
                }
            } catch (Exception e) {

                //to log
                e.printStackTrace();
            }


        }


    }


    public ConfigItemChannel getChannel() {
        return channel;
    }

    public void setChannel(ConfigItemChannel channel) {
        this.channel = channel;
    }

    /**
     * 判断服务器是否存活
     *
     * @return
     */
    private boolean isServerDead() {
       /* //如果一定时间没有收到心跳信息，则断开连接
        if (stopWatch.elapsed(TimeUnit.MILLISECONDS) > 20000) {
            System.out.println("server is disconnected.");
            return true;

        }*/
        return false;
    }
}

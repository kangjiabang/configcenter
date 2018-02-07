package com.netty.configcenter.task;

import com.netty.configcenter.channel.ConfigItemChannel;
import com.netty.configcenter.context.ListenerContext;
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

    private ListenerContext listenerContext;

    public HeartBeatTask(ConfigItemChannel channel,ListenerContext listenerContext) {
        this.channel = channel;
        this.listenerContext = listenerContext;
    }

    @Override
    public void run() {

        while (true) {
            try {
                Thread.sleep(1000);

                long now = System.currentTimeMillis();
                Long lastRead = (Long) channel.getAttribute(ConfigItemChannel.LAST_READ_TIME);


                boolean disConnected = channel.getAttribute(ConfigItemChannel.DISCONNECTED) == null ? true : (Boolean) channel.getAttribute(ConfigItemChannel.DISCONNECTED);

                /*//如果断连，返回
                if (disConnected) {
                    continue;
                }*/

               /* if (log.isDebugEnabled()) {
                    log.debug("last read:" + lastRead);
                }*/

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
                if (lastRead != null && now - lastRead > 10 * 1000) {

                    if (log.isDebugEnabled()) {
                        log.debug("disconnected :");
                    }

                    listenerContext.fireServerDisconnect();

                    //设置通道标识为disconnect
                    channel.setAttribute(ConfigItemChannel.DISCONNECTED,true);

                    //Task 和channel的生命周期相同，如果channel关闭，对应的task也应该结束
                     break;

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

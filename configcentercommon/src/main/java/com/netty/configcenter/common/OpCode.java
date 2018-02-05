package com.netty.configcenter.common;

/**
 * @Author：zeqi
 * @Date: Created in 17:11 1/2/18.
 * @Description:
 */
public interface OpCode {

    int HEARTBEAT = -1; //"心跳"
    int FIRST_REGISTER = 1; //"首次注册"
    int CONFIG_CHANGED = 2; //"配置改变"

}

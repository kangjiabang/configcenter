package netty.configcenter.channel;

/**
 * @Author：zeqi
 * @Date: Created in 23:30 1/2/18.
 * @Description:
 */
public interface ConfChannel {

    /**
     * 设置渠道属性
     * @param key
     * @param value
     */
     void setAttribute(String  key,Object value);


    /**
     * 获取渠道属性
     * @param key
     */
    Object getAttribute(String  key);
}

package netty.configcenter.channel;

import io.netty.channel.Channel;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author：zeqi
 * @Date: Created in 21:12 31/1/18.
 * @Description:
 */
@Data
public class ConfigItemChannel implements ConfChannel {


    public static final String LAST_READ_TIME = "lastReadTime";

    private Map<String,Object> atrributes = new ConcurrentHashMap<>();

    private Channel channel;

    public ConfigItemChannel(Channel channel) {

        this.channel = channel;
    }

    /**
     * 设置渠道属性
     * @param key
     * @param value
     */
    @Override
    public void setAttribute(String  key,Object value) {
        atrributes.put(key, value);
    }

    /**
     * 获取渠道属性
     * @param key
     */
    @Override
    public Object getAttribute(String key) {
        return atrributes.get(key);
    }
}

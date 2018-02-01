package netty.configcenter.channel;

import io.netty.channel.Channel;
import lombok.Data;
import netty.configcenter.model.ConfigItem;

/**
 * @Author：zeqi
 * @Date: Created in 21:12 31/1/18.
 * @Description:
 */
@Data
public class ConfigChannel {

    private ConfigItem configItem;

    private Channel channel;

    public ConfigChannel(ConfigItem configItem, Channel channel) {
        this.configItem = configItem;
        this.channel = channel;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConfigChannel that = (ConfigChannel) o;

        if (!configItem.equals(that.configItem)) return false;
        return channel.equals(that.channel);
    }

    @Override
    public int hashCode() {
        int result = configItem.hashCode();
        result = 31 * result + channel.hashCode();
        return result;
    }
}

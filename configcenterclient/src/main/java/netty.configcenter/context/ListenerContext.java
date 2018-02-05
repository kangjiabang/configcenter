package netty.configcenter.context;

import com.alibaba.fastjson.JSON;
import io.netty.handler.codec.json.JsonObjectDecoder;
import netty.configcenter.event.MessageEvent;
import netty.configcenter.listener.MessageChangedListener;
import netty.configcenter.model.ConfigItem;

/**
 * @Author：zeqi
 * @Date: Created in 20:58 31/1/18.
 * @Description:
 */
public class ListenerContext {

    private MessageChangedListener listener;

    public ListenerContext() {
    }

    public void addListener(MessageChangedListener listener) {
        this.listener = listener;
    }


    public void fireMessageChaned(ConfigItem item) {

        MessageEvent event = new MessageEvent(JSON.toJSONString(item),"1","0");

        if (listener != null) {
            listener.messageChanged(event);
        }
    }
}

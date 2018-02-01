package netty.configcenter.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author：zeqi
 * @Date: Created in 15:20 1/2/18.
 * @Description:
 */
@Data
@Builder
public class Packet implements Serializable {

    private  int header;

    private ConfigItem configItem;
}

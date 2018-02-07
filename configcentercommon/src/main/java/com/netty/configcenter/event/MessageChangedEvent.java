package com.netty.configcenter.event;

/**
 * @Author：zeqi
 * @Date: Created in 20:51 31/1/18.
 * @Description:
 */
public class MessageChangedEvent extends ConfigEvent {

    public MessageChangedEvent(String message, String type, String version) {
        super(null);
        this.message = message;
        this.type = type;
        this.version = version;
    }

    private String message;

    private String type;

    private String version;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}

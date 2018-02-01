package netty.configcenter.enums;

/**
 * @Author：zeqi
 * @Date: Created in 17:11 1/2/18.
 * @Description:
 */
public enum HeaderEnum {

    HEARTBEAT(-1,"心跳"),
    FIRST_REGISTER(1,"首次注册"),
    CONFIG_CHANGED(2,"配置改变"),;



    HeaderEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private int  code;
    private String  desc;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

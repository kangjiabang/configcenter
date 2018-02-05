package com.netty.configcenter.model;

import lombok.*;

import java.io.Serializable;

/**
 * @Author：zeqi
 * @Date: Created in 21:09 31/1/18.
 * @Description:
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ConfigItem implements Serializable  {

    private String module;
    private String subModule;
    private String key;

    private String value;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConfigItem that = (ConfigItem) o;

        if (module != null ? !module.equals(that.module) : that.module != null) return false;
        if (subModule != null ? !subModule.equals(that.subModule) : that.subModule != null) return false;
        return key != null ? key.equals(that.key) : that.key == null;
    }

    @Override
    public int hashCode() {
        int result = module != null ? module.hashCode() : 0;
        result = 31 * result + (subModule != null ? subModule.hashCode() : 0);
        result = 31 * result + (key != null ? key.hashCode() : 0);
        return result;
    }
}

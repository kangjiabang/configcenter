package com.netty.configcenter.utils;

import com.netty.configcenter.constant.Constants;

/**
 * @Author：zeqi
 * @Date: Created in 15:33 8/2/18.
 * @Description:
 */
public class PathUtils {

    /**
     * 构建存放的目录
     * @param module
     * @param subModule
     * @param key
     * @return
     */
    public static String buildConfigData(String module,String subModule,String key) {
        return Constants.PATH_CONFIG_DATA + "/" + module + "/" + subModule +"/" + key;
    }
}

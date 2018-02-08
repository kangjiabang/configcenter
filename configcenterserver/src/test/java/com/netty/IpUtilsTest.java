package com.netty;

import com.netty.configcenter.utils.IpUtils;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Author：zeqi
 * @Date: Created in 15:12 8/2/18.
 * @Description:
 */
public class IpUtilsTest {

    @Test
    public void testIpUtils() {
        System.out.println(IpUtils.getLocalHostLANAddress());
        System.out.println(IpUtils.getLocalHostLANAddress().getHostAddress());
        try {
            System.out.println(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}

package com.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

/**
 * @Author：zeqi
 * @Date: Created in 10:50 9/2/18.
 * @Description:
 */
public class ByteBufferTest {

    public static void main(String[] args) {
        new ByteBufferTest().testByteBuffer();
    }
    //@Test
    public void testByteBuffer() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ByteBuf buf =  Unpooled.buffer(1024);

       buf.writeBytes("ni hao".getBytes());
       //buf.release();
    }
}

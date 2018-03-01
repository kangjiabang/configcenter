package netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.ResourceLeakDetector;
import org.junit.Test;

/**
 * @Author：zeqi
 * @Date: Created in 10:50 9/2/18.
 * @Description:
 */
public class ByteBufferTest {

    ByteBuf buf =  Unpooled.buffer(1024);
    public static void main(String[] args) {
        //ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);
        ByteBuf buf = Unpooled.buffer(12);
        System.out.println("buf refcnt:" + buf.refCnt());
        assert buf.refCnt() == 1;
        new ByteBufferTest().testByteBuffer();
    }
    //@Test
    public void testByteBuffer() {

        while (true) {
            //ByteBuf buf =  Unpooled.buffer(1024);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            buf.writeBytes(("ni lfiaufewurawruaewoaferaerew" +
                    "aerawer" +
                    "ruawerewrawewerawrawererrsaer" +
                    "hao").getBytes());
        }

       //buf.release();
    }
}

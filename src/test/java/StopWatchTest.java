import com.google.common.base.Stopwatch;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * @Author：zeqi
 * @Date: Created in 20:24 1/2/18.
 * @Description:
 */
public class StopWatchTest {

    @Test
    public void stopWatchTest() {
        Stopwatch stopwatch = Stopwatch.createUnstarted();
        stopwatch.start();

        System.out.println("time spend: " + stopwatch.elapsed(TimeUnit.MICROSECONDS));

        System.out.println("time spend: " + stopwatch.elapsed(TimeUnit.MICROSECONDS));

        stopwatch.stop();

        System.out.println("time spend: " + stopwatch.elapsed(TimeUnit.MICROSECONDS));

    }
}

package netty.configcenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author：zeqi
 * @Date: Created in 15:11 5/2/18.
 * @Description:
 */
@SpringBootApplication
public class ConfigServerApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}

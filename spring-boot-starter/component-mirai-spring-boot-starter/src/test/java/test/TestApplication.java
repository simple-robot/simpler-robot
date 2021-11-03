package test;

import love.forte.simbot.annotation.SimbotApplication;
import love.forte.simbot.spring.autoconfigure.EnableSimbot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author ForteScarlet
 */
@SimbotApplication
@EnableSimbot(appClass = TestApplication.class)
@SpringBootApplication
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}

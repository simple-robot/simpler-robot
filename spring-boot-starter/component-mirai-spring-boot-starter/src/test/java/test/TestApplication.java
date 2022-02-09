package test;

import love.forte.simbot.annotation.SimbotApplication;
import love.forte.simbot.bot.Bot;
import love.forte.simbot.bot.BotManager;
import love.forte.simbot.bot.BotVerifyInfo;
import love.forte.simbot.spring.autoconfigure.EnableSimbot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ForteScarlet
 */
@SimbotApplication
@EnableSimbot//(appClass = TestApplication.class)
@SpringBootApplication
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

    @RestController
    public static class MyController {
        @Autowired
        private BotManager manager;

        @GetMapping("/login")
        public String newBot() {
            final Bot bot = manager.registerBot(BotVerifyInfo.withCodeVerification("123", "456"));
            System.out.println("!");
            return bot.getBotInfo().getBotCode();
        }

    }

}

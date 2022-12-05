package demo;

import love.forte.simboot.annotation.Listener;
import love.forte.simboot.spring.autoconfigure.EnableSimbot;
import love.forte.simbot.Identifies;
import love.forte.simbot.definition.Contact;
import love.forte.simbot.event.FriendMessageEvent;
import love.forte.simbot.event.internal.BotStartedEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author ForteScarlet
 */
@EnableSimbot
@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }


    @Listener
    public void onFriend(FriendMessageEvent event) {
        System.out.println(event);
        Contact contact = event.getBot().getContact(Identifies.ID(1149159218L));
        contact.sendBlocking("Hello!");
    }
    @Listener
    public void onStart(BotStartedEvent event) {
        System.out.println(event);
    }
}

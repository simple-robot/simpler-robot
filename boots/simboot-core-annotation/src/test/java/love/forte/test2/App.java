package love.forte.test2;

import kotlin.Unit;
import love.forte.simboot.annotation.Listener;
import love.forte.simbot.event.EventListenerBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author ForteScarlet
 */
@SpringBootApplication
public class App {
    public static void main(String[] args) {
        final ConfigurableApplicationContext context = SpringApplication.run(App.class, args);
        for (EventListenerBuilder value : context.getBeansOfType(EventListenerBuilder.class).values()) {
            System.out.println(value);
        }

    }

    @Listener
    public Unit builder() {
        return Unit.INSTANCE;
    }

}

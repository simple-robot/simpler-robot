package love.forte.test;

import love.forte.simbot.annotation.SimbotApplication;
import love.forte.simbot.core.SimbotApp;
import love.forte.simbot.core.SimbotContext;

/**
 * @author ForteScarlet
 */
@SimbotApplication
public class Application {
    public static void main(String[] args) {
        SimbotContext run = SimbotApp.run(Application.class, args);

        System.out.println("Join and wait.");
        run.join();

    }
}

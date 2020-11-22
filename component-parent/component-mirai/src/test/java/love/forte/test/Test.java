package love.forte.test;

import love.forte.simbot.annotation.SimbotApplication;
import love.forte.simbot.bot.Bot;
import love.forte.simbot.bot.BotManager;
import love.forte.simbot.core.SimbotApp;
import love.forte.simbot.core.SimbotContext;

/**
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@SimbotApplication
public class Test {
    public static void main(String[] args) throws InterruptedException {
        SimbotContext context = SimbotApp.run(Test.class, args);
        BotManager manager = context.get(BotManager.class);

        for (Bot bot : manager.getBots()) {
            System.out.println(bot.getSender().getBotInfo());
        }

        Thread.sleep(5000);


        context.close();

        System.exit(-1);

    }
}

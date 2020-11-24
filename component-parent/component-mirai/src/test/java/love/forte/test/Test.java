package love.forte.test;

import love.forte.common.configuration.Configuration;
import love.forte.simbot.annotation.SimbotApplication;
import love.forte.simbot.bot.Bot;
import love.forte.simbot.bot.BotManager;
import love.forte.simbot.core.SimbotApp;
import love.forte.simbot.core.SimbotContext;
import love.forte.simbot.core.SimbotProcess;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@SimbotApplication
public class Test implements SimbotProcess {
    public static void main(String[] args) throws InterruptedException {
        SimbotApp.run(new Test(), args);
    }

    @Override
    public void pre(@NotNull Configuration config) {

    }

    @Override
    public void post(@NotNull SimbotContext context) {
        context.getBotManager().getBots().forEach(b -> {
            b.getSender().SENDER.sendPrivateMsg("1149159218", "我好了");
        });
    }
}

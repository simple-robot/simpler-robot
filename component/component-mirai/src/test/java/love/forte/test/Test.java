package love.forte.test;

import love.forte.common.configuration.Configuration;
import love.forte.simbot.annotation.SimbotApplication;
import love.forte.simbot.api.sender.Sender;
import love.forte.simbot.core.SimbotApp;
import love.forte.simbot.core.SimbotContext;
import love.forte.simbot.core.SimbotProcess;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@SimbotApplication
public class Test implements SimbotProcess {
    public static void main(String[] args) throws InterruptedException {
        SimbotApp.run(Test.class, args);
    }

    @Override
    public void pre(@NotNull Configuration config) {
    }

    @Override
    public void post(@NotNull SimbotContext context) {
        Sender s = context.getBotManager().getDefaultBot().getSender().SENDER;
        System.out.println(s.sendGroupSign(100000, "", ""));
    }
}

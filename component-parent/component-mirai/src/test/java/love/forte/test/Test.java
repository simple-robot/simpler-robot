package love.forte.test;

import love.forte.common.configuration.Configuration;
import love.forte.simbot.annotation.SimbotApplication;
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
        Thread t = new Thread(() -> {
            SimbotApp.run(new Test(), args);
        });
        t.setDaemon(true);
        t.start();

        Thread.sleep(100000);

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

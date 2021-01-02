package love.forte.test;

import love.forte.simbot.annotation.SimbotApplication;
import love.forte.simbot.core.SimbotApp;

/**
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@SimbotApplication
public class Test/* implements SimbotProcess */ {
    public static void main(String[] args) throws InterruptedException {
        SimbotApp.run(Test.class, args);
    }

    // @Override
    // public void pre(@NotNull Configuration config) {
    // }
    //
    // @Override
    // public void post(@NotNull SimbotContext context) {
    //     context.getBotManager().getBots().forEach(b -> {
    //         b.getSender().SENDER.sendPrivateMsg("1149159218", "我好了");
    //     });
    // }
}

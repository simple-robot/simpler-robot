/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */

package love.forte.test;

import love.forte.common.configuration.Configuration;
import love.forte.simbot.annotation.SimbotApplication;
import love.forte.simbot.bot.Bot;
import love.forte.simbot.bot.BotManager;
import love.forte.simbot.core.SimbotApp;
import love.forte.simbot.core.SimbotContext;
import love.forte.simbot.core.SimbotProcess;
import love.forte.simbot.core.dispatcher.CoreEventDispatcherFactory;
import org.jetbrains.annotations.NotNull;

/**
 * @author ForteScarlet
 */
@SimbotApplication
public class Test implements SimbotProcess {
    public static void main(String[] args) {
        SimbotApp.run(Test.class, args);
    }

    @Override
    public void pre(@NotNull Configuration config) {

    }

    @Override
    public void post(SimbotContext context) {
        BotManager manager = context.getBotManager();

        for (Bot bot : manager.getBots()) {
            bot.getSender().SENDER.sendPrivateMsg(1149159218, "我测试好了");
            System.out.println("==================");
            System.out.println(bot.getBotInfo().getAccountNicknameAndRemark());
            bot.getSender().GETTER.getAuthInfo().getAuths().toMap().forEach((k, v) -> {
                System.out.println(k + "\t=\t" + v);
            });
            System.out.println("==================");
       }

        CoreEventDispatcherFactory coreEventDispatcherFactory = context.get(CoreEventDispatcherFactory.class);

        System.out.println(coreEventDispatcherFactory);


    }
}

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
import love.forte.simbot.api.message.results.AuthInfo;
import love.forte.simbot.api.sender.Getter;
import love.forte.simbot.bot.Bot;
import love.forte.simbot.bot.BotManager;
import love.forte.simbot.bot.BotVerifyInfos;
import love.forte.simbot.core.SimbotApp;
import love.forte.simbot.core.SimbotContext;
import love.forte.simbot.core.SimbotProcess;
import love.forte.simbot.thing.Somethings;
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
            Getter getter = bot.getSender().GETTER;
            AuthInfo.Auths auths = getter.getAuthInfo().getAuths();
            Somethings.forEachNamed(auths, (p, t) -> {
                System.out.println(t.getName() + "\t=\t" + t.getValue());
            });
            String data = Somethings.resolveValue(getter.getAuthInfo().getAuths(), new String[]{"COOKIES", "sKey", "data"}, 0);
            System.out.println(data);
            String t = Somethings.resolveValue(getter.getAuthInfo().getAuths(), new String[]{"COOKIES", "sKey", "creationTime"}, 0);
            System.out.println(t);
            String data2 = Somethings.resolveValue(getter.getAuthInfo().getAuths(), new String[]{"COOKIES", "sKey", "data"}, 0);
            System.out.println(data2);
            String t2 = Somethings.resolveValue(getter.getAuthInfo().getAuths(), new String[]{"COOKIES", "sKey", "creationTime"}, 0);
            System.out.println(t2);
            System.out.println("=========================");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
            manager.registerBot(BotVerifyInfos.getInstance(bot.getBotInfo().getAccountCode(), ""));
            String data3 = Somethings.resolveValue(getter.getAuthInfo().getAuths(), new String[]{"COOKIES", "sKey", "data"}, 0);
            System.out.println(data3);
            String t3 = Somethings.resolveValue(getter.getAuthInfo().getAuths(), new String[]{"COOKIES", "sKey", "creationTime"}, 0);
            System.out.println(t3);
            String data4 = Somethings.resolveValue(getter.getAuthInfo().getAuths(), new String[]{"COOKIES", "sKey", "data"}, 0);
            System.out.println(data4);
            String t4 = Somethings.resolveValue(getter.getAuthInfo().getAuths(), new String[]{"COOKIES", "sKey", "creationTime"}, 0);
            System.out.println(t4);

        }


    }
}

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

import love.forte.simbot.annotation.SimbotApplication;
import love.forte.simbot.api.sender.Sender;
import love.forte.simbot.bot.Bot;
import love.forte.simbot.bot.BotManager;
import love.forte.simbot.core.SimbotApp;
import love.forte.simbot.core.SimbotContext;

/**
 * @author ForteScarlet
 */
@SimbotApplication
public class Test {
    public static void main(String[] args) {
        SimbotContext context = SimbotApp.run(Test.class, args);


        // post(context);

        context.close();

        System.out.println("context.close.");
    }

    public static void post(SimbotContext context) {
        BotManager manager = context.getBotManager();

        Bot defaultBot = manager.getDefaultBot();

        String cat = "[CAT:image,file=http://forte.love:15520/img/r]";

        Sender sender = defaultBot.getSender().SENDER;

        System.out.println(sender.sendGroupMsg(1043409458L, cat + cat + cat + cat));
        System.out.println(sender.sendGroupMsg(1043409458L, cat + cat));
        System.out.println(sender.sendGroupMsg(1043409458L, cat));

    }
}

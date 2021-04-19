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

import love.forte.simbot.api.message.results.FileInfo;
import love.forte.simbot.api.message.results.FileResult;
import love.forte.simbot.api.message.results.FileResults;
import love.forte.simbot.api.sender.AdditionalApi;
import love.forte.simbot.api.sender.Getter;
import love.forte.simbot.bot.Bot;
import love.forte.simbot.component.mirai.additional.MiraiAdditionalApis;
import love.forte.simbot.core.SimbotApp;
import love.forte.simbot.core.SimbotContext;

/**
 * @author ForteScarlet
 */
public class Test {
    public static void main(String[] args) {
        SimbotContext context = SimbotApp.run(Test.class, args);

        post(context);

        context.close();
    }

    public static void post(SimbotContext context) {
        Bot bot = context.getBotManager().getDefaultBot();

        Getter getter = bot.getSender().GETTER;

        AdditionalApi<FileResults> groupFiles = MiraiAdditionalApis.groupFiles(1043409458);

        FileResults results = getter.additionalExecute(groupFiles);

        System.out.println(results);
        for (FileResult result : results.getResults()) {
            System.out.println(result);
            FileInfo info = result.getValue();
            System.out.println(info);
            System.out.println(info.getName());
            System.out.println(info.isFile());
            System.out.println(info.isDirectory());
            System.out.println(info.getUrl());
            System.out.println();
        }
    }
}

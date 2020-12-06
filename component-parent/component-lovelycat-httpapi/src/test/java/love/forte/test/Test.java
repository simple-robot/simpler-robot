/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     Test.java
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *  *
 *  *
 *
 */

package love.forte.test;

import love.forte.common.configuration.Configuration;
import love.forte.simbot.annotation.SimbotApplication;
import love.forte.simbot.api.sender.Getter;
import love.forte.simbot.bot.Bot;
import love.forte.simbot.core.SimbotApp;
import love.forte.simbot.core.SimbotContext;
import love.forte.simbot.core.SimbotProcess;
import org.jetbrains.annotations.NotNull;

/**
 * @author ForteScarlet
 */
@SimbotApplication
public class Test implements SimbotProcess {
    public static void main(String[] args) {
        SimbotContext context = SimbotApp.run(Test.class, args);

        // LovelyCatApiTemplate api = context.get(LovelyCatApiTemplate.class);

        Bot defaultBot = context.getBotManager().getDefaultBot();

        Getter getter = defaultBot.getSender().GETTER;
        System.out.println(getter.getBotInfo());
        System.out.println(getter.getBotInfo());
        System.out.println(getter.getBotInfo());
        System.out.println(getter.getBotInfo());
        System.out.println(getter.getBotInfo());
        System.out.println(getter.getBotInfo());
        System.out.println(getter.getBotInfo());
        System.out.println(getter.getBotInfo());


        // HttpTemplate template = context.get(HttpTemplate.class);
        //
        // String url = "http://127.0.0.1:88/httpAPI";
        //
        // JsonSerializerFactory fac = context.get(JsonSerializerFactory.class);
        //
        // LovelyCatApiTemplate api = new LovelyCatApiTemplateImpl(template, url, fac);
        //
        // String botId = "wxid_bqy1ezxxkdat22";
        // String groupWxid = "11046274610@chatroom";
        // String forteWxid = "wxid_khv2ht7uwa5x22";


        // try {
        //
        // } catch (Exception e) {
        //     e.printStackTrace();
        // } finally {r
        //     System.exit(1);
        // }


    }

    @Override
    public void pre(@NotNull Configuration config) {

    }

    @Override
    public void post(@NotNull SimbotContext context) {
        // BotManager botManager = context.getBotManager();
        // if (!botManager.getBots().isEmpty()){
        //     MessageContentBuilderFactory factory = context.get(MessageContentBuilderFactory.class);
        //     MessageContentBuilder builder = factory.getMessageContentBuilder();
        //     MessageContent msg = builder.at("wxid_khv2ht7uwa5x22").text("你煞笔").build();
        //     Bot bot = botManager.getDefaultBot();
        //     bot.getSender().SENDER.sendGroupMsg("18367333210@chatroom", msg);
        // }
    }
}

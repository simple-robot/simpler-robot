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

import love.forte.simbot.annotation.SimbotApplication;
import love.forte.simbot.core.SimbotApp;
import love.forte.simbot.core.SimbotContext;

/**
 * @author ForteScarlet
 */
@SimbotApplication
public class Test {
    public static void main(String[] args) {
        SimbotContext context = SimbotApp.run(Test.class, args);

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
        // } finally {
        //     System.exit(1);
        // }


    }
}

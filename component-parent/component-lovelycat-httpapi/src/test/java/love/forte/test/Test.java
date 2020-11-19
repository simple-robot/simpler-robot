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
import love.forte.simbot.component.lovelycat.LovelyCatApiTemplateImpl;
import love.forte.simbot.core.SimbotApp;
import love.forte.simbot.core.SimbotContext;
import love.forte.simbot.http.template.HttpTemplate;

/**
 * @author ForteScarlet
 */
@SimbotApplication
public class Test {
    public static void main(String[] args) {
        SimbotContext context = SimbotApp.run(Test.class, args);

        HttpTemplate template = context.get(HttpTemplate.class);

        String url = "http://127.0.0.1:88/httpAPI";

        LovelyCatApiTemplateImpl api = new LovelyCatApiTemplateImpl(template, url);

        String id = "wxid_bqy1ezxxkdat22";
        System.out.println(api.getRobotName(id));
        System.out.println(api.getRobotHeadImgUrl(id));


        System.exit(1);
    }
}

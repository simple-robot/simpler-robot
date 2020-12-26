/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
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

package love.forte.test.timer;

import love.forte.common.ioc.annotation.Beans;
import love.forte.common.ioc.annotation.Depend;
import love.forte.simbot.bot.BotManager;
import love.forte.simbot.timer.EnableTimeTask;
import love.forte.simbot.timer.Fixed;

/**
 * @author ForteScarlet
 */
@EnableTimeTask
@Beans
public class TimeTasker {

    @Depend
    private BotManager botManager;


    @Fixed(5000)
    public void run(){
        System.out.println("time! " + System.currentTimeMillis());
        System.out.println(botManager.getDefaultBot());
    }

}

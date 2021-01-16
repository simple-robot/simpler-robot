/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     MyTest.java
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

import love.forte.common.ioc.annotation.Beans;
import love.forte.common.ioc.annotation.Depend;
import love.forte.simbot.bot.BotManager;
import love.forte.simbot.timer.EnableTimeTask;
import love.forte.simbot.timer.Fixed;

import java.util.concurrent.TimeUnit;

/**
 * @author ForteScarlet
 */
@Beans
@EnableTimeTask
public class MyTest {

    /**
     * 获取bot管理器。
     */
    @Depend
    private BotManager botManager;

    /**
     * 5分钟执行一次。
     */
    @Fixed(value = 5, timeUnit = TimeUnit.MINUTES)
    public void task() {
        // do your task...
    }

}

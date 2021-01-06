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

package love.forte.test.interceptor;

import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.api.message.events.MessageGet;
import love.forte.simbot.api.message.events.MsgGet;
import love.forte.simbot.intercept.InterceptionType;
import love.forte.simbot.listener.MsgInterceptContext;
import love.forte.simbot.listener.MsgInterceptor;
import org.jetbrains.annotations.NotNull;

/**
 * @author ForteScarlet
 */
@Beans
public class MyMsgInterceptor implements MsgInterceptor {

    @NotNull
    @Override
    public InterceptionType intercept(@NotNull MsgInterceptContext context) {
        if (context.getMsgGet() instanceof MessageGet) {
            System.out.println("msg intercept. " + context);
        }
        return InterceptionType.PASS;
    }



}

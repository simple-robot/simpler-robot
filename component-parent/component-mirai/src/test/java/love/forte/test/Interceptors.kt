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

package love.forte.test

import love.forte.common.ioc.annotation.Beans
import love.forte.simbot.intercept.InterceptionType
import love.forte.simbot.listener.ListenerInterceptContext
import love.forte.simbot.listener.ListenerInterceptor


@Beans
public class Interceptor1 : ListenerInterceptor {
    override fun intercept(context: ListenerInterceptContext): InterceptionType {
        println("intercepted.")
        return InterceptionType.`(v)`
    }
}
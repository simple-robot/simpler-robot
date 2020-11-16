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

package love.forte.simbot.listener


/**
 * 监听函数拦截链工厂，用于通过一个监听函数来构建一个拦截链。
 */
public interface ListenerInterceptChainFactory {

    /**
     * 通过一个拦截信息主体得到拦截链。
     */
    fun getInterceptorChain(context: ListenerInterceptContext): ListenerInterceptorChain
}




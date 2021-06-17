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

@file:JvmName("MsgInterceptChainFactories")
package love.forte.simbot.listener


/**
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
interface MsgInterceptChainFactory {

    /**
     * 根据一个消息主体得到一个消息拦截器链实例。
     */
    fun getInterceptorChain(context: MsgInterceptContext): MsgInterceptChain

    /**
     * 用于判断是否存在消息拦截器。
     * 可在获取拦截器链之前判断以减少资源浪费。
     */
    fun isEmpty(): Boolean
}

/**
 * 如果拦截器不为空，则获取拦截器链。
 */
public inline fun MsgInterceptChainFactory.getInterceptorChainOnNonEmpty(contextBlock: () -> MsgInterceptContext): MsgInterceptChain?
    = if (isEmpty()) null else getInterceptorChain(contextBlock())

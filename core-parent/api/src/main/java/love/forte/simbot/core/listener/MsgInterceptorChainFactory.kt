/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     MsgInterceptorChainFactory.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.listener


/**
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
interface MsgInterceptorChainFactory {

    /**
     * 根据一个消息主体得到一个消息拦截器链实例。
     */
    fun getInterceptorChain(context: MsgInterceptContext): MsgInterceptor

}
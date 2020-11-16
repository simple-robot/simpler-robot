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
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
interface MsgInterceptChainFactory {

    /**
     * 根据一个消息主体得到一个消息拦截器链实例。
     */
    fun getInterceptorChain(context: MsgInterceptContext): MsgInterceptChain
}
/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     ListenerFunction.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.listener

import love.forte.simbot.core.api.message.MsgGet


/**
 * 监听函数。
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
interface ListenerFunction {

    /**
     * 判断当前监听函数是否可以触发当前监听.
     */
    fun <T: MsgGet> isListen(onType: Class<T>): Boolean


    /**
     * 执行监听函数并返回一个执行后的响应结果。
     */
    fun invoke(): Any


}
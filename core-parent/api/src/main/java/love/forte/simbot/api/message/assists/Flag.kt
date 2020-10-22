/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     Flag.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

@file:JvmName("Flags")
package love.forte.simbot.api.message.assists

/**
 *
 * 一个 **标识** 类型接口 ;
 *
 * 标识可以是任何形式的，但是任何标识都应该存在有一个 [flag] 标识主体 ;
 *
 * 通过 [标识容器][love.forte.simbot.api.message.containers.FlagContainer] 可以得到一个标识实例。
 *
 * @author ForteScarlet <ForteScarlet@163.com>
 * @date 2020/9/2
 * @since
 */
public interface Flag<out T: FlagContent> {
    /**
     * 标识主体
     */
    val flag: T
}






/**
 * [Flag] 的基础数据实现类。
 */
public data class FlagImpl<out T: FlagContent>(override val flag: T): Flag<T>

/**
 * function param like `val flag = flag { "id" }`。
 */
// @Suppress("FunctionName")
public inline fun <T: FlagContent> flag(getFlag: () -> T): Flag<T> = FlagImpl(getFlag())


/**
 * [Flag] 的标识主体，定义了一个标识主体至少要存在一个 [id]。
 *
 * @see love.forte.simbot.api.message.events.MessageEventGet.MessageFlagContent
 *
 * @see love.forte.simbot.api.message.events.GroupMsg.FlagContent
 * @see love.forte.simbot.api.message.events.PrivateMsg.FlagContent
 * @see love.forte.simbot.api.message.events.FriendAddRequest.FlagContent
 * @see love.forte.simbot.api.message.events.GroupAddRequest.FlagContent
 *
 * @property id String
 */
public interface FlagContent {
    val id: String
}





/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     Flag.kt
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

package love.forte.simbot.common.api.message.assists

/**
 *
 * 一个 **标识** 类型接口
 *
 * 标识可以是任何形式的，但是任何标识都应该存在有一个  [id]
 *
 * 通过 [标识容器][love.forte.simbot.common.api.message.containers.FlagContainer] 可以得到一个标识实例。
 *
 * @author ForteScarlet <ForteScarlet@163.com>
 * @date 2020/9/2
 * @since
 */
interface Flag {
    /**
     * 此标识的ID
     */
    val id: String
}


/**
 *
 * [标识][Flag] 的简单实现类, 以一个单纯的字符串 [id] 作为此标识的载体。
 *
 * @property id String 唯一标识
 */
data class StringFlag(override val id: String): Flag


/**
 * 一个单例的空 [标识][Flag]
 */
object EmptyFlag: Flag {
    override val id: String = ""
    override fun toString(): String = "EmptyFlag"
}

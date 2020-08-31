/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     baseMessages.kt
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

package love.forte.simbot.common.api.message

import love.forte.simbot.common.api.message.containers.AccountContainer
import love.forte.simbot.common.api.message.containers.BotContainer
import love.forte.simbot.common.api.message.containers.FlagContainer
import love.forte.simbot.common.api.message.containers.OriginalDataContainer
import java.time.LocalDateTime


/**
 * 监听消息的父接口。
 *
 * 所有的监听消息都应当实现的容器：
 * - [原始信息容器][OriginalDataContainer],
 * - [bot基础信息容器][BotContainer],
 * - [用户容器][AccountContainer]
 * @since 2.0.0
 */
public interface MsgGet: OriginalDataContainer, BotContainer, AccountContainer {
    /** 当前监听事件消息的ID。一般情况下应当是一个唯一ID。 */
    val id: String

    /** 监听消息的消息正文文本 */
    var msg: String?

    /** 消息接收到的时间。一般是一个时间戳。 */
    val time: Long

    /** 应当重写toString方法 */
    override fun toString(): String
}

// receipt

//region 消息类型事件

/**
 * 与消息有关的事件
 * 除了[MsgGet]中的东西以外, 还要有一个[FlagContainer]。
 * 但是一般来讲, [FlagContainer.flag] 都可以用 [id]来代替。
 * 因此 [flag]提供为默认方法并使用[id]作为返回值。如果有特殊需要则重写
 */
public interface MessageEventGet: MsgGet, FlagContainer {
    /**
     * 默认使用[id]实现[flag]
     */
    @JvmDefault
    override val flag: String
        get() = id
}



/**
 * 与消息撤回有关的事件
 * 例如群消息撤回或者私聊撤回
 * 一般来讲应该可以得到撤回的[消息内容][MsgGet.msg]以及[撤回时间][recallTime]
 */
public interface MessageRecallEventGet: MsgGet {
    /**
     * 撤回时间。
     * 使用[LocalDateTime]来代表一个准确时间点以防止使用事件戳导致时间格式不统一
     */
    val recallTime: LocalDateTime
}






//endregion


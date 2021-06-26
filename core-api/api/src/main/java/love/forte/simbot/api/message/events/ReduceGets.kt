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
@file:JvmName("MsgGets")
@file:JvmMultifileClass
package love.forte.simbot.api.message.events

import love.forte.simbot.api.message.assists.ActionMotivations
import love.forte.simbot.api.message.containers.*

/*
 * 与 **减少** 相关的事件
 *
 * @author ForteScarlet <ForteScarlet@163.com>
 * @date 2020/9/1
 * @since
 */


/**
 * 好友减少事件
 */
public interface FriendReduce: ReduceEventGet


/**
 * 群友减少事件。
 *
 * 一般指群友已经减少后。
 *
 * 群友减少事件除了存在 [事件主体账号信息][AccountContainer] 以外,
 * 还应存在 [操作性账号信息][OperatingContainer].
 *
 * 但是一般来讲, [操作性账号信息][OperatingContainer]中的 **被操作者** 信息基本均等同于 [事件主体账号信息][AccountContainer],
 * 因此此接口对 **被操作者** 信息提供默认实现, 直接指向 **主体账号信息**中对应的信息.
 */
public interface GroupMemberReduce: ReduceEventGet, GroupContainer, OperatingContainer {
    /**
     * 有时候群友减少也可能代表是bot被踢出了某个群.
     * @return Boolean 事件主体是否为bot
     */
    fun isBot(): Boolean

    /**
     * [被操作者][beOperatorInfo] 默认实现为 [当前主体账户][accountInfo]
     */
    
    override val beOperatorInfo: BeOperatorInfo
        get() = accountInfo.asBeOperator()

    /**
     * 减少类型
     */
    val reduceType: Type

    /**
     * 群友减少事件类型，主要分为 **主动离开** 与 **被踢出**
     * @property actionMotivations ActionMotivations
     * @constructor
     */
    public enum class Type(override val actionMotivations: ActionMotivations): ActionMotivationContainer {
        /** 主动离开 */
        LEAVE(ActionMotivations.PROACTIVE),
        /** 被踢出 */
        KICK(ActionMotivations.PASSIVE)
    }

}

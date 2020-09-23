/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     IncreaseGets.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.api.message.events

import love.forte.simbot.core.api.message.IncreaseEventGet
import love.forte.simbot.core.api.message.assists.ActionMotivations
import love.forte.simbot.core.api.message.containers.*

/*
 * 与 **增加** 相关的事件
 *
 * @author ForteScarlet <ForteScarlet@163.com>
 * @date 2020/9/1
 * @since
 */


/**
 * 好友增加.
 *
 * 一般指代的是已经增加了之后
 */
public interface FriendIncrease: IncreaseEventGet


/**
 * 群友增加。
 *
 * 一般指代的是已经增加了之后。
 *
 * 群友增加事件除了存在 [事件主体账号信息][AccountContainer] 以外,
 * 还应存在 [操作性账号信息][OperatingContainer]。
 *
 * 但是一般来讲, [操作性账号信息][OperatingContainer]中的 **被操作者** 信息基本均等同于 [事件主体账号信息][AccountContainer],
 * 因此此接口对 **被操作者** 信息提供默认实现, 直接指向 **主体账号信息**中对应的信息.
 */
public interface GroupMemberIncrease: IncreaseEventGet, GroupContainer, OperatingContainer {
    /**
     * 有时候群友增加也可能代表是bot进入了某个群.
     * @return Boolean 事件主体是否为bot
     */
    fun isBot(): Boolean

    /**
     * [被操作者][beOperatorInfo] 默认实现为 [当前主体账户][accountInfo]
     */
    @JvmDefault
    override val beOperatorInfo: BeOperatorInfo
        get() = AccountAsBeOperator(accountInfo)

    /**
     * 增加类型
     */
    val increaseType: Type

    /**
     * 成员增加的类型.
     */
    public enum class Type(override val actionMotivations: ActionMotivations): ActionMotivationContainer {
        /**
         * 被邀请的, 一般指代入群者是被邀请的或者bot被邀请进入了某群
         */
        INVITED(ActionMotivations.PASSIVE),

        /** 主动添加的, 一般指代入群者为主动申请加入群聊或bot主动同意了申请后进入了某群 */
        PROACTIVE(ActionMotivations.PROACTIVE)
    }
}
/*
 *     Copyright (c) 2024-2026. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package love.forte.simbot.component.onebot.v11.core.event.request

import love.forte.simbot.ability.AcceptOption
import love.forte.simbot.ability.RejectOption
import love.forte.simbot.common.id.LongID
import love.forte.simbot.component.onebot.v11.core.actor.OneBotGroup
import love.forte.simbot.component.onebot.v11.core.actor.OneBotStranger
import love.forte.simbot.component.onebot.v11.core.event.OneBotBotEvent
import love.forte.simbot.component.onebot.v11.event.request.RawFriendRequestEvent
import love.forte.simbot.component.onebot.v11.event.request.RawGroupRequestEvent
import love.forte.simbot.event.FuzzyEventTypeImplementation
import love.forte.simbot.event.OrganizationJoinRequestEvent
import love.forte.simbot.event.RequestEvent
import love.forte.simbot.suspendrunner.STP
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic

public typealias OBSourceRequestEvent = love.forte.simbot.component.onebot.v11.event.request.RawRequestEvent

/**
 * OneBot组件中的 [事件请求][love.forte.simbot.component.onebot.v11.event.request.RawRequestEvent]
 * 的组件事件类型。
 *
 * @see love.forte.simbot.component.onebot.v11.event.request.RawRequestEvent
 * @see OneBotFriendRequestEvent
 * @see OneBotGroupRequestEvent
 *
 * @author ForteScarlet
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface OneBotRequestEvent : OneBotBotEvent, RequestEvent {
    override val sourceEvent: OBSourceRequestEvent

    /**
     * 接受请求
     */
    @JvmSynthetic
    override suspend fun accept()

    /**
     * 接受请求
     */
    @JvmSynthetic
    override suspend fun accept(vararg options: AcceptOption)

    /**
     * 拒绝请求
     */
    @JvmSynthetic
    override suspend fun reject()

    /**
     * 拒绝请求
     */
    @JvmSynthetic
    override suspend fun reject(vararg options: RejectOption)
}

/**
 * 好友添加申请事件
 * @see RawFriendRequestEvent
 */
public interface OneBotFriendRequestEvent : OneBotRequestEvent {
    override val sourceEvent: RawFriendRequestEvent

    /**
     * 好友添加申请始终是 [主动地][RequestEvent.Type.PROACTIVE]
     */
    override val type: RequestEvent.Type
        get() = RequestEvent.Type.PROACTIVE

    /**
     * 验证信息。
     *
     * @see RawFriendRequestEvent.comment
     */
    override val message: String
        get() = sourceEvent.comment

    /**
     * 请求 flag，在调用处理请求的 API 时需要传入
     *
     * @see RawFriendRequestEvent.flag
     */
    public val flag: String
        get() = sourceEvent.flag

    /**
     * 发送请求的 QQ 号
     *
     * @see RawFriendRequestEvent.userId
     */
    public val requesterId: LongID
        get() = sourceEvent.userId

    /**
     * 接受申请。
     *
     * @param options 好友申请中可使用的额外属性，
     * 支持 [OneBotFriendRequestAcceptOption] 下的类型。
     *
     * @see OneBotFriendRequestAcceptOption
     */
    @JvmSynthetic
    override suspend fun accept(vararg options: AcceptOption)
}


/**
 * 可使用于 [OneBotFriendRequestEvent.accept] 中的额外属性 [AcceptOption] 实现。
 */
public sealed class OneBotFriendRequestAcceptOption : AcceptOption {
    /**
     * 接受申请后为其设置一个备注
     */
    public data class Remark(val remark: String) : OneBotFriendRequestAcceptOption()


    public companion object {
        /**
         * 接受申请后为其设置一个备注
         * @see Remark
         */
        @JvmStatic
        public fun remark(remark: String): Remark = Remark(remark)
    }
}


/**
 * 群添加申请事件
 * @see RawGroupRequestEvent
 */
public interface OneBotGroupRequestEvent : OneBotRequestEvent, OrganizationJoinRequestEvent {
    override val sourceEvent: RawGroupRequestEvent

    /**
     * 申请加入的群。
     */
    @STP
    override suspend fun content(): OneBotGroup

    /**
     * 根据 [RawGroupRequestEvent.subType] 的值区分类型。
     * 如果是 `invite` 则为被动，否则（包括 `add`）均视为主动。
     *
     */
    override val type: RequestEvent.Type
        get() = when (sourceEvent.subType) {
            RawGroupRequestEvent.SUB_TYPE_INVITE -> RequestEvent.Type.PASSIVE
            RawGroupRequestEvent.SUB_TYPE_ADD -> RequestEvent.Type.PROACTIVE
            else -> RequestEvent.Type.PROACTIVE
        }

    /**
     * 验证信息。
     *
     * @see RawGroupRequestEvent.comment
     */
    override val message: String
        get() = sourceEvent.comment

    /**
     * 请求 flag，在调用处理请求的 API 时需要传入
     *
     * @see RawGroupRequestEvent.flag
     */
    public val flag: String
        get() = sourceEvent.flag

    /**
     * 发送请求的 QQ 号
     *
     * @see RawGroupRequestEvent.userId
     */
    override val requesterId: LongID
        get() = sourceEvent.userId

    /**
     * 申请者的部分信息。
     */
    @STP
    override suspend fun requester(): OneBotStranger

    /**
     * 拒绝申请。
     *
     * @param options 拒绝时可提供的额外选项。
     * 支持使用 [OneBotGroupRequestRejectOption] 下的类型。
     *
     * @see OneBotGroupRequestRejectOption
     */
    @JvmSynthetic
    override suspend fun reject(vararg options: RejectOption)

}


/**
 * 可使用于 [OneBotGroupRequestEvent.reject] 中的额外属性 [RejectOption] 实现。
 */
public sealed class OneBotGroupRequestRejectOption : RejectOption {
    /**
     * 拒绝的理由
     */
    public data class Reason(val reason: String) : OneBotGroupRequestRejectOption()

    public companion object {
        /**
         * 拒绝的理由
         *
         * @see Reason
         */
        @JvmStatic
        public fun reason(reason: String): Reason = Reason(reason)
    }
}

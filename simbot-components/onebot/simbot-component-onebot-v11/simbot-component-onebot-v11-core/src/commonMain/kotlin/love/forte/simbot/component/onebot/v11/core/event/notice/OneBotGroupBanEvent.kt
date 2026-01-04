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

package love.forte.simbot.component.onebot.v11.core.event.notice

import love.forte.simbot.common.id.LongID
import love.forte.simbot.component.onebot.v11.core.actor.OneBotGroup
import love.forte.simbot.component.onebot.v11.core.actor.OneBotMember
import love.forte.simbot.component.onebot.v11.event.notice.RawGroupBanEvent
import love.forte.simbot.event.FuzzyEventTypeImplementation
import love.forte.simbot.event.MemberEvent
import love.forte.simbot.suspendrunner.STP
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds


/**
 * 群禁言事件
 *
 * @see RawGroupBanEvent
 *
 * @author ForteScarlet
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface OneBotGroupBanEvent : OneBotNoticeEvent, MemberEvent {
    override val sourceEvent: RawGroupBanEvent

    /**
     * 事件子类型，分别表示禁言、解除禁言。
     * 可能的值: `ban`、`lift_ban`
     *
     * @see RawGroupBanEvent.subType
     */
    public val subType: String
        get() = sourceEvent.subType

    /**
     * 是否为禁言。
     * 即 [subType] == [RawGroupBanEvent.SUB_TYPE_BAN]
     */
    public val isBan: Boolean
        get() = subType == RawGroupBanEvent.SUB_TYPE_BAN

    /**
     * 群号
     */
    public val groupId: LongID
        get() = sourceEvent.groupId

    /**
     * 被操作群成员的ID
     */
    public val userId: LongID
        get() = sourceEvent.userId

    /**
     * 操作者的ID
     */
    public val operatorId: LongID?
        get() = sourceEvent.operatorId

    /**
     * 禁言时长，单位秒。
     *
     * @see RawGroupBanEvent.duration
     */
    public val durationSeconds: Long
        get() = sourceEvent.duration

    /**
     * 被禁言成员
     *
     * @throws Exception
     */
    @STP
    override suspend fun content(): OneBotMember

    /**
     * 所在群
     *
     * @throws Exception
     */
    @STP
    override suspend fun source(): OneBotGroup
}

/**
 * 禁言时长
 *
 * @see OneBotGroupBanEvent.durationSeconds
 */
public inline val OneBotGroupBanEvent.duration: Duration
    get() = durationSeconds.seconds

/**
 * 是否为解除禁言。
 * 即 [OneBotGroupBanEvent.isBan] == `false`
 *
 * @see OneBotGroupBanEvent.isBan
 */
public inline val OneBotGroupBanEvent.isLeftBan: Boolean
    get() = !isBan

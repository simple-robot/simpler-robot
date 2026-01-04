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
import love.forte.simbot.component.onebot.v11.event.notice.RawGroupAdminEvent
import love.forte.simbot.event.FuzzyEventTypeImplementation
import love.forte.simbot.event.MemberEvent
import love.forte.simbot.suspendrunner.STP


/**
 * 群管理员变动事件
 * @see RawGroupAdminEvent
 *
 * @author ForteScarlet
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface OneBotGroupAdminEvent : OneBotNoticeEvent, MemberEvent {
    override val sourceEvent: RawGroupAdminEvent

    /**
     * 群号
     */
    public val groupId: LongID
        get() = sourceEvent.groupId

    /**
     * 被操作的群成员ID
     */
    public val userId: LongID
        get() = sourceEvent.userId

    /**
     * 事件子类型，分别表示设置和取消管理员。
     * 可能的值: `set`、`unset`
     *
     * @see RawGroupAdminEvent.subType
     */
    public val subType: String
        get() = sourceEvent.subType

    /**
     * 是被 _任职_ 为管理，
     * 即 [subType] == [RawGroupAdminEvent.SUB_TYPE_SET].
     */
    public val isSet: Boolean
        get() = subType == RawGroupAdminEvent.SUB_TYPE_SET

    /**
     * 群
     *
     * @throws Exception
     */
    @STP
    override suspend fun source(): OneBotGroup

    /**
     * 被操作的群成员
     *
     * @throws Exception
     */
    @STP
    override suspend fun content(): OneBotMember
}

/**
 * 是被 _免职_ ，
 * 即 [OneBotGroupAdminEvent.isSet] == `false`.
 *
 * @see OneBotGroupAdminEvent.isSet
 */
public inline val OneBotGroupAdminEvent.isNotSet: Boolean
    get() = !isSet

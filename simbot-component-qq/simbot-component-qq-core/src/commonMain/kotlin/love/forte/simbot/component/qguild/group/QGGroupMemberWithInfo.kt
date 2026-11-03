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

package love.forte.simbot.component.qguild.group

import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.time.Timestamp
import love.forte.simbot.common.time.Timestamp.Companion.toTimestamp
import love.forte.simbot.qguild.model.group.GroupMemberInfo

/**
 * 通过 API 查询得到的 QQ 群成员，包含本次查询的完整资料。
 *
 * 可通过 [QGGroup.memberWithInfo] 查询指定成员，或通过 [QGGroup.members] 获取群成员列表。
 * [source] 保留本次查询的结果，需要刷新资料时再次查询。
 *
 * @since 5.0
 */
public abstract class QGGroupMemberWithInfo internal constructor() : QGGroupMember {
    /** 本次查询得到的群成员资料。 */
    public abstract val source: GroupMemberInfo

    override val id: ID
        get() = source.memberOpenid.ID

    override val name: String
        get() = source.username

    /** 成员在群内的身份。 */
    public val memberRole: QGGroupRole
        get() = source.memberRole.toQGGroupRole()

    /** 成员加入此群的时间。 */
    public val joinTime: Timestamp
        get() = source.joinedAt.toTimestamp()

    /** 是否为机器人账号。 */
    public val isBot: Boolean
        get() = source.bot

    /** 用户在应用或开放平台下的统一标识。 */
    public val unionOpenid: String?
        get() = source.unionOpenid
}

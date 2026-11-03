/*
 *     Copyright (c) 2026. ForteScarlet.
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

package love.forte.simbot.qguild.model.group

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.common.ApiModel
import love.forte.simbot.qguild.common.ApiModelConstructor
import kotlin.time.Instant

/**
 * 群黑名单中的用户。
 *
 * @property memberOpenid 群成员 OpenID。
 * @property username 用户昵称。
 * @property bannedAt 加入黑名单时间。
 * @property bot 是否机器人。
 * @property unionOpenid 用户在应用/开放平台下的统一标识，如有。
 * @since 5.0
 */
@ApiModel
@Serializable
public class GroupBlacklistUser @ApiModelConstructor internal constructor(
    @SerialName("member_openid")
    public val memberOpenid: String,
    public val username: String,
    @SerialName("banned_at")
    public val bannedAt: Instant,
    public val bot: Boolean,
    @SerialName("union_openid")
    public val unionOpenid: String? = null,
) {
    override fun toString(): String =
        "GroupBlacklistUser(memberOpenid=$memberOpenid, username=$username, bannedAt=$bannedAt, bot=$bot, unionOpenid=$unionOpenid)"
}

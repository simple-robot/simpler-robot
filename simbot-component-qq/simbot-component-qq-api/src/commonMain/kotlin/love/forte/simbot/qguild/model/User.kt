/*
 *     Copyright (c) 2023-2026. ForteScarlet.
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

package love.forte.simbot.qguild.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.common.ApiModel
import love.forte.simbot.qguild.common.ApiModelConstructor
import love.forte.simbot.qguild.common.DataClassCompatibilities

/**
 * [用户对象](https://bot.q.qq.com/wiki/develop/api/openapi/user/model.html#user)
 *
 * 用户对象中所涉及的 ID 类数据，都仅在机器人场景流通，与真实的 ID 无关。请不要理解为真实的 ID
 *
 * @property id 用户 id
 * @property username 用户名
 * @property avatar 用户头像地址
 * @property isBot 是否是机器人
 * @property unionOpenid 跨应用统一用户 OpenID（需特殊申请）
 * @property unionUserAccount 跨应用统一用户账号（需特殊申请）
 */
@ApiModel
@Serializable
public class User @ApiModelConstructor constructor(
    public val id: String,
    public val username: String,
    public val avatar: String = "",
    @SerialName("bot") public val isBot: Boolean = false,
    @SerialName("union_openid") public val unionOpenid: String? = null,
    @SerialName("union_user_account") public val unionUserAccount: String? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is User) return false

        if (id != other.id) return false
        if (username != other.username) return false
        if (avatar != other.avatar) return false
        if (isBot != other.isBot) return false
        if (unionOpenid != other.unionOpenid) return false
        if (unionUserAccount != other.unionUserAccount) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + avatar.hashCode()
        result = 31 * result + isBot.hashCode()
        result = 31 * result + (unionOpenid?.hashCode() ?: 0)
        result = 31 * result + (unionUserAccount?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "User(" +
            "id='$id', " +
            "username='$username', " +
            "avatar='$avatar', " +
            "isBot=$isBot, " +
            "unionOpenid=$unionOpenid, " +
            "unionUserAccount=$unionUserAccount)"
    }

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("id"))
    public operator fun component1(): String = id

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("username"))
    public operator fun component2(): String = username

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("avatar"))
    public operator fun component3(): String = avatar

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("isBot"))
    public operator fun component4(): Boolean = isBot

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("unionOpenid"))
    public operator fun component5(): String? = unionOpenid

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("unionUserAccount"))
    public operator fun component6(): String? = unionUserAccount

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        id: String = this.id,
        username: String = this.username,
        avatar: String = this.avatar,
        isBot: Boolean = this.isBot,
        unionOpenid: String? = this.unionOpenid,
        unionUserAccount: String? = this.unionUserAccount,
    ): User = User(id, username, avatar, isBot, unionOpenid, unionUserAccount)
    //endregion
}

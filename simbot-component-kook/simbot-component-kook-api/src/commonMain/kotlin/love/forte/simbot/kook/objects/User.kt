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

package love.forte.simbot.kook.objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.kook.objects.SystemUser.SYSTEM_USER_ID
import love.forte.simbot.kook.objects.SystemUser.SYSTEM_USER_IDENTIFY_NUM
import love.forte.simbot.kook.objects.SystemUser.SYSTEM_USER_NAME
import love.forte.simbot.kook.objects.SystemUser.id


/**
 * [用户 User](https://developer.kookapp.cn/doc/objects#%E7%94%A8%E6%88%B7%20User)
 *
 * @author ForteScarlet
 */
public interface User {
    /**
     * 用户的id
     */
    public val id: String

    /**
     * 用户名称
     */
    public val username: String

    /**
     * 用户在当前服务器的昵称
     */
    public val nickname: String?


    /**
     * 用户名的认证数字，用户名正常为：`user_name#identify_num`
     */
    public val identifyNum: String
    // ser name: identify_num

    /**
     * 当前是否在线
     */
    public val isOnline: Boolean
    // ser name: online

    /**
     * 用户是否为机器人
     */
    public val isBot: Boolean
    // ser name: bot

    /**
     *用户的状态, `0` 和 `1` 代表正常，`10` 代表被封禁
     */
    public val status: Int

    /**
     * 用户的头像的url地址
     */
    public val avatar: String


    /**
     * vip用户的头像的url地址，可能为gif动图
     */
    public val vipAvatar: String?

    /**
     * 是否手机号已验证
     */
    public val isMobileVerified: Boolean
    // ser name: mobile_verified


    /**
     * 用户在当前服务器中的角色 id 组成的列表。为null则代表信息中未包含此信息。
     */
    public val roles: List<Long>?

    public companion object {
        /**
         * [User.status] 为 `10` 时所代表的 `"被封禁"`
         */
        public const val BANNED_STATUS: Int = 10
    }

}

/**
 * 如果 [User.status] 为 [`10`][User.BANNED_STATUS] 则代表封禁。
 *
 */
public inline val User.isStatusBanned: Boolean get() = status == User.BANNED_STATUS


/**
 * [User] 的基础实现。
 *
 * 除了
 *
 * - [id]
 * - [username]
 * - [avatar]
 *
 * 以外，大部分属性因为都可能缺失而存在默认值。
 * 当缺失时，
 *
 * - 可能为 `null` 的属性默认为 `null`
 * - 整型类型无特殊说明情况下默认为 `-1`
 * - 字符串类型无特殊说明情况下默认为 `""` (空字符串)
 * - 布尔类型无特殊说明情况下默认为 `false`
 *
 * @see User
 */
@Serializable
public data class SimpleUser(
    override val id: String,
    override val username: String,
    override val avatar: String,

    /**
     * 用户在当前服务器的昵称，默认为 `null`
     */
    override val nickname: String? = null,
    /**
     * 用户名的认证数字，用户名正常为：user_name#identify_num
     *
     * 没有 `identify_num` 的情况下，会**尝试**从 [username] 中切割 `#` 并解析出 identifyNum 的值，
     * 无法得到结果时使用空字符串。
     */
    @SerialName("identify_num") override val identifyNum: String =
        username.substringAfter('#', ""),
    /**
     * 当前是否在线，默认为 `false`
     */
    @SerialName("online") override val isOnline: Boolean = false,
    /**
     * 用户是否为机器人，默认为 `false`
     */
    @SerialName("bot") override val isBot: Boolean = false,
    /**
     * 用户的状态, 0 和 1 代表正常，10 代表被封禁，默认情况下视为正常状态 `0`
     */
    override val status: Int = 0,
    /**
     * vip用户的头像的url地址，可能为gif动图，默认为 `null`
     */
    @SerialName("vip_avatar") override val vipAvatar: String? = null,
    /**
     * 是否手机号已验证，默认为 `false`
     */
    @SerialName("mobile_verified") override val isMobileVerified: Boolean = false,
    /**
     * 用户在当前服务器中的角色 id 组成的列表，默认为 `null`
     */
    override val roles: List<Long>? = null
) : User


/**
 *
 * 当 [id] == `1` 的时候，用户代表为 _系统用户_ 。
 *
 * 参考 [事件结构/格式说明](https://developer.kaiheila.cn/doc/event/event-introduction) 中事件结构的 `author_id` 字段说明。
 *
 * @see User
 */
public object SystemUser : User {
    /**
     * 系统用户的默认ID值。
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public const val SYSTEM_USER_ID: String = "1"

    /**
     * 系统用户的默认用户名与昵称。
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public const val SYSTEM_USER_NAME: String = "System"

    /**
     * 系统用户的默认 `identifyNum`
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public const val SYSTEM_USER_IDENTIFY_NUM: String = "0000"

    /**
     * 系统用户ID，始终为 [SYSTEM_USER_ID]。
     */
    override val id: String = SYSTEM_USER_ID

    /**
     * 系统用户名称，始终为 [SYSTEM_USER_NAME]
     */
    override val username: String
        get() = SYSTEM_USER_NAME

    /**
     * 系统用户昵称，始终为 [SYSTEM_USER_NAME]
     */
    override val nickname: String
        get() = SYSTEM_USER_NAME

    /**
     * 系统用户的 `identifyNum`，始终为 [SYSTEM_USER_IDENTIFY_NUM]
     */
    override val identifyNum: String
        get() = SYSTEM_USER_IDENTIFY_NUM

    /**
     * 系统用户始终在线
     */
    override val isOnline: Boolean
        get() = true

    /**
     * 系统用户不算 `bot`，值为 `false`
     */
    override val isBot: Boolean
        get() = false

    /**
     * 系统用户状态，始终为 `0`
     */
    override val status: Int
        get() = 0

    /**
     * 系统用户头像，始终为空字符串 `""`
     */
    override val avatar: String
        get() = ""

    /**
     * 系统用户 `vipAvatar` ，始终为 `null`
     */
    override val vipAvatar: String?
        get() = null

    /**
     * 系统用户手机号始终为未验证，值为 `false`
     */
    override val isMobileVerified: Boolean
        get() = false

    /**
     * 系统用户持有角色列表始终为 `null`
     */
    override val roles: List<Long>?
        get() = null
}


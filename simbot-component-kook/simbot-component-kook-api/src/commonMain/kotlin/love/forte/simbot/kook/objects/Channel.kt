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
import love.forte.simbot.kook.util.BooleanToIntSerializer

/**
 * [频道 Channel](https://developer.kookapp.cn/doc/objects#%E9%A2%91%E9%81%93%20Channel)
 *
 * @see SimpleChannel
 *
 * @author ForteScarlet
 */
public interface Channel {

    /** 频道id */
    public val id: String

    /** 频道名称 */
    public val name: String

    /** 创建者id */
    public val userId: String

    /** 服务器id */
    public val guildId: String

    /** 频道简介 */
    public val topic: String

    /** 是否为分组 */
    public val isCategory: Boolean
    // is_category

    /** 上级分组的id */
    public val parentId: String

    /** 排序level */
    public val level: Int

    /** 慢速模式下限制发言的最短时间间隔, 单位为秒(s) */
    public val slowMode: Int

    /** 频道类型: 1 文字频道, 2 语音频道 */
    public val type: Int

    /** 针对角色在该频道的权限覆写规则组成的列表 */
    public val permissionOverwrites: List<PermissionOverwrite>
    // permission_overwrites

    /** 针对用户在该频道的权限覆写规则组成的列表 */
    public val permissionUsers: List<PermissionUser>
    // permission_users

    /** 权限设置是否与分组同步, 1 or 0 */
    public val permissionSync: Int
    // permission_sync

    /**
     * 	是否有密码
     */
    public val hasPassword: Boolean
    // has_password

    /**
     * [Channel] 的类型，一般出现在事件中。
     */
    @Serializable
    public enum class Type(public val value: Int) {
        /**
         * 文字频道
         */
        TEXT(1),

        /**
         * 语音频道
         */
        VOICE(2)
    }
}

/**
 * 得到 [Channel.type] 对应的 [Channel.Type] 枚举值。
 *
 * - `1` -> [Channel.Type.TEXT]
 * - `2` -> [Channel.Type.VOICE]
 * - 其他 -> 抛出 [NoSuchElementException]
 *
 * @throws NoSuchElementException 类型不是 [Channel.Type] 中的可选项
 */
public val Channel.typeValue: Channel.Type get() = typeValueOrNull ?: throw NoSuchElementException("type: $type")


/**
 * 得到 [Channel.type] 对应的 [Channel.Type] 枚举值。
 *
 * - `1` -> [Channel.Type.TEXT]
 * - `2` -> [Channel.Type.VOICE]
 * - 其他 -> `null`
 *
 */
public val Channel.typeValueOrNull: Channel.Type?
    get() =
        when (type) {
            Channel.Type.TEXT.value -> Channel.Type.TEXT
            Channel.Type.VOICE.value -> Channel.Type.VOICE
            else -> null
        }


/**
 * 针对角色在该频道的权限覆写规则，是 [Channel.permissionOverwrites] 中的数据类型
 *
 * ```json
 *  "permission_overwrites": [
 *         {
 *             "role_id": 0,
 *             "allow": 0,
 *             "deny": 0
 *         }
 *     ],
 * ```
 *
 * _定义时未在文档中寻得详细描述_
 *
 */
@Serializable
public data class PermissionOverwrite(@SerialName("role_id") val roleId: Int = 0, val allow: Int = 0, val deny: Int = 0)


/**
 * 针对用户在该频道的权限覆写规则，是 [Channel.permissionUsers] 中的数据类型
 *
 * ```json
 * "permission_users": [
 *         {
 *             //user字段参见 https://developer.kookapp.cn/doc/objects#%E7%94%A8%E6%88%B7User
 *             "user": {
 *                // ...
 *             },
 *             "allow": 0,
 *             "deny": 0
 *         }
 * ```
 *
 * _定义时未在文档中寻得详细描述_
 */
@Serializable
public data class PermissionUser(
    /**
     * 用户信息
     */
    val user: SimpleUser,
    val allow: Int = 0,
    val deny: Int = 0,
)


/**
 * [Channel] 的基础实现
 *
 */
@Serializable
public data class SimpleChannel(
    override val id: String,
    override val name: String,
    @SerialName("user_id") override val userId: String,
    @SerialName("guild_id") override val guildId: String,
    override val topic: String,
    @SerialName("is_category")
    @Serializable(with = BooleanToIntSerializer::class)
    override val isCategory: Boolean,
    @SerialName("parent_id") override val parentId: String,
    override val level: Int,
    @SerialName("slow_mode") override val slowMode: Int,
    override val type: Int,
    @SerialName("permission_overwrites") override val permissionOverwrites: List<PermissionOverwrite> = emptyList(),
    @SerialName("permission_users") override val permissionUsers: List<PermissionUser> = emptyList(),
    @SerialName("permission_sync") override val permissionSync: Int,
    @SerialName("has_password") override val hasPassword: Boolean
) : Channel


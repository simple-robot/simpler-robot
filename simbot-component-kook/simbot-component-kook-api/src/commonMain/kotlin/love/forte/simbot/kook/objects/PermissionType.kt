/*
 *     Copyright (c) 2021-2026. ForteScarlet.
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

@file:Suppress("unused")
@file:JvmName("PermissionTypes")

package love.forte.simbot.kook.objects

import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic

/**
 *
 * 权限bitValue类型枚举.
 *
 * see [权限说明](https://developer.kaiheila.cn/doc/http/guild-role#权限说明)
 *
 * @author ForteScarlet
 *
 * @property bitValue 比特位。
 * @property isAdmin 是否为一个可能被视为拥有某种管理权限的权限值。
 */
@Suppress("MemberVisibilityCanBePrivate")
public enum class PermissionType(
    @get:JvmSynthetic public val bitValue: UInt, public val isAdmin: Boolean = false
) {
    /**
     * 拥有此权限会获得完整的管理权，包括绕开所有其他权限（包括频道权限）限制，属于危险权限。
     * `管理员`
     */
    ADMIN(0u, true),
    // 管理员(0u),


    /**
     * 拥有此权限的成员可以修改服务器名称和更换区域。
     * `管理服务器`
     */
    GUILD_MANAGEMENT(1u, true),
    // 管理服务器(1u),


    /**
     * 拥有此权限的成员可以查看服务器的管理日志。
     * `查看管理日志`
     */
    VIEW_MANAGEMENT_LOG(2u, true),
    // 查看管理日志(2u),


    /**
     * 能否创建服务器邀请链接
     * `创建服务器邀请`
     */
    CREATE_GUILD_INVITATION(3u),
    // 创建服务器邀请(3u),


    /**
     * 拥有该权限可以管理服务器的邀请
     * `管理邀请`
     */
    INVITATIONS_MANAGEMENT(4u, true),
    // 管理邀请(4u),

    /**
     * 拥有此权限的成员可以创建新的频道以及编辑或删除已存在的频道。
     * `频道管理`
     */
    CHANNEL_MANAGEMENT(5u, true),
    // 频道管理(5u),

    /**
     *
     * `踢出用户`
     */
    KICK_USER(6u, true),
    // 踢出用户(6u),

    /**
     *
     * `封禁用户`
     */
    BAN_USER(7u, true),
    // 封禁用户(7u),

    /**
     *
     * `管理自定义表情`
     */
    CUSTOM_EMOTICONS_MANAGEMENT(8u, true),
    // 管理自定义表情(8u),

    /**
     * 拥有此权限的用户可以更改他们的昵称。
     * `修改服务器昵称`
     */
    EDIT_GUILD_NICKNAME(9u, true),
    // 修改服务器昵称(9u),

    /**
     * 拥有此权限成员可以创建新的角色和编辑删除低于该角色的身份。
     * `管理角色权限`
     */
    ROLE_PERMISSIONS_MANAGEMENT(10u, true),
    // 管理角色权限(10u),

    /**
     * 语音频道
     * `查看文字`
     */
    VIEW_TEXT(11u),
    // 查看文字(11u),

    /**
     *
     * `发布消息`
     */
    SEND_MESSAGE(12u),
    // 发布消息(12u),

    /**
     * 拥有此权限的成员可以删除其他成员发出的消息和置顶消息。
     * `管理消息`
     */
    MESSAGE_MANAGEMENT(13u, true),
    // 管理消息(13u),

    /**
     *
     * `上传文件`
     */
    UPLOAD_FILE(14u),
    // 上传文件(14u),

    /**
     *
     * `语音链接`
     */
    VOICE_LINK(15u),
    // 语音链接(15u),

    /**
     * 拥有此权限的成员可以把其他成员移动和踢出频道；但此类移动仅限于在该成员和被移动成员均有权限的频道之间进行。
     * `语音管理`
     */
    VOICE_MANAGEMENT(16u, true),
    // 语音管理(16u),

    /**
     * 全体成员	拥有此权限的成员可使用@全体成员以提及该频道中所有成员。
     * `提及`
     */
    AT(17u),
    // 提及(17u),

    /**
     * 拥有此权限的成员可以对消息添加新的反应。
     * `添加反应`
     */
    ADD_REACTION(18u),
    // 添加反应(18u),

    /**
     * 拥有此权限的成员可以跟随使用已经添加的反应。
     * `跟随添加反应`
     */
    FOLLOW_THE_ADDITION_REACTION(19u),
    // 跟随添加反应(19u),

    /**
     * 拥有此限制的成员无法主动连接语音频道，只能在被动邀请或被人移动时，才可以进入语音频道。
     * `被动连接语音频道`
     */
    PASSIVELY_CONNECT_TO_THE_VOICE_CHANNEL(20u),
    // 被动连接语音频道(20u),

    /**
     * 拥有此限制的成员加入语音频道后，只能使用按键说话。
     * `仅使用按键说话`
     */
    TALK_USING_ONLY_KEYSTROKES(21u),
    // 仅使用按键说话(21u),

    /**
     * 没有此权限的成员，必须在频道内使用按键说话。
     * `使用自由麦`
     */
    TALK_FREELY(22u),
    // 使用自由麦(22u),

    /**
     *
     * `说话`
     */
    TALK(23u),
    // 说话(23u),

    /**
     *
     * `服务器静音`
     */
    GUILD_MUTE(24u),
    // 服务器静音(24u),

    /**
     *
     * `服务器闭麦`
     */
    GUILD_CLOSED_MIC(25u),
    // 服务器闭麦(25u),

    /**
     * 拥有此权限的用户可以更改他人的昵称
     * `修改他人昵称`
     */
    EDIT_ANOTHER_PERSON_NICKNAME(26u, true),
    // 修改他人昵称(26u),

    /**
     * 拥有此权限的成员可在语音频道中播放音乐伴奏
     * `播放伴奏`
     */
    PLAY_ACCOMPANIMENT(27u),
    // 播放伴奏(27u),

    ;

    /**
     * 比特位.
     */
    public val bit: Int get() = bitValue.toInt()

    /**
     * 权限的实际值
     */
    @get:JvmSynthetic
    public val value: UInt get() = 1u shl bit

    /**
     * 权限的实际值，[Int] 类型。
     */
    public val permValue: Int get() = value.toInt()

    public companion object {

        /**
         * 将两个 [PermissionType] 的权限值合并
         */
        @Suppress("FunctionName")
        @JvmStatic
        @JvmName("plus")
        public fun _plus(left: PermissionType, right: PermissionType): Int = (left + right).toInt()

        /**
         * 将多个 [PermissionType] 的权限值合并到一起。
         */
        @Suppress("FunctionName")
        @JvmStatic
        @JvmName("combine")
        public fun _combine(vararg perm: PermissionType): Int = perm.combine().toInt()


    }
}


/**
 * 合并两个权限值
 */
public operator fun PermissionType.plus(other: PermissionType): UInt = value or other.value

/**
 * 合并多个权限值
 */
public fun Array<out PermissionType>.combine(): UInt {
    var combine = 0u
    for (p in this) {
        combine = combine or p.value
    }
    return combine
}



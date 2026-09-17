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

package love.forte.simbot.qguild.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 互动事件的事件体。
 *
 * @property chatType `0` 频道场景, `1` 群聊场景, `2` 单聊场景
 * @property data 互动事件的数据
 * @property groupMemberOpenid 按钮触发用户的群成员 openid, 仅群聊场景提供
 * @property groupOpenid 群 openid, 仅群聊场景提供
 * @property id 互动事件 ID, 用于回应互动事件
 * @property scene 事件发生的场景: `c2c`, `group`, `guild`
 * @property timestamp 触发时间 RFC 3339 格式
 * @property type 互动类型。消息按钮: `11`, 自定义菜单: `12`
 * @property guildId 频道 openid, 仅频道场景提供
 * @property channelId 文字子频道 openid, 仅频道场景提供
 * @property userOpenid 单聊按钮触发用户 openid, 仅单聊场景提供
 * @property version 默认 `1`
 *
 * @since 4.4.0
 */
@Serializable
public data class InteractionCreateEventData(
    @SerialName("chat_type")
    val chatType: Int,
    val data: InteractionCreateData,
    @SerialName("group_member_openid")
    val groupMemberOpenid: String? = null,
    @SerialName("group_openid")
    val groupOpenid: String? = null,
    val id: String,
    val scene: String? = null,
    val timestamp: String,
    val type: Int,
    @SerialName("guild_id")
    val guildId: String? = null,
    @SerialName("channel_id")
    val channelId: String? = null,
    @SerialName("user_openid")
    val userOpenid: String? = null,
    val version: Int = 1,
) {
    public companion object {
        /**
         * 表示*频道场景*的 [InteractionCreateEventData.chatType]。
         */
        public const val CHAT_TYPE_CHANNEL: Int = 0

        /**
         * 表示*群聊场景*的 [InteractionCreateEventData.chatType]。
         */
        public const val CHAT_TYPE_GROUP: Int = 1

        /**
         * 表示*单聊场景*的 [InteractionCreateEventData.chatType]。
         */
        public const val CHAT_TYPE_PRIVATE: Int = 2

        /**
         * 表示*消息按钮*的 [InteractionCreateEventData.type]。
         */
        public const val TYPE_BUTTON: Int = 11

        /**
         * 表示*自定义菜单*的 [InteractionCreateEventData.type]。
         */
        public const val TYPE_MENU: Int = 12

        /**
         * 表示*C2C 单聊场景*的 [InteractionCreateEventData.scene]。
         */
        public const val SCENE_C2C: String = "c2c"

        /**
         * 表示*群聊场景*的 [InteractionCreateEventData.scene]。
         */
        public const val SCENE_GROUP: String = "group"

        /**
         * 表示*频道场景*的 [InteractionCreateEventData.scene]。
         */
        public const val SCENE_GUILD: String = "guild"
    }
}

/**
 * 互动事件的数据。
 *
 * @property type 互动类型。消息按钮: `11`, 自定义菜单: `12`
 * @property resolved 互动事件解析后的数据
 *
 * @since 4.4.0
 */
@Serializable
public data class InteractionCreateData(
    val type: Int,
    val resolved: InteractionCreateResolvedData,
)

/**
 * 互动事件解析后的数据。
 *
 * @property buttonData 操作按钮的 `data` 字段值
 * @property buttonId 操作按钮的 `id` 字段值
 * @property userId 操作的用户 userid, 仅频道场景提供
 * @property featureId 操作按钮的 id 字段值, 仅自定义菜单提供
 * @property messageId 操作的消息 id, 目前仅频道场景提供
 *
 * @since 4.4.0
 */
@Serializable
public data class InteractionCreateResolvedData(
    @SerialName("button_data")
    val buttonData: String? = null,
    @SerialName("button_id")
    val buttonId: String? = null,
    @SerialName("user_id")
    val userId: String? = null,
    @SerialName("feature_id")
    val featureId: String? = null,
    @SerialName("message_id")
    val messageId: String? = null,
)

/**
 * 互动事件创建时。
 *
 * @since 4.4.0
 */
@Serializable
@SerialName(EventIntents.Interaction.INTERACTION_CREATE_TYPE)
@DispatchTypeName(EventIntents.Interaction.INTERACTION_CREATE_TYPE)
public data class InteractionCreate(
    override val id: String,
    override val s: Long = DEFAULT_SEQ,
    @SerialName("d")
    override val data: InteractionCreateEventData,
) : Signal.Dispatch()

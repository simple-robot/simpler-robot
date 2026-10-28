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

@file:OptIn(ExperimentalStdlibApi::class)

package love.forte.simbot.qguild.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.common.DataClassCompatibilities
import love.forte.simbot.qguild.common.EventModelConstructor
import kotlin.jvm.JvmExposeBoxed
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmStatic

/**
 * 互动事件的聊天场景。
 *
 * 0=频道, 1=群聊, 2=单聊
 *
 * @since 5.0
 * @see InteractionCreateEventData
 */
@JvmInline
@JvmExposeBoxed
@Serializable
public value class InteractionChatType private constructor(public val value: Int) {
    @JvmExposeBoxed
    public companion object {
        /**
         * 频道值常量
         */
        public const val CHANNEL_VALUE: Int = 0

        /**
         * 群聊值常量
         */
        public const val GROUP_VALUE: Int = 1

        /**
         * 单聊值常量
         */
        public const val PRIVATE_VALUE: Int = 2

        /**
         * 频道
         */
        @JvmStatic
        public val Channel: InteractionChatType = InteractionChatType(CHANNEL_VALUE)

        /**
         * 群聊
         */
        @JvmStatic
        public val Group: InteractionChatType = InteractionChatType(GROUP_VALUE)

        /**
         * 单聊
         */
        @JvmStatic
        public val Private: InteractionChatType = InteractionChatType(PRIVATE_VALUE)

        /**
         * 构建任意数值的 [InteractionChatType]
         */
        @JvmStatic
        public fun of(value: Int): InteractionChatType = InteractionChatType(value)
    }
}

/**
 * 互动类型。
 *
 * - 11=消息按钮回调（INLINE_KEYBOARD）：用户点击消息中的内联键盘按钮
 * - 12=单聊快捷菜单回调（CALLBACK_COMMAND）：用户点击单聊场景下的自定义菜单
 * - 13=消息反馈（MESSAGE_FEEDBACK）：用户对智能体消息进行点赞/点踩反馈
 * - 14=清空会话（CLEAR_SESSION）：用户清空智能体会话历史
 * - 15=进出故事集（IN_OUT_STORY）：用户进入或退出故事集
 * - 16=切换模型（SWITCH_MODEL）：用户切换智能体模型
 * - 18=用户授权（USER_AUTHORIZE）：用户授权事件
 * - 19=群授权（GROUP_AUTHORIZE）：群授权事件
 * - 20=群授权状态变更（GROUP_AUTHORIZE_STATUS）
 *
 * @since 5.0
 * @see InteractionCreateEventData
 */
@JvmInline
@JvmExposeBoxed
@Serializable
public value class InteractionType private constructor(public val value: Int) {
    @JvmExposeBoxed
    public companion object {
        /**
         * 消息按钮回调（INLINE_KEYBOARD）值常量
         */
        public const val INLINE_KEYBOARD_VALUE: Int = 11

        /**
         * 单聊快捷菜单回调（CALLBACK_COMMAND）值常量
         */
        public const val CALLBACK_COMMAND_VALUE: Int = 12

        /**
         * 消息反馈（MESSAGE_FEEDBACK）值常量
         */
        public const val MESSAGE_FEEDBACK_VALUE: Int = 13

        /**
         * 清空会话（CLEAR_SESSION）值常量
         */
        public const val CLEAR_SESSION_VALUE: Int = 14

        /**
         * 进出故事集（IN_OUT_STORY）值常量
         */
        public const val IN_OUT_STORY_VALUE: Int = 15

        /**
         * 切换模型（SWITCH_MODEL）值常量
         */
        public const val SWITCH_MODEL_VALUE: Int = 16

        /**
         * 用户授权（USER_AUTHORIZE）值常量
         */
        public const val USER_AUTHORIZE_VALUE: Int = 18

        /**
         * 群授权（GROUP_AUTHORIZE）值常量
         */
        public const val GROUP_AUTHORIZE_VALUE: Int = 19

        /**
         * 群授权状态变更（GROUP_AUTHORIZE_STATUS）值常量
         */
        public const val GROUP_AUTHORIZE_STATUS_VALUE: Int = 20

        /**
         * 消息按钮回调（INLINE_KEYBOARD）：用户点击消息中的内联键盘按钮
         */
        @JvmStatic
        public val InlineKeyboard: InteractionType = InteractionType(INLINE_KEYBOARD_VALUE)

        /**
         * 单聊快捷菜单回调（CALLBACK_COMMAND）：用户点击单聊场景下的自定义菜单
         */
        @JvmStatic
        public val CallbackCommand: InteractionType = InteractionType(CALLBACK_COMMAND_VALUE)

        /**
         * 消息反馈（MESSAGE_FEEDBACK）：用户对智能体消息进行点赞/点踩反馈
         */
        @JvmStatic
        public val MessageFeedback: InteractionType = InteractionType(MESSAGE_FEEDBACK_VALUE)

        /**
         * 清空会话（CLEAR_SESSION）：用户清空智能体会话历史
         */
        @JvmStatic
        public val ClearSession: InteractionType = InteractionType(CLEAR_SESSION_VALUE)

        /**
         * 进出故事集（IN_OUT_STORY）：用户进入或退出故事集
         */
        @JvmStatic
        public val InOutStory: InteractionType = InteractionType(IN_OUT_STORY_VALUE)

        /**
         * 切换模型（SWITCH_MODEL）：用户切换智能体模型
         */
        @JvmStatic
        public val SwitchModel: InteractionType = InteractionType(SWITCH_MODEL_VALUE)

        /**
         * 用户授权（USER_AUTHORIZE）：用户授权事件
         */
        @JvmStatic
        public val UserAuthorize: InteractionType = InteractionType(USER_AUTHORIZE_VALUE)

        /**
         * 群授权（GROUP_AUTHORIZE）：群授权事件
         */
        @JvmStatic
        public val GroupAuthorize: InteractionType = InteractionType(GROUP_AUTHORIZE_VALUE)

        /**
         * 群授权状态变更（GROUP_AUTHORIZE_STATUS）
         */
        @JvmStatic
        public val GroupAuthorizeStatus: InteractionType = InteractionType(GROUP_AUTHORIZE_STATUS_VALUE)

        /**
         * 构建任意数值的 [InteractionType]
         */
        @JvmStatic
        public fun of(value: Int): InteractionType = InteractionType(value)
    }
}


/**
 * 互动事件发生场景。
 *
 * c2c=单聊, group=群聊, guild=频道
 *
 * @since 5.0
 * @see InteractionCreateEventData
 */
@JvmInline
@JvmExposeBoxed
@Serializable
public value class InteractionScene private constructor(public val value: String) {
    @JvmExposeBoxed
    public companion object {
        /**
         * 单聊值常量
         */
        public const val C2C_VALUE: String = "c2c"

        /**
         * 群聊值常量
         */
        public const val GROUP_VALUE: String = "group"

        /**
         * 频道值常量
         */
        public const val GUILD_VALUE: String = "guild"

        /**
         * 单聊
         */
        @JvmStatic
        public val C2C: InteractionScene = InteractionScene(C2C_VALUE)

        /**
         * 群聊
         */
        @JvmStatic
        public val Group: InteractionScene = InteractionScene(GROUP_VALUE)

        /**
         * 频道
         */
        @JvmStatic
        public val Guild: InteractionScene = InteractionScene(GUILD_VALUE)

        /**
         * 构建任意字符串的 [InteractionScene]
         */
        @JvmStatic
        public fun of(value: String): InteractionScene = InteractionScene(value)
    }
}


/**
 * 互动事件的事件体。
 *
 * 参考：https://bot.q.qq.com/wiki/develop/api-v2/autogen/event/interaction_create.html
 *
 * @property id 互动事件 ID, 用于回应互动事件
 * @property interactionType 互动类型。消息按钮: `11`, 自定义菜单: `12`
 * @property interactionScene 事件发生的场景: `c2c`, `group`, `guild`
 * @property interactionChatType `0` 频道场景, `1` 群聊场景, `2` 单聊场景
 * @property timestamp 触发时间 RFC 3339 格式
 * @property guildId 频道 openid, 仅频道场景提供
 * @property channelId 文字子频道 openid, 仅频道场景提供
 * @property userOpenid 单聊按钮触发用户 openid, 仅单聊场景提供
 * @property groupOpenid 群 openid, 仅群聊场景提供
 * @property groupMemberOpenid 按钮触发用户的群成员 openid, 仅群聊场景提供
 * @property data 互动事件的数据
 * @property version 默认 `1`
 *
 * @since 4.4.0
 */
@Serializable
public class InteractionCreateEventData internal constructor(
    public val id: String,
    @SerialName("type")
    @get:JvmExposeBoxed
    public val interactionType: InteractionType,
    @get:JvmExposeBoxed
    @SerialName("scene")
    public val interactionScene: InteractionScene? = null,
    @SerialName("chat_type")
    @get:JvmExposeBoxed
    public val interactionChatType: InteractionChatType,
    public val timestamp: String,
    @SerialName("guild_id")
    public val guildId: String? = null,
    @SerialName("channel_id")
    public val channelId: String? = null,
    @SerialName("user_openid")
    public val userOpenid: String? = null,
    @SerialName("group_openid")
    public val groupOpenid: String? = null,
    @SerialName("group_member_openid")
    public val groupMemberOpenid: String? = null,
    public val data: InteractionCreateData,
    public val version: Int = 1,
    /**
     * 机器人 AppID
     * @since 5.0
     */
    public val applicationId: String? = null,
) {
    @Deprecated(
        DataClassCompatibilities.DEPRECATED_CONSTRUCTOR_MESSAGE,
        level = DeprecationLevel.ERROR
    )
    @EventModelConstructor
    public constructor(
        chatType: Int,
        data: InteractionCreateData,
        groupMemberOpenid: String? = null,
        groupOpenid: String? = null,
        id: String,
        scene: String? = null,
        timestamp: String,
        type: Int,
        guildId: String? = null,
        channelId: String? = null,
        userOpenid: String? = null,
        version: Int = 1,
    ) : this(
        interactionChatType = InteractionChatType.of(chatType),
        data = data,
        groupMemberOpenid = groupMemberOpenid,
        groupOpenid = groupOpenid,
        id = id,
        interactionScene = scene?.let { InteractionScene.of(it) },
        timestamp = timestamp,
        interactionType = InteractionType.of(type),
        guildId = guildId,
        channelId = channelId,
        userOpenid = userOpenid,
        version = version,
    )

    @Deprecated("Use interactionScene instead.", ReplaceWith("interactionScene?.value"))
    public val scene: String?
        get() = interactionScene?.value

    @Deprecated("Use interactionType instead.", ReplaceWith("interactionType.value"))
    public val type: Int
        get() = interactionType.value

    @Deprecated("Use interactionChatType instead.", ReplaceWith("interactionChatType.value"))
    public val chatType: Int
        get() = interactionChatType.value

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is InteractionCreateEventData) return false

        if (interactionChatType != other.interactionChatType) return false
        if (data != other.data) return false
        if (groupMemberOpenid != other.groupMemberOpenid) return false
        if (groupOpenid != other.groupOpenid) return false
        if (id != other.id) return false
        if (interactionScene != other.interactionScene) return false
        if (timestamp != other.timestamp) return false
        if (interactionType != other.interactionType) return false
        if (guildId != other.guildId) return false
        if (channelId != other.channelId) return false
        if (userOpenid != other.userOpenid) return false
        if (version != other.version) return false

        return true
    }

    override fun hashCode(): Int {
        var result = interactionChatType.hashCode()
        result = 31 * result + data.hashCode()
        result = 31 * result + groupMemberOpenid.hashCode()
        result = 31 * result + groupOpenid.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + interactionScene.hashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + interactionType.hashCode()
        result = 31 * result + guildId.hashCode()
        result = 31 * result + channelId.hashCode()
        result = 31 * result + userOpenid.hashCode()
        result = 31 * result + version
        return result
    }

    override fun toString(): String {
        return "InteractionCreateEventData(" +
            "chatType=$interactionChatType, " +
            "data=$data, " +
            "groupMemberOpenid=$groupMemberOpenid, " +
            "groupOpenid=$groupOpenid, " +
            "id='$id', " +
            "scene=$interactionScene, " +
            "timestamp='$timestamp', " +
            "type=$interactionType, " +
            "guildId=$guildId, " +
            "channelId=$channelId, " +
            "userOpenid=$userOpenid, " +
            "version=$version)"
    }

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("interactionChatType.value"))
    public operator fun component1(): Int = interactionChatType.value

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("data"))
    public operator fun component2(): InteractionCreateData = data

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("groupMemberOpenid"))
    public operator fun component3(): String? = groupMemberOpenid

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("groupOpenid"))
    public operator fun component4(): String? = groupOpenid

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("id"))
    public operator fun component5(): String = id

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("interactionScene?.value"))
    public operator fun component6(): String? = interactionScene?.value

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("timestamp"))
    public operator fun component7(): String = timestamp

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("interactionType.value"))
    public operator fun component8(): Int = interactionType.value

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("guildId"))
    public operator fun component9(): String? = guildId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("channelId"))
    public operator fun component10(): String? = channelId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("userOpenid"))
    public operator fun component11(): String? = userOpenid

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("version"))
    public operator fun component12(): Int = version

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        chatType: Int = this.interactionChatType.value,
        data: InteractionCreateData = this.data,
        groupMemberOpenid: String? = this.groupMemberOpenid,
        groupOpenid: String? = this.groupOpenid,
        id: String = this.id,
        scene: String? = this.interactionScene?.value,
        timestamp: String = this.timestamp,
        type: Int = this.interactionType.value,
        guildId: String? = this.guildId,
        channelId: String? = this.channelId,
        userOpenid: String? = this.userOpenid,
        version: Int = this.version,
    ): InteractionCreateEventData = InteractionCreateEventData(
        interactionChatType = InteractionChatType.of(chatType),
        data = data,
        groupMemberOpenid = groupMemberOpenid,
        groupOpenid = groupOpenid,
        id = id,
        interactionScene = scene?.let { InteractionScene.of(it) },
        timestamp = timestamp,
        interactionType = InteractionType.of(type),
        guildId = guildId,
        channelId = channelId,
        userOpenid = userOpenid,
        version = version,
        applicationId = applicationId,
    )
    //endregion

    public companion object {
        /**
         * 表示*频道场景*的 [InteractionCreateEventData.chatType]。
         */
        @Deprecated(
            "Use InteractionChatType.CHANNEL_VALUE instead.",
            ReplaceWith(
                "InteractionChatType.CHANNEL_VALUE",
                "love.forte.simbot.qguild.event.InteractionChatType"
            ),
        )
        public const val CHAT_TYPE_CHANNEL: Int = InteractionChatType.CHANNEL_VALUE

        /**
         * 表示*群聊场景*的 [InteractionCreateEventData.chatType]。
         */
        @Deprecated(
            "Use InteractionChatType.GROUP_VALUE instead.",
            ReplaceWith(
                "InteractionChatType.GROUP_VALUE",
                "love.forte.simbot.qguild.event.InteractionChatType"
            ),
        )
        public const val CHAT_TYPE_GROUP: Int = InteractionChatType.GROUP_VALUE

        /**
         * 表示*单聊场景*的 [InteractionCreateEventData.chatType]。
         */
        @Deprecated(
            "Use InteractionChatType.PRIVATE_VALUE instead.",
            ReplaceWith(
                "InteractionChatType.PRIVATE_VALUE",
                "love.forte.simbot.qguild.event.InteractionChatType"
            ),
        )
        public const val CHAT_TYPE_PRIVATE: Int = InteractionChatType.PRIVATE_VALUE

        /**
         * 表示*消息按钮*的 [InteractionCreateEventData.type]。
         *
         * 原常量值`11`，现文档描述为：`11` - 消息按钮回调（INLINE_KEYBOARD）：用户点击消息中的内联键盘按钮
         */
        @Deprecated(
            "Use InteractionType.INLINE_KEYBOARD_VALUE instead.",
            ReplaceWith(
                "InteractionType.INLINE_KEYBOARD_VALUE",
                "love.forte.simbot.qguild.event.InteractionType"
            ),
        )
        public const val TYPE_BUTTON: Int = InteractionType.INLINE_KEYBOARD_VALUE

        /**
         * 表示*自定义菜单*的 [InteractionCreateEventData.type]。
         *
         * 原常量值`12`，现文档描述为：`12` - 单聊快捷菜单回调（CALLBACK_COMMAND）：用户点击单聊场景下的自定义菜单
         */
        @Deprecated(
            "Use InteractionType.CALLBACK_COMMAND_VALUE instead.",
            ReplaceWith(
                "InteractionType.CALLBACK_COMMAND_VALUE",
                "love.forte.simbot.qguild.event.InteractionType"
            ),
        )
        public const val TYPE_MENU: Int = InteractionType.CALLBACK_COMMAND_VALUE

        /**
         * 表示*C2C 单聊场景*的 [InteractionCreateEventData.interactionScene]。
         */
        @Deprecated(
            "Use InteractionScene.C2C_VALUE instead.",
            ReplaceWith(
                "InteractionScene.C2C_VALUE",
                "love.forte.simbot.qguild.event.InteractionScene"
            ),
        )
        public const val SCENE_C2C: String = InteractionScene.C2C_VALUE

        /**
         * 表示*群聊场景*的 [InteractionCreateEventData.interactionScene]。
         */
        @Deprecated(
            "Use InteractionScene.GROUP_VALUE instead.",
            ReplaceWith(
                "InteractionScene.GROUP_VALUE",
                "love.forte.simbot.qguild.event.InteractionScene"
            ),
        )
        public const val SCENE_GROUP: String = InteractionScene.GROUP_VALUE

        /**
         * 表示*频道场景*的 [InteractionCreateEventData.interactionScene]。
         */
        @Deprecated(
            "Use InteractionScene.GUILD_VALUE instead.",
            ReplaceWith(
                "InteractionScene.GUILD_VALUE",
                "love.forte.simbot.qguild.event.InteractionScene"
            ),
        )
        public const val SCENE_GUILD: String = InteractionScene.GUILD_VALUE
    }
}

/**
 * 互动事件的数据。
 *
 * @property interactionType 互动类型。消息按钮: `11`, 自定义菜单: `12`
 * @property resolved 互动事件解析后的数据
 *
 * @since 4.4.0
 */
@Serializable
public class InteractionCreateData internal constructor(
    @SerialName("type")
    @get:JvmExposeBoxed
    public val interactionType: InteractionType,
    public val resolved: InteractionCreateResolvedData,
) {

    @Deprecated("Use interactionType instead.", ReplaceWith("interactionType.value"))
    public val type: Int
        get() = interactionType.value

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is InteractionCreateData) return false

        if (interactionType != other.interactionType) return false
        if (resolved != other.resolved) return false

        return true
    }

    override fun hashCode(): Int {
        var result = interactionType.hashCode()
        result = 31 * result + resolved.hashCode()
        return result
    }

    override fun toString(): String {
        return "InteractionCreateData(type=$interactionType, resolved=$resolved)"
    }

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("interactionType.value"))
    public operator fun component1(): Int = interactionType.value

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("resolved"))
    public operator fun component2(): InteractionCreateResolvedData = resolved

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        type: Int = this.interactionType.value,
        resolved: InteractionCreateResolvedData = this.resolved,
    ): InteractionCreateData = InteractionCreateData(
        interactionType = InteractionType.of(type),
        resolved = resolved
    )
    //endregion
}


/**
 * 互动事件解析数据中的反馈选项。
 *
 * LIKE=点赞, UNLIKE=点踩
 *
 * @since 5.0
 * @see InteractionCreateResolvedData
 */
@JvmInline
@JvmExposeBoxed
@Serializable
public value class InteractionResolvedFeedbackOpt private constructor(public val value: String) {
    @JvmExposeBoxed
    public companion object {
        /**
         * 点赞值常量
         */
        public const val LIKE_VALUE: String = "LIKE"

        /**
         * 点踩值常量
         */
        public const val UNLIKE_VALUE: String = "UNLIKE"

        /**
         * 点赞
         */
        @JvmStatic
        public val Like: InteractionResolvedFeedbackOpt = InteractionResolvedFeedbackOpt(LIKE_VALUE)

        /**
         * 点踩
         */
        @JvmStatic
        public val Unlike: InteractionResolvedFeedbackOpt = InteractionResolvedFeedbackOpt(UNLIKE_VALUE)

        /**
         * 构建任意字符串的 [InteractionResolvedFeedbackOpt]
         */
        @JvmStatic
        public fun of(value: String): InteractionResolvedFeedbackOpt = InteractionResolvedFeedbackOpt(value)
    }
}

/**
 * 互动事件解析数据中的操作类型。
 *
 * type=15 故事集：ENTER_STORY=进入, QUIT_STORY=退出；
 * type=16 切换模型：对应操作动作。
 *
 * @since 5.0
 * @see InteractionCreateResolvedData
 */
@JvmInline
@JvmExposeBoxed
@Serializable
public value class InteractionResolvedAction private constructor(public val value: String) {
    @JvmExposeBoxed
    public companion object {
        /**
         * 进入故事集值常量
         */
        public const val ENTER_STORY_VALUE: String = "ENTER_STORY"

        /**
         * 退出故事集值常量
         */
        public const val QUIT_STORY_VALUE: String = "QUIT_STORY"

        /**
         * 进入故事集
         */
        @JvmStatic
        public val EnterStory: InteractionResolvedAction = InteractionResolvedAction(ENTER_STORY_VALUE)

        /**
         * 退出故事集
         */
        @JvmStatic
        public val QuitStory: InteractionResolvedAction = InteractionResolvedAction(QUIT_STORY_VALUE)

        /**
         * 构建任意字符串的 [InteractionResolvedAction]
         */
        @JvmStatic
        public fun of(value: String): InteractionResolvedAction = InteractionResolvedAction(value)
    }
}

/**
 * 授权数据中的授权操作场景。
 *
 * setting=资料页设置, dialog=弹窗授权
 *
 * @since 5.0
 */
@JvmInline
@JvmExposeBoxed
@Serializable
public value class AuthorizeDataOptScene private constructor(public val value: String) {
    @JvmExposeBoxed
    public companion object {
        /**
         * 资料页设置值常量
         */
        public const val SETTING_VALUE: String = "setting"

        /**
         * 弹窗授权值常量
         */
        public const val DIALOG_VALUE: String = "dialog"

        /**
         * 资料页设置
         */
        @JvmStatic
        public val Setting: AuthorizeDataOptScene = AuthorizeDataOptScene(SETTING_VALUE)

        /**
         * 弹窗授权
         */
        @JvmStatic
        public val Dialog: AuthorizeDataOptScene = AuthorizeDataOptScene(DIALOG_VALUE)

        /**
         * 构建任意字符串的 [AuthorizeDataOptScene]
         */
        @JvmStatic
        public fun of(value: String): AuthorizeDataOptScene = AuthorizeDataOptScene(value)
    }
}

/**
 * 授权数据中的授权范围。
 *
 * c2c_push=C2C 主动消息推送, group_push=群主动消息推送
 *
 * @since 5.0
 */
@JvmInline
@JvmExposeBoxed
@Serializable
public value class AuthorizeDataScope private constructor(public val value: String) {
    @JvmExposeBoxed
    public companion object {
        /**
         * C2C 主动消息推送值常量
         */
        public const val C2C_PUSH_VALUE: String = "c2c_push"

        /**
         * 群主动消息推送值常量
         */
        public const val GROUP_PUSH_VALUE: String = "group_push"

        /**
         * C2C 主动消息推送
         */
        @JvmStatic
        public val C2CPush: AuthorizeDataScope = AuthorizeDataScope(C2C_PUSH_VALUE)

        /**
         * 群主动消息推送
         */
        @JvmStatic
        public val GroupPush: AuthorizeDataScope = AuthorizeDataScope(GROUP_PUSH_VALUE)

        /**
         * 构建任意字符串的 [AuthorizeDataScope]
         */
        @JvmStatic
        public fun of(value: String): AuthorizeDataScope = AuthorizeDataScope(value)
    }
}


/**
 * 互动事件解析后的数据。
 *
 * @property buttonData 操作按钮的 `data` 字段值
 * @property buttonId 操作按钮的 `id` 字段值
 * @property userId 操作的用户 userid, 仅频道场景提供
 * @property featureId 操作按钮的 id 字段值, 仅自定义菜单提供
 * @property messageId 操作的消息 id, 目前仅频道场景提供
 * @property feedbackOpt 反馈选项（仅 type=13 消息反馈）。LIKE=点赞, UNLIKE=点踩
 * @property checked 反馈选项是否选中（仅 type=13 消息反馈）
 * @property action 操作类型 （type=15 故事集：ENTER_STORY=进入, QUIT_STORY=退出；type=16 切换模型：对应操作动作）
 * @property messageScene 消息场景信息（仅 type=13 消息反馈）
 * @property authorizeData 授权数据（仅 type=18/19 用户/群授权事件）
 *
 * @since 4.4.0
 */
@Serializable
public class InteractionCreateResolvedData internal constructor(
    @SerialName("button_data")
    public val buttonData: String? = null,
    @SerialName("button_id")
    public val buttonId: String? = null,
    @SerialName("user_id")
    public val userId: String? = null,
    @SerialName("feature_id")
    public val featureId: String? = null,
    @SerialName("message_id")
    public val messageId: String? = null,
    @SerialName("feedback_opt")
    @get:JvmExposeBoxed
    public val feedbackOpt: InteractionResolvedFeedbackOpt? = null,
    public val checked: Int? = null,
    @get:JvmExposeBoxed
    public val action: InteractionResolvedAction? = null,
    @SerialName("message_scene")
    public val messageScene: InteractionMessageScene? = null,
    @SerialName("authorize_data")
    public val authorizeData: AuthorizeData? = null,
) {
    @Deprecated(
        DataClassCompatibilities.DEPRECATED_CONSTRUCTOR_MESSAGE,
        level = DeprecationLevel.ERROR
    )
    @EventModelConstructor
    public constructor(
        buttonData: String? = null,
        buttonId: String? = null,
        userId: String? = null,
        featureId: String? = null,
        messageId: String? = null,
    ) : this(
        buttonData = buttonData,
        buttonId = buttonId,
        userId = userId,
        featureId = featureId,
        messageId = messageId,
        feedbackOpt = null,
        checked = null,
        action = null,
    )

    @Deprecated(
        DataClassCompatibilities.DEPRECATED_CONSTRUCTOR_MESSAGE,
        level = DeprecationLevel.ERROR
    )
    @EventModelConstructor
    public constructor() : this(
        buttonData = null,
        buttonId = null,
        userId = null,
        featureId = null,
        messageId = null,
        feedbackOpt = null,
        checked = null,
        action = null,
    )

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("buttonData"))
    public operator fun component1(): String? = buttonData

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("buttonId"))
    public operator fun component2(): String? = buttonId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("userId"))
    public operator fun component3(): String? = userId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("featureId"))
    public operator fun component4(): String? = featureId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("messageId"))
    public operator fun component5(): String? = messageId

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        buttonData: String? = this.buttonData,
        buttonId: String? = this.buttonId,
        userId: String? = this.userId,
        featureId: String? = this.featureId,
        messageId: String? = this.messageId,
    ): InteractionCreateResolvedData = InteractionCreateResolvedData(
        buttonData = buttonData,
        buttonId = buttonId,
        userId = userId,
        featureId = featureId,
        messageId = messageId,
        feedbackOpt = feedbackOpt,
        checked = checked,
        action = action,
        messageScene = messageScene,
        authorizeData = authorizeData
    )
    //endregion

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is InteractionCreateResolvedData) return false

        if (checked != other.checked) return false
        if (buttonData != other.buttonData) return false
        if (buttonId != other.buttonId) return false
        if (userId != other.userId) return false
        if (featureId != other.featureId) return false
        if (messageId != other.messageId) return false
        if (feedbackOpt != other.feedbackOpt) return false
        if (action != other.action) return false
        if (messageScene != other.messageScene) return false
        if (authorizeData != other.authorizeData) return false

        return true
    }

    override fun hashCode(): Int {
        var result = checked.hashCode()
        result = 31 * result + buttonData.hashCode()
        result = 31 * result + buttonId.hashCode()
        result = 31 * result + userId.hashCode()
        result = 31 * result + featureId.hashCode()
        result = 31 * result + messageId.hashCode()
        result = 31 * result + feedbackOpt.hashCode()
        result = 31 * result + action.hashCode()
        result = 31 * result + messageScene.hashCode()
        result = 31 * result + authorizeData.hashCode()
        return result
    }

    override fun toString(): String {
        return "InteractionCreateResolvedData(buttonData=$buttonData, " +
            "buttonId=$buttonId, " +
            "userId=$userId, " +
            "featureId=$featureId, " +
            "messageId=$messageId, " +
            "feedbackOpt=$feedbackOpt, " +
            "checked=$checked, " +
            "action=$action, " +
            "messageScene=$messageScene, " +
            "authorizeData=$authorizeData)"
    }
}

/**
 * 消息场景信息（仅 type=13 消息反馈）
 *
 * @property ext 扩展信息键值对列表，如 "disable_net_search=1" 表示关闭联网搜索
 *
 * @since 5.0
 */
@Serializable
public class InteractionMessageScene internal constructor(
    public val ext: List<String>? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is InteractionMessageScene) return false

        if (ext != other.ext) return false

        return true
    }

    override fun hashCode(): Int {
        return ext.hashCode()
    }

    override fun toString(): String {
        return "InteractionMessageScene(ext=$ext)"
    }
}

/**
 * 授权数据（仅 type=18/19 用户/群授权事件）
 *
 * @since 5.0
 */
@Serializable
public class AuthorizeData internal constructor(
    @get:JvmExposeBoxed
    public val optScene: AuthorizeDataOptScene? = null,
    @get:JvmExposeBoxed
    public val scope: AuthorizeDataScope? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AuthorizeData) return false

        if (optScene != other.optScene) return false
        if (scope != other.scope) return false

        return true
    }

    override fun hashCode(): Int {
        var result = optScene.hashCode()
        result = 31 * result + scope.hashCode()
        return result
    }

    override fun toString(): String {
        return "AuthorizeData(optScene=$optScene, scope=$scope)"
    }
}


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

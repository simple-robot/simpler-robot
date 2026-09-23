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

@file:OptIn(ExperimentalStdlibApi::class)

package love.forte.simbot.qguild.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.common.DataClassCompatibilities
import love.forte.simbot.qguild.common.EventModelConstructor
import love.forte.simbot.qguild.model.Message
import kotlin.jvm.JvmExposeBoxed
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmStatic

/**
 * C2C 单聊消息类型。
 *
 * 已知类型：`0` 普通文本、`3` 结构化卡片、`101` 并行消息、`102` 聊天记录、`103` 引用消息。
 *
 * [of] 限制为当前已知类型；通过序列化读取上游新增的类型时仍会保留其原始值。
 *
 * @since 5.0
 * @see C2CMessageCreate.Data.messageType
 */
@JvmInline
@JvmExposeBoxed
@Serializable
public value class C2CMessageType private constructor(public val value: Int) {
    @JvmExposeBoxed
    public companion object {
        /** 普通文本消息的原始值。 */
        public const val TEXT_VALUE: Int = 0

        /** 结构化卡片消息的原始值。 */
        public const val ARK_VALUE: Int = 3

        /** 并行消息的原始值。 */
        public const val PARALLEL_MESSAGE_VALUE: Int = 101

        /** 聊天记录消息的原始值。 */
        public const val CHAT_RECORD_VALUE: Int = 102

        /** 引用消息的原始值。 */
        public const val REFERENCE_MESSAGE_VALUE: Int = 103

        /** 普通文本消息。 */
        @JvmStatic
        public val Text: C2CMessageType = C2CMessageType(TEXT_VALUE)

        /** 结构化卡片消息。 */
        @JvmStatic
        public val Ark: C2CMessageType = C2CMessageType(ARK_VALUE)

        /** 并行消息。 */
        @JvmStatic
        public val ParallelMessage: C2CMessageType = C2CMessageType(PARALLEL_MESSAGE_VALUE)

        /** 聊天记录消息。 */
        @JvmStatic
        public val ChatRecord: C2CMessageType = C2CMessageType(CHAT_RECORD_VALUE)

        /** 引用消息。 */
        @JvmStatic
        public val ReferenceMessage: C2CMessageType = C2CMessageType(REFERENCE_MESSAGE_VALUE)

        /**
         * 根据当前已知的原始值构建 [C2CMessageType]。
         *
         * @throws IllegalArgumentException [value] 不是已知的消息类型值时抛出
         */
        @JvmStatic
        public fun of(value: Int): C2CMessageType = when (value) {
            TEXT_VALUE -> Text
            ARK_VALUE -> Ark
            PARALLEL_MESSAGE_VALUE -> ParallelMessage
            CHAT_RECORD_VALUE -> ChatRecord
            REFERENCE_MESSAGE_VALUE -> ReferenceMessage
            else -> throw IllegalArgumentException("Unknown C2C message type: $value")
        }
    }
}

/**
 * [单聊消息](https://bot.q.qq.com/wiki/develop/api-v2/autogen/event/c2c_message_create.html)
 *
 * 触发场景	用户在单聊发送消息给机器人
 */
@Serializable
@SerialName(EventIntents.GroupAndC2CEvent.C2C_MESSAGE_CREATE_TYPE)
@DispatchTypeName(EventIntents.GroupAndC2CEvent.C2C_MESSAGE_CREATE_TYPE)
public data class C2CMessageCreate @EventModelConstructor constructor(
    override val id: String? = null,
    override val s: Long = DEFAULT_SEQ,
    @SerialName("d")
    override val data: Data,
) : Signal.Dispatch() {

    /**
     * The data of [C2CMessageCreate.data].
     *
     * @property id 平台方消息ID，可以用于被动消息发送
     * @property author 发送者
     * @property content 文本消息内容
     * @property timestamp 消息生产时间（RFC3339）
     * @property messageType 消息类型。0=普通文本, 3=结构化卡片, 101=并行消息, 102=聊天记录, 103=引用消息
     * @property messageScene 消息场景；自定义菜单开关操作的设置结果位于 [MessageScene.ext] 中
     * @property attachments 富媒体文件附件，文件类型："图片，语音，视频，文件"
     * `{"content_type": "", "filename": "", "height": "", "width": "", "size": "", "url": ""}`
     *
     */
    @Serializable
    public class Data internal constructor(
        public val id: String,
        public val author: Author,
        public val content: String,
        public val timestamp: String,
        public val attachments: List<Message.Attachment> = emptyList(),
        /**
         * 消息类型。
         *
         * @since 5.0
         */
        @SerialName("message_type")
        @get:JvmExposeBoxed
        public val messageType: C2CMessageType? = null,
        /**
         * 消息场景信息。
         *
         * @since 5.0
         */
        @SerialName("message_scene")
        public val messageScene: MessageScene? = null,
    ) {
        @Deprecated(
            DataClassCompatibilities.DEPRECATED_CONSTRUCTOR_MESSAGE,
            level = DeprecationLevel.ERROR
        )
        @EventModelConstructor
        public constructor(
            id: String,
            author: Author,
            content: String,
            timestamp: String,
            attachments: List<Message.Attachment> = emptyList(),
        ) : this(
            id = id,
            author = author,
            content = content,
            timestamp = timestamp,
            attachments = attachments,
            messageType = null,
            messageScene = null,
        )

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("id"))
        public operator fun component1(): String = id

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("author"))
        public operator fun component2(): Author = author

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("content"))
        public operator fun component3(): String = content

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("timestamp"))
        public operator fun component4(): String = timestamp

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("attachments"))
        public operator fun component5(): List<Message.Attachment> = attachments

        // 旧 data class 的 copy 签名只包含原五项，并保留新增字段当前值。
        @Suppress("DeprecatedCallableAddReplaceWith")
        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
        public fun copy(
            id: String = this.id,
            author: Author = this.author,
            content: String = this.content,
            timestamp: String = this.timestamp,
            attachments: List<Message.Attachment> = this.attachments,
        ): Data = Data(
            id = id,
            author = author,
            content = content,
            timestamp = timestamp,
            attachments = attachments,
            messageType = messageType,
            messageScene = messageScene,
        )

        // 未发布的 5.0 字段不进入旧 data class 的相等性、哈希和字符串表示。
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Data) return false

            if (id != other.id) return false
            if (author != other.author) return false
            if (content != other.content) return false
            if (timestamp != other.timestamp) return false
            if (attachments != other.attachments) return false

            return true
        }

        override fun hashCode(): Int {
            var result = id.hashCode()
            result = 31 * result + author.hashCode()
            result = 31 * result + content.hashCode()
            result = 31 * result + timestamp.hashCode()
            result = 31 * result + attachments.hashCode()
            return result
        }

        override fun toString(): String {
            return "Data(id=$id, author=$author, content=$content, timestamp=$timestamp, attachments=$attachments)"
        }

        // TODO: 官方单聊消息事件还声明了 `ark_data` 和 `msg_elements`；待抽象对应 ARK 与消息元素模型后补齐。
    }

    /**
     * C2C 消息的场景信息。
     *
     * [ext] 中的元素是 `key=value` 形式的扩展信息。自定义菜单的开关操作会在此处携带开关设置结果。
     *
     * @property source 消息来源。
     * @property ext 场景扩展信息。
     *
     * @since 5.0
     */
    @Serializable
    public class MessageScene @EventModelConstructor internal constructor(
        public val source: String? = null,
        public val ext: List<String> = emptyList(),
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is MessageScene) return false

            if (source != other.source) return false
            if (ext != other.ext) return false

            return true
        }

        override fun hashCode(): Int {
            var result = source.hashCode()
            result = 31 * result + ext.hashCode()
            return result
        }

        override fun toString(): String {
            return "MessageScene(source=$source, ext=$ext)"
        }
    }

    /**
     * The [Data.author].
     *
     * TODO: 官方单聊消息事件还声明了 `id`、`username`、`bot`、`union_openid`、`union_user_account`、
     *  `member_openid` 与 `member_role`；待确认跨 C2C/群聊的统一作者模型后补齐。
     */
    @Serializable
    public class Author @EventModelConstructor constructor(
        @SerialName("user_openid")
        public val userOpenid: String,
    ) {
        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("userOpenid"))
        public operator fun component1(): String = userOpenid

        @Suppress("DeprecatedCallableAddReplaceWith")
        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
        public fun copy(userOpenid: String = this.userOpenid): Author = Author(userOpenid)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Author) return false

            if (userOpenid != other.userOpenid) return false

            return true
        }

        override fun hashCode(): Int = userOpenid.hashCode()

        override fun toString(): String = "Author(userOpenid=$userOpenid)"
    }
}

/**
 * 群聊消息内容体
 *
 * @since 4.3.0
 */
@Serializable
public sealed interface GroupMessageData {

    /**
     * 平台方消息 ID，可以用于被动消息发送
     */
    public val id: String

    /**
     * 发送者
     */
    public val author: GroupMessageAuthor

    /**
     * 消息内容
     */
    public val content: String

    /**
     * 消息生产时间（RFC3339）
     */
    public val timestamp: String

    /**
     * 群聊的 openid
     */
    public val groupOpenid: String

    /**
     * 富媒体文件附件，文件类型："图片，语音，视频，文件"
     * `{"content_type": "", "filename": "", "height": "", "width": "", "size": "", "url": ""}`
     */
    public val attachments: List<Message.Attachment>

}

/**
 * 群聊消息体内的发信人信息。
 * @since 4.3.0
 */
@Serializable
public sealed interface GroupMessageAuthor {
    /**
     * 用户在本群的 member_openid
     */
    public val memberOpenid: String

    /**
     * 消息发送者在群内的身份，枚举值：owner、admin、member
     */
    public val memberRole: GroupMessageAuthorRole

    /**
     * 是否是机器人
     */
    public val bot: Boolean

}

/**
 * 群聊消息体内的发信人身份。
 * @since 4.3.0
 */
@Serializable
public enum class GroupMessageAuthorRole {
    @SerialName("owner")
    OWNER,

    @SerialName("admin")
    ADMIN,

    @SerialName("member")
    MEMBER
}

/**
 * [群聊@机器人](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/send-receive/event.html#群聊-机器人)
 *
 * 触发场景	用户在群聊@机器人发送消息
 */
@Serializable
@SerialName(EventIntents.GroupAndC2CEvent.GROUP_AT_MESSAGE_CREATE_TYPE)
@DispatchTypeName(EventIntents.GroupAndC2CEvent.GROUP_AT_MESSAGE_CREATE_TYPE)
public data class GroupAtMessageCreate(
    override val id: String? = null,
    override val s: Long = DEFAULT_SEQ,
    @SerialName("d")
    override val data: Data,
) : Signal.Dispatch() {
    /**
     * The data of [GroupAtMessageCreate.data]
     */
    @Serializable
    public class Data @EventModelConstructor constructor(
        override val id: String,
        override val author: Author,
        override val content: String,
        override val timestamp: String,
        @SerialName("group_openid")
        override val groupOpenid: String,
        override val attachments: List<Message.Attachment> = emptyList(),
    ) : GroupMessageData {
        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("id"))
        public operator fun component1(): String = id

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("author"))
        public operator fun component2(): Author = author

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("content"))
        public operator fun component3(): String = content

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("timestamp"))
        public operator fun component4(): String = timestamp

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("groupOpenid"))
        public operator fun component5(): String = groupOpenid

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("attachments"))
        public operator fun component6(): List<Message.Attachment> = attachments

        @Suppress("DeprecatedCallableAddReplaceWith")
        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
        public fun copy(
            id: String = this.id,
            author: Author = this.author,
            content: String = this.content,
            timestamp: String = this.timestamp,
            groupOpenid: String = this.groupOpenid,
            attachments: List<Message.Attachment> = this.attachments,
        ): Data = Data(id, author, content, timestamp, groupOpenid, attachments)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Data) return false

            if (id != other.id) return false
            if (author != other.author) return false
            if (content != other.content) return false
            if (timestamp != other.timestamp) return false
            if (groupOpenid != other.groupOpenid) return false
            if (attachments != other.attachments) return false

            return true
        }

        override fun hashCode(): Int {
            var result = id.hashCode()
            result = 31 * result + author.hashCode()
            result = 31 * result + content.hashCode()
            result = 31 * result + timestamp.hashCode()
            result = 31 * result + groupOpenid.hashCode()
            result = 31 * result + attachments.hashCode()
            return result
        }

        override fun toString(): String {
            return "Data(id=$id, author=$author, content=$content, timestamp=$timestamp, " +
                "groupOpenid=$groupOpenid, attachments=$attachments)"
        }
    }

    /**
     * The [Data.author]
     */
    @Serializable
    public class Author @EventModelConstructor constructor(
        @SerialName("member_openid")
        override val memberOpenid: String,
        @SerialName("member_role")
        override val memberRole: GroupMessageAuthorRole = GroupMessageAuthorRole.MEMBER,
        override val bot: Boolean = false,
    ) : GroupMessageAuthor {
        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("memberOpenid"))
        public operator fun component1(): String = memberOpenid

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("memberRole"))
        public operator fun component2(): GroupMessageAuthorRole = memberRole

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("bot"))
        public operator fun component3(): Boolean = bot

        @Suppress("DeprecatedCallableAddReplaceWith")
        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
        public fun copy(
            memberOpenid: String = this.memberOpenid,
            memberRole: GroupMessageAuthorRole = this.memberRole,
            bot: Boolean = this.bot,
        ): Author = Author(memberOpenid, memberRole, bot)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Author) return false

            if (memberOpenid != other.memberOpenid) return false
            if (memberRole != other.memberRole) return false
            if (bot != other.bot) return false

            return true
        }

        override fun hashCode(): Int {
            var result = memberOpenid.hashCode()
            result = 31 * result + memberRole.hashCode()
            result = 31 * result + bot.hashCode()
            return result
        }

        override fun toString(): String {
            return "Author(memberOpenid=$memberOpenid, memberRole=$memberRole, bot=$bot)"
        }
    }
}


/**
 * [群聊全量消息](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/send-receive/event.html#群聊全量消息)
 *
 * > 说明：当群主设定允许该机器人接收群内全部消息时，机器人可接收到群内所有成员在群内的发言消息。
 *
 * ### 事件示例
 *
 * ```JSON5
 * // Websocket
 * {
 *   "author": {
 *       "member_openid": "E4F4AEA33253A2797FB897C50B81D7ED"
 *   },
 *   "content": " 123",
 *   "group_openid": "C9F778FE6ADF9D1D1DBE395BF744A33A",
 *   "id": "ROBOT1.0_eBIyWnxpmSu6uLQ7u7fU0eGloKGYg4eEa737vRyKnMCgyZjKi7JLYkQ9B0VapbiY",
 *   "timestamp": "2023-11-06T13:37:18+08:00"
 * }
 * ```
 */
@Serializable
@SerialName(EventIntents.GroupAndC2CEvent.GROUP_MESSAGE_CREATE_TYPE)
@DispatchTypeName(EventIntents.GroupAndC2CEvent.GROUP_MESSAGE_CREATE_TYPE)
public data class GroupMessageCreate(
    override val id: String? = null,
    override val s: Long = DEFAULT_SEQ,
    @SerialName("d")
    override val data: Data,
) : Signal.Dispatch() {

    /**
     * The data of [GroupMessageCreate.data]
     */
    @Serializable
    public class Data @EventModelConstructor constructor(
        override val id: String,
        override val author: Author,
        override val content: String,
        override val timestamp: String,
        @SerialName("group_openid")
        override val groupOpenid: String,
        override val attachments: List<Message.Attachment> = emptyList(),
    ) : GroupMessageData {
        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("id"))
        public operator fun component1(): String = id

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("author"))
        public operator fun component2(): Author = author

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("content"))
        public operator fun component3(): String = content

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("timestamp"))
        public operator fun component4(): String = timestamp

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("groupOpenid"))
        public operator fun component5(): String = groupOpenid

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("attachments"))
        public operator fun component6(): List<Message.Attachment> = attachments

        @Suppress("DeprecatedCallableAddReplaceWith")
        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
        public fun copy(
            id: String = this.id,
            author: Author = this.author,
            content: String = this.content,
            timestamp: String = this.timestamp,
            groupOpenid: String = this.groupOpenid,
            attachments: List<Message.Attachment> = this.attachments,
        ): Data = Data(id, author, content, timestamp, groupOpenid, attachments)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Data) return false

            if (id != other.id) return false
            if (author != other.author) return false
            if (content != other.content) return false
            if (timestamp != other.timestamp) return false
            if (groupOpenid != other.groupOpenid) return false
            if (attachments != other.attachments) return false

            return true
        }

        override fun hashCode(): Int {
            var result = id.hashCode()
            result = 31 * result + author.hashCode()
            result = 31 * result + content.hashCode()
            result = 31 * result + timestamp.hashCode()
            result = 31 * result + groupOpenid.hashCode()
            result = 31 * result + attachments.hashCode()
            return result
        }

        override fun toString(): String {
            return "Data(id=$id, author=$author, content=$content, timestamp=$timestamp, " +
                "groupOpenid=$groupOpenid, attachments=$attachments)"
        }
    }

    /**
     * The [Data.author]
     */
    @Serializable
    public class Author @EventModelConstructor constructor(
        @SerialName("member_openid")
        override val memberOpenid: String,
        @SerialName("member_role")
        override val memberRole: GroupMessageAuthorRole = GroupMessageAuthorRole.MEMBER,
        override val bot: Boolean = false,
    ) : GroupMessageAuthor {
        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("memberOpenid"))
        public operator fun component1(): String = memberOpenid

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("memberRole"))
        public operator fun component2(): GroupMessageAuthorRole = memberRole

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("bot"))
        public operator fun component3(): Boolean = bot

        @Suppress("DeprecatedCallableAddReplaceWith")
        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
        public fun copy(
            memberOpenid: String = this.memberOpenid,
            memberRole: GroupMessageAuthorRole = this.memberRole,
            bot: Boolean = this.bot,
        ): Author = Author(memberOpenid, memberRole, bot)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Author) return false

            if (memberOpenid != other.memberOpenid) return false
            if (memberRole != other.memberRole) return false
            if (bot != other.bot) return false

            return true
        }

        override fun hashCode(): Int {
            var result = memberOpenid.hashCode()
            result = 31 * result + memberRole.hashCode()
            result = 31 * result + bot.hashCode()
            return result
        }

        override fun toString(): String {
            return "Author(memberOpenid=$memberOpenid, memberRole=$memberRole, bot=$bot)"
        }
    }
}

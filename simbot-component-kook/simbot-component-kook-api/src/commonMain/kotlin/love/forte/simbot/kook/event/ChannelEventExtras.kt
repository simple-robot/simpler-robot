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

package love.forte.simbot.kook.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.kook.objects.ReactionEmoji
import love.forte.simbot.kook.objects.SimpleChannel


/**
 * [新增频道](https://developer.kookapp.cn/doc/event/channel#%E6%96%B0%E5%A2%9E%E9%A2%91%E9%81%93)
 *
 * @author ForteScarlet
 */
@Serializable
@SerialName(AddedChannelEventExtra.TYPE)
public data class AddedChannelEventExtra(override val body: SimpleChannel) : SystemExtra() {
    public companion object {
        public const val TYPE: String = "added_channel"
    }
}


/**
 * [修改频道信息](https://developer.kookapp.cn/doc/event/channel#%E4%BF%AE%E6%94%B9%E9%A2%91%E9%81%93%E4%BF%A1%E6%81%AF)
 *
 * @author ForteScarlet
 */
@Serializable
@SerialName(UpdatedChannelEventExtra.TYPE)
public data class UpdatedChannelEventExtra(override val body: SimpleChannel) : SystemExtra() {
    public companion object {
        public const val TYPE: String = "updated_channel"
    }
}


/**
 * [DeletedChannelEventExtra] 事件体，被删除的子频道信息。
 *
 * @see DeletedChannelEventExtra
 */
@Serializable
public data class DeletedChannelEventBody(
    /**
     * 被删掉的频道 id
     */
    val id: String,

    /**
     * 删除时间(毫秒)
     */
    @SerialName("deleted_at")
    val deletedAt: Long
)

/**
 * [删除频道](https://developer.kookapp.cn/doc/event/channel#%E5%88%A0%E9%99%A4%E9%A2%91%E9%81%93)
 *
 * @author ForteScarlet
 */
@Serializable
@SerialName(DeletedChannelEventExtra.TYPE)
public data class DeletedChannelEventExtra(override val body: DeletedChannelEventBody) : SystemExtra() {
    public companion object {
        public const val TYPE: String = "deleted_channel"
    }
}


/**
 * [频道内用户添加 reaction](https://developer.kookapp.cn/doc/event/channel#%E9%A2%91%E9%81%93%E5%86%85%E7%94%A8%E6%88%B7%E6%B7%BB%E5%8A%A0%20reaction)
 *
 * @author ForteScarlet
 */
@Serializable
@SerialName(AddedReactionEventExtra.TYPE)
public data class AddedReactionEventExtra(override val body: ReactionEventExtraBody) : SystemExtra() {
    public companion object {
        public const val TYPE: String = "added_reaction"
    }
}


/**
 * [频道内用户取消 reaction](https://developer.kookapp.cn/doc/event/channel#%E9%A2%91%E9%81%93%E5%86%85%E7%94%A8%E6%88%B7%E5%8F%96%E6%B6%88%20reaction)
 *
 * @author ForteScarlet
 */
@Serializable
@SerialName(DeletedReactionEventExtra.TYPE)
public data class DeletedReactionEventExtra(override val body: ReactionEventExtraBody) : SystemExtra() {
    public companion object {
        public const val TYPE: String = "deleted_reaction"
    }
}

/**
 * Reaction 相关事件 extra 内 body
 */
@Serializable
public data class ReactionEventExtraBody(
    /**
     * 用户点击的消息 id
     */
    @SerialName("msg_id")
    val msgId: String,

    /**
     * 点击的用户
     */
    @SerialName("user_id")
    val userId: String,

    /**
     * 频道 id
     */
    @SerialName("channel_id")
    val channelId: String,

    /**
     * Reaction emoji 信息
     */
    val emoji: ReactionEmoji
)


/**
 * [频道消息更新](https://developer.kookapp.cn/doc/event/channel#%E9%A2%91%E9%81%93%E6%B6%88%E6%81%AF%E6%9B%B4%E6%96%B0)
 *
 * @author ForteScarlet
 */
@Serializable
@SerialName(UpdatedMessageEventExtra.TYPE)
public data class UpdatedMessageEventExtra(override val body: Body) : SystemExtra() {
    public companion object {
        public const val TYPE: String = "updated_message"
    }

    /**
     * 频道消息更新事件 extra 内 body
     */
    @Serializable
    public data class Body(
        /**
         * 被更新的消息的 id
         */
        @SerialName("msg_id")
        val msgId: String,

        /**
         * 消息内容
         */
        val content: String,

        /**
         * 频道 id
         */
        @SerialName("channel_id")
        val channelId: String,


        /**
         * 提及的用户 id 组成的列表
         */
        val mention: List<String> = emptyList(),

        /**
         * 是否提及 全体成员
         */
        @SerialName("mention_all")
        val isMentionAll: Boolean = false,

        /**
         * 是否提及 在线成员
         */
        @SerialName("mention_here")
        val isMentionHere: Boolean = false,

        /**
         * 提及的角色 id 组成的列表
         */
        @SerialName("mention_roles")
        val mentionRoles: List<Int> = emptyList(),

        /**
         * 更新时间戳(毫秒)
         */
        @SerialName("updated_at")
        val updatedAt: Long
    )
}


/**
 * [频道消息被删除](https://developer.kookapp.cn/doc/event/channel#%E9%A2%91%E9%81%93%E6%B6%88%E6%81%AF%E8%A2%AB%E5%88%A0%E9%99%A4)
 *
 * @author ForteScarlet
 */
@Serializable
@SerialName(DeletedMessageEventExtra.TYPE)
public data class DeletedMessageEventExtra(override val body: Body) : SystemExtra() {
    public companion object {
        public const val TYPE: String = "deleted_message"
    }

    /**
     * [DeletedMessageEventExtra] 事件 body
     */
    @Serializable
    public data class Body(
        /**
         * 被更新的消息的 id
         */
        @SerialName("msg_id")
        public val msgId: String,

        /**
         * 频道 id
         */
        @SerialName("channel_id")
        public val channelId: String
    )
}


/**
 * [新的频道置顶消息](https://developer.kookapp.cn/doc/event/channel#%E6%96%B0%E7%9A%84%E9%A2%91%E9%81%93%E7%BD%AE%E9%A1%B6%E6%B6%88%E6%81%AF)
 *
 * @author ForteScarlet
 */
@Serializable
@SerialName(PinnedMessageEventExtra.TYPE)
public data class PinnedMessageEventExtra(override val body: PingEventExtraBody) : SystemExtra() {
    public companion object {
        public const val TYPE: String = "pinned_message"
    }
}


/**
 * [取消频道置顶消息](https://developer.kookapp.cn/doc/event/channel#%E5%8F%96%E6%B6%88%E9%A2%91%E9%81%93%E7%BD%AE%E9%A1%B6%E6%B6%88%E6%81%AF)
 *
 * @author ForteScarlet
 */
@Serializable
@SerialName(UnpinnedMessageEventExtra.TYPE)
public data class UnpinnedMessageEventExtra(override val body: PingEventExtraBody) : SystemExtra() {
    public companion object {
        public const val TYPE: String = "unpinned_message"
    }
}


/**
 * 置顶消息相关事件 extra 内 body
 *
 */
@Serializable
public data class PingEventExtraBody(
    /**
     * 频道 id
     */
    @SerialName("channel_id")
    val channelId: String,

    /**
     * 操作人 id
     */
    @SerialName("operator_id")
    val operatorId: String,

    /**
     * 被操作的消息 id
     */
    @SerialName("msg_id")
    val msgId: String
)

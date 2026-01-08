/*
 * Copyright (c) 2022-2025. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.qguild.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.PrivateDomainOnly
import love.forte.simbot.qguild.event.EventIntents.Companion.READY_TYPE
import love.forte.simbot.qguild.event.EventIntents.Companion.RESUMED_TYPE
import love.forte.simbot.qguild.internal.EventNameBasedMarker
import love.forte.simbot.qguild.model.User
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic

/**
 * [intents](https://bot.q.qq.com/wiki/develop/api/gateway/intents.html#intents)
 *
 * 事件的 `intents` 是一个标记位，每一位都代表不同的事件，如果需要接收某类事件，就将该位置为 1。
 *
 * 每个 `intents` 位代表的是一类事件，可以使用使用 websocket 传输的数据中的 `t` 字段的值来区分。
 *
 * 合并两种监听类型：
 * ```kotlin
 * val intA = Intents(...)
 * val intB = Intents(...)
 * val intC = intA + intB
 * ```
 *
 *
 * @see EventIntents
 *
 */
@JvmInline
@Serializable
public value class Intents(public val value: Int) {

    /**
     * 合并两个 [Intents]，取二者标记位的或值。
     */
    public operator fun plus(intents: Intents): Intents = Intents(value or intents.value)

    /**
     * 判断 [intents] 中的权限是否**完全**包含在当前 [value] 中。
     */
    public operator fun contains(intents: Intents): Boolean = contains(intents, true)

    /**
     * 判断 [intents] 中的权限是否包含在当前 [value] 中。
     * 当 [exactly] 为 `true` 时代表**完全包含**才返回 `true`，否则仅部分包含也会得到 `true`。
     *
     * @param exactly 是否判断完全包含, 默认为 `true`。
     *
     */
    public fun contains(intents: Intents, exactly: Boolean): Boolean {
        return if (exactly) {
            intents.value and value == intents.value
        } else {
            intents.value and value != 0
        }
    }

    public companion object {
        public val ZERO: Intents = Intents(0)
    }

}

/**
 * 各事件的 [`intents`][Intents] 和类型常量。
 *
 * ## 权限
 * 事件类型的订阅，是有权限控制的，除了 [`GUILDS`][Guilds]，[`PUBLIC_GUILD_MESSAGES`][PublicGuildMessages]，[`GUILD_MEMBERS`][GuildMembers] 事件是基础的事件，
 * 默认有权限订阅之外，其他的特殊事件，都需要经过申请才能够使用，如果在 鉴权的时候传递了无权限的 intents，websocket 会报错，并直接关闭连接。
 * 请开发者注意订阅事件的范围需要控制在自己所需要的范围之内。
 *
 * 如果拥有的某个特殊事件类型的权限被取消，则在当前连接上不会报错，但是将不会收到对应的事件类型，
 * 如果重新连接，则报错，所以如果开发者的事件类型权限被取消，
 * 请及时调整监听事件代码，避免报错导致的无法连接。
 *
 * 更多参考[文档](https://bot.q.qq.com/wiki/develop/api/gateway/intents.html)
 *
 * @see EventIntentsAggregation
 */
public sealed class EventIntents {
    // 实现时，必须要有一个 internal 的名为 INTENTS 的 Int 类型的常量来表示 intents 值，
    // 此常量会被 ksp 处理并用于生成 EventIntentsAggregation

    /**
     * 获取事件类型对应的标记为值。
     */
    public abstract val intentsValue: Int

    /**
     * ```
     * GUILDS (1 << 0)
     *   - GUILD_CREATE           // 当机器人加入新guild时
     *   - GUILD_UPDATE           // 当guild资料发生变更时
     *   - GUILD_DELETE           // 当机器人退出guild时
     *   - CHANNEL_CREATE         // 当channel被创建时
     *   - CHANNEL_UPDATE         // 当channel被更新时
     *   - CHANNEL_DELETE         // 当channel被删除时
     * ```
     *
     */
    public data object Guilds : EventIntents() {
        public const val INTENTS_INDEX: Int = 0
        internal const val INTENTS: Int = 1 shl INTENTS_INDEX

        /** 频道事件 `intents` */
        @get:JvmStatic
        @get:JvmName("getIntents")
        public val intents: Intents = Intents(INTENTS)

        override val intentsValue: Int
            get() = INTENTS

        /** 当机器人加入新guild时 */
        public const val GUILD_CREATE_TYPE: String = "GUILD_CREATE"

        /** 当guild资料发生变更时 */
        public const val GUILD_UPDATE_TYPE: String = "GUILD_UPDATE"

        /** 当机器人退出guild时 */
        public const val GUILD_DELETE_TYPE: String = "GUILD_DELETE"

        /** 当channel被创建时 */
        public const val CHANNEL_CREATE_TYPE: String = "CHANNEL_CREATE"

        /** 当channel被更新时 */
        public const val CHANNEL_UPDATE_TYPE: String = "CHANNEL_UPDATE"

        /** 当channel被删除时 */
        public const val CHANNEL_DELETE_TYPE: String = "CHANNEL_DELETE"
    }

    /**
     * ```
     * GUILD_MEMBERS (1 << 1)
     *   - GUILD_MEMBER_ADD       // 当成员加入时
     *   - GUILD_MEMBER_UPDATE    // 当成员资料变更时
     *   - GUILD_MEMBER_REMOVE    // 当成员被移除时
     * ```
     */
    public data object GuildMembers : EventIntents() {
        public const val INTENTS_INDEX: Int = 1
        internal const val INTENTS = 1 shl INTENTS_INDEX

        /** 频道成员事件 `intents` */
        @get:JvmStatic
        @get:JvmName("getIntents")
        public val intents: Intents = Intents(INTENTS)

        override val intentsValue: Int
            get() = INTENTS

        /** 当成员加入时 */
        public const val GUILD_MEMBER_ADD_TYPE: String = "GUILD_MEMBER_ADD"

        /** 当成员资料变更时 */
        public const val GUILD_MEMBER_UPDATE_TYPE: String = "GUILD_MEMBER_UPDATE"

        /** 当成员被移除时 */
        public const val GUILD_MEMBER_REMOVE_TYPE: String = "GUILD_MEMBER_REMOVE"

    }

    /**
     * ```
     * GUILD_MESSAGES (1 << 9)    // 消息事件，仅 *私域* 机器人能够设置此 intents。
     *   - MESSAGE_CREATE         // 发送消息事件，代表频道内的全部消息，而不只是 at 机器人的消息。内容与 AT_MESSAGE_CREATE 相同
     *   - MESSAGE_DELETE         // 删除（撤回）消息事件
     * ```
     */
    @PrivateDomainOnly
    public data object GuildMessages : EventIntents() {
        public const val INTENTS_INDEX: Int = 9
        internal const val INTENTS = 1 shl INTENTS_INDEX

        /** 频道消息事件 `intents` */
        @get:JvmStatic
        @get:JvmName("getIntents")
        public val intents: Intents = Intents(INTENTS)

        override val intentsValue: Int
            get() = INTENTS

        /** 发送消息事件，代表频道内的全部消息，而不只是 at 机器人的消息。内容与 AT_MESSAGE_CREATE 相同 */
        public const val MESSAGE_CREATE_TYPE: String = "MESSAGE_CREATE"

        /** 删除（撤回）消息事件 */
        public const val MESSAGE_DELETE_TYPE: String = "MESSAGE_DELETE"
    }

    /**
     * ```
     * GUILD_MESSAGE_REACTIONS (1 << 10)
     *   - MESSAGE_REACTION_ADD    // 为消息添加表情表态
     *   - MESSAGE_REACTION_REMOVE // 为消息删除表情表态
     * ```
     */
    public data object GuildMessageReactions : EventIntents() {
        public const val INTENTS_INDEX: Int = 10
        internal const val INTENTS: Int = 1 shl INTENTS_INDEX

        /** 表情表态事件 `intents` */
        @get:JvmStatic
        @get:JvmName("getIntents")
        public val intents: Intents = Intents(INTENTS)

        override val intentsValue: Int
            get() = INTENTS

        /** 为消息添加表情表态 */
        public const val MESSAGE_REACTION_ADD_TYPE: String = "MESSAGE_REACTION_ADD"

        /** 为消息删除表情表态 */
        public const val MESSAGE_REACTION_REMOVE_TYPE: String = "MESSAGE_REACTION_REMOVE"

    }

    /**
     * ```
     * DIRECT_MESSAGE (1 << 12)
     *   - DIRECT_MESSAGE_CREATE   // 当收到用户发给机器人的私信消息时
     *   - DIRECT_MESSAGE_DELETE   // 删除（撤回）消息事件
     * ```
     */
    public data object DirectMessage : EventIntents() {
        public const val INTENTS_INDEX: Int = 12
        internal const val INTENTS: Int = 1 shl INTENTS_INDEX

        /** 表情表态事件 `intents` */
        @get:JvmStatic
        @get:JvmName("getIntents")
        public val intents: Intents = Intents(INTENTS)

        override val intentsValue: Int
            get() = INTENTS

        /** 当收到用户发给机器人的私信消息时 */
        public const val DIRECT_MESSAGE_CREATE_TYPE: String = "DIRECT_MESSAGE_CREATE"

        /** 删除（撤回）消息事件 */
        public const val DIRECT_MESSAGE_DELETE_TYPE: String = "DIRECT_MESSAGE_DELETE"

    }

    /**
     * ```
     * OPEN_FORUMS_EVENT (1 << 18)      // 论坛事件, 此为公域的论坛事件
     *   - OPEN_FORUM_THREAD_CREATE     // 当用户创建主题时
     *   - OPEN_FORUM_THREAD_UPDATE     // 当用户更新主题时
     *   - OPEN_FORUM_THREAD_DELETE     // 当用户删除主题时
     *   - OPEN_FORUM_POST_CREATE       // 当用户创建帖子时
     *   - OPEN_FORUM_POST_DELETE       // 当用户删除帖子时
     *   - OPEN_FORUM_REPLY_CREATE      // 当用户回复评论时
     *   - OPEN_FORUM_REPLY_DELETE      // 当用户删除评论时
     * ```
     */
    public data object OpenForumsEvent : EventIntents() {
        public const val INTENTS_INDEX: Int = 18
        internal const val INTENTS: Int = 1 shl INTENTS_INDEX

        /** 论坛事件 `intents` */
        @get:JvmStatic
        @get:JvmName("getIntents")
        public val intents: Intents = Intents(INTENTS)

        override val intentsValue: Int
            get() = INTENTS

        /** 当用户创建主题时 */
        public const val OPEN_FORUM_THREAD_CREATE_TYPE: String = "OPEN_FORUM_THREAD_CREATE"

        /** 当用户更新主题时 */
        public const val OPEN_FORUM_THREAD_UPDATE_TYPE: String = "OPEN_FORUM_THREAD_UPDATE"

        /** 当用户删除主题时 */
        public const val OPEN_FORUM_THREAD_DELETE_TYPE: String = "OPEN_FORUM_THREAD_DELETE"

        /** 当用户创建帖子时 */
        public const val OPEN_FORUM_POST_CREATE_TYPE: String = "OPEN_FORUM_POST_CREATE"

        /** 当用户删除帖子时 */
        public const val OPEN_FORUM_POST_DELETE_TYPE: String = "OPEN_FORUM_POST_DELETE"

        /** 当用户回复评论时 */
        public const val OPEN_FORUM_REPLY_CREATE_TYPE: String = "OPEN_FORUM_REPLY_CREATE"

        /** 当用户删除评论时 */
        public const val OPEN_FORUM_REPLY_DELETE_TYPE: String = "OPEN_FORUM_REPLY_DELETE"

    }

    /**
     * ```
     * AUDIO_OR_LIVE_CHANNEL_MEMBER (1 << 19)  // 音视频/直播子频道成员进出事件
     *   - AUDIO_OR_LIVE_CHANNEL_MEMBER_ENTER  // 当用户进入音视频/直播子频道
     *   - AUDIO_OR_LIVE_CHANNEL_MEMBER_EXIT   // 当用户离开音视频/直播子频道
     * ```
     */
    public data object AudioOrLiveChannelMember : EventIntents() {
        public const val INTENTS_INDEX: Int = 19
        internal const val INTENTS: Int = 1 shl INTENTS_INDEX

        /** 音视频/直播子频道成员进出事件 `intents` */
        @get:JvmStatic
        @get:JvmName("getIntents")
        public val intents: Intents = Intents(INTENTS)

        override val intentsValue: Int
            get() = INTENTS

        /** 当用户进入音视频/直播子频道 */
        public const val AUDIO_OR_LIVE_CHANNEL_MEMBER_ENTER_TYPE: String = "AUDIO_OR_LIVE_CHANNEL_MEMBER_ENTER"

        /** 当用户离开音视频/直播子频道 */
        public const val AUDIO_OR_LIVE_CHANNEL_MEMBER_EXIT_TYPE: String = "AUDIO_OR_LIVE_CHANNEL_MEMBER_EXIT"

    }

    /**
     * ```
     * GROUP_AND_C2C_EVENT (1 << 25)
     *   - C2C_MESSAGE_CREATE      // 用户单聊发消息给机器人时候
     *   - FRIEND_ADD              // 用户添加使用机器人
     *   - FRIEND_DEL              // 用户删除机器人
     *   - C2C_MSG_REJECT          // 用户在机器人资料卡手动关闭"主动消息"推送
     *   - C2C_MSG_RECEIVE         // 用户在机器人资料卡手动开启"主动消息"推送开关
     *   - GROUP_AT_MESSAGE_CREATE // 用户在群里@机器人时收到的消息
     *   - GROUP_ADD_ROBOT         // 机器人被添加到群聊
     *   - GROUP_DEL_ROBOT         // 机器人被移出群聊
     *   - GROUP_MSG_REJECT        // 群管理员主动在机器人资料页操作关闭通知
     *   - GROUP_MSG_RECEIVE       // 群管理员主动在机器人资料页操作开启通知
     * ```
     */
    @EventNameBasedMarker(
        snackLower = "group_and_c2c_event",
        snackUpper = "GROUP_AND_C2C_EVENT"
    )
    public data object GroupAndC2CEvent : EventIntents() {
        public const val INTENTS_INDEX: Int = 25
        internal const val INTENTS: Int = 1 shl INTENTS_INDEX

        /** C2C群聊相关事件 `intents` */
        @get:JvmStatic
        @get:JvmName("getIntents")
        public val intents: Intents = Intents(INTENTS)

        override val intentsValue: Int
            get() = INTENTS

        /**
         * 用户单聊发消息给机器人时候
         */
        public const val C2C_MESSAGE_CREATE_TYPE: String = "C2C_MESSAGE_CREATE"

        /**
         * 用户添加使用机器人
         */
        public const val FRIEND_ADD_TYPE: String = "FRIEND_ADD"

        /**
         * 用户删除机器人
         */
        public const val FRIEND_DEL_TYPE: String = "FRIEND_DEL"

        /**
         * 用户在机器人资料卡手动关闭"主动消息"推送
         */
        public const val C2C_MSG_REJECT_TYPE: String = "C2C_MSG_REJECT"

        /**
         * 用户在机器人资料卡手动开启"主动消息"推送开关
         */
        public const val C2C_MSG_RECEIVE_TYPE: String = "C2C_MSG_RECEIVE"

        /**
         * 用户在群里@机器人时收到的消息
         */
        public const val GROUP_AT_MESSAGE_CREATE_TYPE: String = "GROUP_AT_MESSAGE_CREATE"

        /**
         * 机器人被添加到群聊
         */
        public const val GROUP_ADD_ROBOT_TYPE: String = "GROUP_ADD_ROBOT"

        /**
         * 机器人被移出群聊
         */
        public const val GROUP_DEL_ROBOT_TYPE: String = "GROUP_DEL_ROBOT"

        /**
         * 群管理员主动在机器人资料页操作关闭通知
         */
        public const val GROUP_MSG_REJECT_TYPE: String = "GROUP_MSG_REJECT"

        /**
         * 群管理员主动在机器人资料页操作开启通知
         */
        public const val GROUP_MSG_RECEIVE_TYPE: String = "GROUP_MSG_RECEIVE"
    }

    /**
     * ```
     * INTERACTION (1 << 26)
     *   - INTERACTION_CREATE     // 互动事件创建时
     * ```
     */
    public data object Interaction : EventIntents() {
        public const val INTENTS_INDEX: Int = 26
        internal const val INTENTS: Int = 1 shl INTENTS_INDEX

        /** 互动事件 `intents` */
        @get:JvmStatic
        @get:JvmName("getIntents")
        public val intents: Intents = Intents(INTENTS)

        override val intentsValue: Int
            get() = INTENTS

        /** 互动事件创建时 */
        public const val INTERACTION_CREATE_TYPE: String = "INTERACTION_CREATE"

    }

    /**
     * ```
     * MESSAGE_AUDIT (1 << 27)
     * - MESSAGE_AUDIT_PASS     // 消息审核通过
     * - MESSAGE_AUDIT_REJECT   // 消息审核不通过
     * ```
     */
    public data object MessageAudit : EventIntents() {
        public const val INTENTS_INDEX: Int = 27
        internal const val INTENTS: Int = 1 shl INTENTS_INDEX

        /** 互动事件 `intents` */
        @get:JvmStatic
        @get:JvmName("getIntents")
        public val intents: Intents = Intents(INTENTS)

        override val intentsValue: Int
            get() = INTENTS

        /** 消息审核通过 */
        public const val MESSAGE_AUDIT_PASS_TYPE: String = "MESSAGE_AUDIT_PASS"

        /** 消息审核不通过 */
        public const val MESSAGE_AUDIT_REJECT_TYPE: String = "MESSAGE_AUDIT_REJECT"

    }

    /**
     * ```
     * FORUMS_EVENT (1 << 28)  // 论坛事件，仅 *私域* 机器人能够设置此 intents。
     *   - FORUM_THREAD_CREATE     // 当用户创建主题时
     *   - FORUM_THREAD_UPDATE     // 当用户更新主题时
     *   - FORUM_THREAD_DELETE     // 当用户删除主题时
     *   - FORUM_POST_CREATE       // 当用户创建帖子时
     *   - FORUM_POST_DELETE       // 当用户删除帖子时
     *   - FORUM_REPLY_CREATE      // 当用户回复评论时
     *   - FORUM_REPLY_DELETE      // 当用户删除评论时
     *   - FORUM_PUBLISH_AUDIT_RESULT      // 当用户发表审核通过时
     * ```
     */
    @PrivateDomainOnly
    public data object ForumsEvent : EventIntents() {
        public const val INTENTS_INDEX: Int = 28
        internal const val INTENTS: Int = 1 shl INTENTS_INDEX

        /** 论坛事件 `intents` */
        @get:JvmStatic
        @get:JvmName("getIntents")
        public val intents: Intents = Intents(INTENTS)

        override val intentsValue: Int
            get() = INTENTS

        /** 当用户创建主题时 */
        public const val FORUM_THREAD_CREATE_TYPE: String = "FORUM_THREAD_CREATE"

        /** 当用户更新主题时 */
        public const val FORUM_THREAD_UPDATE_TYPE: String = "FORUM_THREAD_UPDATE"

        /** 当用户删除主题时 */
        public const val FORUM_THREAD_DELETE_TYPE: String = "FORUM_THREAD_DELETE"

        /** 当用户创建帖子时 */
        public const val FORUM_POST_CREATE_TYPE: String = "FORUM_POST_CREATE"

        /** 当用户删除帖子时 */
        public const val FORUM_POST_DELETE_TYPE: String = "FORUM_POST_DELETE"

        /** 当用户回复评论时 */
        public const val FORUM_REPLY_CREATE_TYPE: String = "FORUM_REPLY_CREATE"

        /** 当用户删除评论时 */
        public const val FORUM_REPLY_DELETE_TYPE: String = "FORUM_REPLY_DELETE"

        /** 当用户发表审核通过时 */
        public const val FORUM_PUBLISH_AUDIT_RESULT_TYPE: String = "FORUM_PUBLISH_AUDIT_RESULT"

    }

    /**
     * ```
     * AUDIO_ACTION (1 << 29)
     *   - AUDIO_START             // 音频开始播放时
     *   - AUDIO_FINISH            // 音频播放结束时
     *   - AUDIO_ON_MIC            // 上麦时
     *   - AUDIO_OFF_MIC           // 下麦时
     * ```
     */
    @PrivateDomainOnly
    public data object AudioAction : EventIntents() {
        public const val INTENTS_INDEX: Int = 29
        internal const val INTENTS: Int = 1 shl INTENTS_INDEX

        /** 论坛事件 `intents` */
        @get:JvmStatic
        @get:JvmName("getIntents")
        public val intents: Intents = Intents(INTENTS)

        override val intentsValue: Int
            get() = INTENTS

        /** 音频开始播放时 */
        public const val AUDIO_START_TYPE: String = "AUDIO_START"

        /** 音频播放结束时 */
        public const val AUDIO_FINISH_TYPE: String = "AUDIO_FINISH"

        /** 上麦时 */
        public const val AUDIO_ON_MIC_TYPE: String = "AUDIO_ON_MIC"

        /** 下麦时 */
        public const val AUDIO_OFF_MIC_TYPE: String = "AUDIO_OFF_MIC"

    }

    /**
     * ```
     * PUBLIC_GUILD_MESSAGES (1 << 30) // 消息事件，此为公域的消息事件
     *   - AT_MESSAGE_CREATE       // 当收到@机器人的消息时
     *   - PUBLIC_MESSAGE_DELETE   // 当频道的消息被删除时
     * ```
     */
    @PrivateDomainOnly
    public data object PublicGuildMessages : EventIntents() {
        public const val INTENTS_INDEX: Int = 30
        internal const val INTENTS: Int = 1 shl INTENTS_INDEX

        /** 论坛事件 `intents` */
        @get:JvmStatic
        @get:JvmName("getIntents")
        public val intents: Intents = Intents(INTENTS)

        override val intentsValue: Int
            get() = INTENTS

        /** 当收到@机器人的消息时 */
        public const val AT_MESSAGE_CREATE_TYPE: String = "AT_MESSAGE_CREATE"

        /** 当频道的消息被删除时 */
        public const val PUBLIC_MESSAGE_DELETE_TYPE: String = "PUBLIC_MESSAGE_DELETE"

    }

    public companion object {
        /**
         * 鉴权成功之后.
         *
         * 鉴权成功之后，后台会下发一个 Ready Event.
         *
         * 更多参考[文档](https://bot.q.qq.com/wiki/develop/api/gateway/reference.html#_2-%E9%89%B4%E6%9D%83%E8%BF%9E%E6%8E%A5)
         */
        public const val READY_TYPE: String = "READY"

        /**
         * 恢复成功之后，就开始补发遗漏事件，所有事件补发完成之后，会下发一个 `Resumed Event`
         *
         * 更多参考[文档](https://bot.q.qq.com/wiki/develop/api/gateway/reference.html#_4-%E6%81%A2%E5%A4%8D%E8%BF%9E%E6%8E%A5)
         */
        public const val RESUMED_TYPE: String = "RESUMED"
    }
}

/**
 * [EventIntents] 的所有实现类型的数组。
 *
 * Deprecated: 使用更可靠的 [EventIntentsAggregation]
 *
 * @see EventIntentsAggregation.allEventIntents
 */
@Deprecated(
    "Use EventIntentsAggregation.allEventIntents",
    ReplaceWith("EventIntentsAggregation.allEventIntents()")
)
public val EventIntentsInstances: Array<EventIntents>
    get() = EventIntentsAggregation.allEventIntents().toTypedArray()

/**
 * 鉴权成功之后，后台会下发的 Ready Event.
 */
@Serializable
@SerialName(READY_TYPE)
@DispatchTypeName(READY_TYPE)
public data class Ready(
    override val id: String? = null,
    override val s: Long = DEFAULT_SEQ,
    @SerialName("d") override val data: Data
) : Signal.Dispatch() {
    /**
     * [Ready] 的事件内容
     */
    @Serializable
    public data class Data(
        val version: String,
        @SerialName("session_id") val sessionId: String,
        val user: User,
        val shard: Shard
    )
}

/**
 * [4.恢复连接](https://bot.q.qq.com/wiki/develop/api/gateway/reference.html)
 *
 * 恢复成功之后，就开始补发遗漏事件，所有事件补发完成之后，会下发一个 `Resumed Event`
 *
 */
@Serializable
@SerialName(RESUMED_TYPE)
@DispatchTypeName(RESUMED_TYPE)
public data class Resumed(
    override val id: String? = null,
    override val s: Long = DEFAULT_SEQ,
    @SerialName("d") override val data: String
) : Signal.Dispatch()


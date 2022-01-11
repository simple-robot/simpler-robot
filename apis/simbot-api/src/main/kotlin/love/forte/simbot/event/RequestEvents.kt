/*
 *  Copyright (c) 2021-2022 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.event

import kotlinx.coroutines.runBlocking
import love.forte.simbot.Api4J
import love.forte.simbot.Bot
import love.forte.simbot.ExperimentalSimbotApi
import love.forte.simbot.definition.User
import love.forte.simbot.message.doSafeCast


/**
 *
 * 与 **请求** 有关的事件。
 *
 * 一个请求可能有两种情况：
 * - 外界向当前bot为某种目的而发出的**申请**。
 *      常见为一个入群申请、好友添加申请。
 *
 * - 外界向当前bot所发出的一种**邀请**。
 *      常见为对BOT的入群邀请。
 *
 *
 * @author ForteScarlet
 */
public interface RequestEvent : Event {
    override val metadata: Event.Metadata
    override val bot: Bot

    /**
     * 请求事件的可见范围。
     * 对于请求事件，可见范围普遍为 [Event.VisibleScope.INTERNAL] 或 [Event.VisibleScope.PRIVATE].
     */
    override val visibleScope: Event.VisibleScope


    /**
     * 此申请的类型。
     */
    public val type: Type

    /**
     * 是否同意/接受此次请求。
     */
    @ExperimentalSimbotApi
    public suspend fun accept(/*action: AcceptAction = AccDefault*/): Boolean

    @Api4J
    @ExperimentalSimbotApi
    public fun acceptBlocking(): Boolean = runBlocking { accept() }

    /**
     * 是否拒绝/回绝此次请求。
     */
    @ExperimentalSimbotApi
    public suspend fun reject(/*action: RejectAction = RejDefault*/): Boolean

    @Api4J
    @ExperimentalSimbotApi
    public fun rejectBlocking(): Boolean = runBlocking { reject() }


    public enum class Type {
        /**
         * 主动申请。
         */
        APPLICATION,

        /**
         * 邀请/被邀请。
         */
        INVITATION

    }


    public companion object Key : BaseEventKey<RequestEvent>("api.request") {
        override fun safeCast(value: Any): RequestEvent? = doSafeCast(value)
    }
}


/**
 * 一个与频道服务器相关的申请事件。
 */
public interface GuildRequestEvent : RequestEvent, GuildEvent {


    public companion object Key : BaseEventKey<GuildRequestEvent>(
        "api.guild_request", setOf(
            RequestEvent, GuildEvent
        )
    ) {
        override fun safeCast(value: Any): GuildRequestEvent? = doSafeCast(value)
    }
}

/**
 * 一个与群相关的请求事件。
 */
public interface GroupRequestEvent : RequestEvent, GroupEvent {

    public companion object Key : BaseEventKey<GroupRequestEvent>(
        "api.group_request", setOf(
            RequestEvent, GroupEvent
        )
    ) {
        override fun safeCast(value: Any): GroupRequestEvent? = doSafeCast(value)
    }
}

/**
 * 一个与频道相关的请求事件。
 */
public interface ChannelRequestEvent : RequestEvent, ChannelEvent {

    public companion object Key : BaseEventKey<ChannelRequestEvent>(
        "api.channel_request", setOf(
            RequestEvent, ChannelEvent
        )
    ) {
        override fun safeCast(value: Any): ChannelRequestEvent? = doSafeCast(value)
    }
}

/**
 * 一个与其他[用户][User]相关的请求事件
 */
public interface UserRequestEvent : RequestEvent, UserEvent {

    public companion object Key : BaseEventKey<UserRequestEvent>(
        "api.user_request", setOf(
            RequestEvent, UserEvent
        )
    ) {
        override fun safeCast(value: Any): UserRequestEvent? = doSafeCast(value)
    }
}
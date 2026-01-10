/*
 *     Copyright (c) 2022-2026. ForteScarlet.
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

package love.forte.simbot.component.kook.event

import love.forte.simbot.common.collectable.Collectable
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.time.Timestamp
import love.forte.simbot.component.kook.KookChatChannel
import love.forte.simbot.component.kook.KookGuild
import love.forte.simbot.component.kook.KookMember
import love.forte.simbot.event.*
import love.forte.simbot.kook.event.*
import love.forte.simbot.suspendrunner.STP
import love.forte.simbot.kook.event.Event as KEvent
import love.forte.simbot.kook.objects.User as KUser

/**
 * KOOK 的频道成员变更事件。
 *
 * 相关的 KOOK **原始**事件类型有：
 * - [ExitedChannelEventExtra]
 * - [JoinedChannelEventExtra]
 * - [JoinedGuildEventExtra]
 * - [ExitedGuildEventExtra]
 * - [UpdatedGuildMemberEventExtra]
 * - [SelfExitedGuildEventExtra]
 * - [SelfJoinedGuildEventExtra]
 *
 * 其中，[SelfExitedGuildEventExtra] 和 [SelfJoinedGuildEventExtra]
 * 代表为 BOT 自身作为成员的变动，
 * 因此会额外提供相对应的 [bot成员变动][KookBotMemberChangedEvent]
 * 事件类型来进行更精准的事件监听。
 *
 * ## 相关事件
 * ### 成员的频道变更事件
 * [KookMemberChannelChangedEvent] 事件及其子类型
 * [KookMemberExitedChannelEvent]、[KookMemberJoinedChannelEvent]
 * 代表了一个频道服务器中的某个群成员加入、离开某一个频道（通常为语音频道）的事件。
 *
 * ### 成员的服务器变更事件
 * [KookMemberGuildChangedEvent] 事件及其子类型
 * [KookMemberJoinedGuildEvent]、[KookMemberExitedGuildEvent]
 * 代表了一个频道服务器中有新群成员加入、旧成员离开此服务器的事件。
 *
 * ### 成员的信息变更事件
 * [KookMemberUpdatedEvent] 事件
 * 代表了一个成员的信息发生了变更的事件。
 *
 * ### Bot频道服务器事件
 * [KookBotMemberChangedEvent] 事件及其子类型
 * [KookBotSelfJoinedGuildEvent]、[KookBotSelfExitedGuildEvent]
 * 代表了当前bot加入新频道服务器、离开旧频道服务器的事件。
 *
 * @author forte
 */
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
@STP
public abstract class KookMemberChangedEvent : KookSystemEvent(), ChangeEvent {
    /**
     * 变更时间。
     */
    override val time: Timestamp
        get() = Timestamp.ofMilliseconds(sourceEvent.msgTimestamp)
}

// region member相关

// region 频道进出相关

/**
 * KOOK [成员变更事件][KookMemberChangedEvent] 中与**语音频道的进出**相关的变更事件。
 * 这类事件代表某人进入、离开某个语音频道 (`channel`)，而不代表成员进入、离开了当前的频道服务器（`guild`）。
 */
@OptIn(FuzzyEventTypeImplementation::class)
@STP
public abstract class KookMemberChannelChangedEvent : KookMemberChangedEvent(), OrganizationSourceEvent {
    /**
     * 事件涉及的频道信息。
     */
    abstract override suspend fun content(): KookMember

    /**
     * 事件涉及的频道。
     */
    public abstract suspend fun channel(): KookChatChannel

    /**
     * 事件发生的频道服务器。
     */
    abstract override suspend fun source(): KookGuild
}


/**
 * KOOK 成员加入(语音频道)事件。
 *
 * @see JoinedChannelEventExtra
 * @author forte
 */
public abstract class KookMemberJoinedChannelEvent : KookMemberChannelChangedEvent() {
    abstract override val sourceEvent: KEvent<JoinedChannelEventExtra>

    override val sourceBody: JoinedChannelEventBody
        get() = sourceEvent.extra.body
}

/**
 * KOOK 成员离开(语音频道)事件。
 *
 * @see ExitedChannelEventExtra
 * @author forte
 */
public abstract class KookMemberExitedChannelEvent : KookMemberChannelChangedEvent() {
    abstract override val sourceEvent: KEvent<ExitedChannelEventExtra>

    override val sourceBody: ExitedChannelEventBody
        get() = sourceEvent.extra.body
}
// endregion


// region 频道服务器进出

/**
 * KOOK [成员变更事件][KookMemberChangedEvent] 中与**频道服务器进出**相关的变更事件。
 * 这类事件代表某人加入、离开某个频道服务器。
 */
@OptIn(FuzzyEventTypeImplementation::class)
@STP
public abstract class KookMemberGuildChangedEvent : KookMemberChangedEvent() {
    /**
     * 涉及的相关频道服务器。
     */
    abstract override suspend fun content(): KookGuild
}


/**
 * KOOK 成员离开(频道)事件。
 *
 * @see ExitedGuildEventExtra
 * @author forte
 */
@STP
public abstract class KookMemberExitedGuildEvent : KookMemberGuildChangedEvent(), GuildMemberDecreaseEvent {
    abstract override val sourceEvent: KEvent<ExitedGuildEventExtra>

    override val sourceBody: ExitedGuildEventBody
        get() = sourceEvent.extra.body

    /**
     * 离开的成员。
     */
    abstract override suspend fun member(): KookMember

    /**
     * 涉及的频道服务器。
     */
    abstract override suspend fun content(): KookGuild
}

/**
 * KOOK 成员加入(频道)事件。
 *
 * @see JoinedGuildEventExtra
 * @author forte
 */
@STP
public abstract class KookMemberJoinedGuildEvent : KookMemberGuildChangedEvent(), GuildMemberIncreaseEvent {
    abstract override val sourceEvent: KEvent<JoinedGuildEventExtra>

    override val sourceBody: JoinedGuildEventBody
        get() = sourceEvent.extra.body

    /**
     * 涉及的相关频道服务器。
     */
    abstract override suspend fun content(): KookGuild

    /**
     * 加入的成员。
     */
    abstract override suspend fun member(): KookMember
}


// endregion


/**
 * KOOK 频道成员信息更新事件。
 *
 * @see UpdatedGuildMemberEventExtra
 * @author forte
 */
@OptIn(FuzzyEventTypeImplementation::class)
public abstract class KookMemberUpdatedEvent : KookMemberChangedEvent(), MemberChangeEvent, OrganizationSourceEvent {

    abstract override val sourceEvent: KEvent<UpdatedGuildMemberEventExtra>

    override val sourceBody: UpdatedGuildMemberEventBody
        get() = sourceEvent.extra.body

    /**
     * 发生变更前的用户信息。
     * 从变更前用户信息中的 [KookMember.source] 中取得。
     */
    public abstract val beforeSource: KUser

    /**
     * 涉及的相关频道服务器。
     */
    @STP
    abstract override suspend fun source(): KookGuild

    /**
     * 已经发生变更后的成员。
     */
    @STP
    abstract override suspend fun content(): KookMember
}


// endregion


// region bot相关

/**
 * 频道成员的变动事件中，变动本体为bot自身时的事件。
 * 对应 KOOK 原始事件的 [SelfExitedGuildEventExtra] 和 [SelfJoinedGuildEventExtra]。
 *
 * @see KookBotMemberChangedEvent
 *
 * @author forte
 */
@OptIn(FuzzyEventTypeImplementation::class)
@STP
public abstract class KookBotMemberChangedEvent : KookMemberChangedEvent(), GuildEvent {
    abstract override val sourceBody: BotSelfGuildEventBody

    /**
     * 涉及的相关频道服务器。
     */
    abstract override suspend fun content(): KookGuild
}


/**
 * KOOK BOT自身离开(频道)事件。
 *
 * @see SelfExitedGuildEventExtra
 * @author forte
 */
@OptIn(FuzzyEventTypeImplementation::class)
public abstract class KookBotSelfExitedGuildEvent : KookBotMemberChangedEvent(), GuildEvent {
    abstract override val sourceEvent: KEvent<SelfExitedGuildEventExtra>

    override val sourceBody: BotSelfGuildEventBody
        get() = sourceEvent.extra.body
}

/**
 * KOOK BOT自身加入(频道)事件。
 *
 * @see SelfJoinedGuildEventExtra
 * @author forte
 */
public abstract class KookBotSelfJoinedGuildEvent : KookBotMemberChangedEvent() {
    abstract override val sourceEvent: KEvent<SelfJoinedGuildEventExtra>

    override val sourceBody: BotSelfGuildEventBody
        get() = sourceEvent.extra.body
}
// endregion

/**
 * KOOK 用户在线状态变更相关事件的抽象父类。
 *
 * 涉及到的原始事件有：
 * - [GuildMemberOfflineEventExtra]
 * - [GuildMemberOnlineEventExtra]
 *
 * ## 变化主体
 * 此事件主体是事件中的 [用户ID][GuildMemberOnlineStatusChangedEventBody.userId]
 *
 * ## 子类型
 * 此事件是密封的，如果你只想监听某人的上线或下线中的其中一种事件，则考虑监听此事件类的具体子类型。
 *
 * @see KookMemberOnlineEvent
 * @see KookMemberOfflineEvent
 *
 * @author forte
 */
@OptIn(FuzzyEventTypeImplementation::class)
public sealed class KookUserOnlineStatusChangedEvent : KookSystemEvent(), ChangeEvent {
    /**
     * 变更时间。
     */
    override val time: Timestamp
        get() = Timestamp.ofMilliseconds(sourceEvent.msgTimestamp)

    abstract override val sourceBody: GuildMemberOnlineStatusChangedEventBody

    /**
     * 用户ID。
     *
     * @see GuildMemberOnlineStatusChangedEventBody.userId
     */
    public val userId: ID
        get() = sourceBody.userId.ID

    /**
     * 状态变化后，此用户是否为_在线_状态。
     */
    public abstract val isOnline: Boolean

    /**
     * 同 [isOnline]。
     */
    @STP
    override suspend fun content(): Boolean = isOnline

    /**
     * 此用户与当前bot所同处的频道服务器的id列表。
     *
     * @see GuildMemberOnlineStatusChangedEventBody.guilds
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public val guildIds: List<ID>
        get() = sourceBody.guilds.map { it.ID }

    /**
     * 通过 [guildIds] 信息获取各个ID对应的 [KookGuild] 实例。
     *
     * 注意：[guilds] 的数据不是根据 [guildIds] 立即生效的，
     * 而是在每次获取的时候读取缓存信息。
     * 因此可能存在获取不到的情况（例如在获取时某 guild 已经被删除），
     * [guilds] 序列中的元素可能无法与 [guildIds] 完全对应。
     *
     */
    public abstract val guilds: Collectable<KookGuild>
}


/**
 * [KookUserOnlineStatusChangedEvent] 对于用户上线的事件子类型。
 *
 */
public abstract class KookMemberOnlineEvent : KookUserOnlineStatusChangedEvent() {
    abstract override val sourceEvent: KEvent<GuildMemberOnlineEventExtra>

    override val sourceBody: GuildMemberOnlineStatusChangedEventBody
        get() = sourceEvent.extra.body

    /**
     * 此事件代表上线，[isOnline] == true.
     */
    override val isOnline: Boolean
        get() = true
}

/**
 * [KookUserOnlineStatusChangedEvent] 对于用户离线的事件子类型。
 *
 */
public abstract class KookMemberOfflineEvent : KookUserOnlineStatusChangedEvent() {
    abstract override val sourceEvent: KEvent<GuildMemberOfflineEventExtra>

    override val sourceBody: GuildMemberOnlineStatusChangedEventBody
        get() = sourceEvent.extra.body

    /**
     * 此事件代表下线，[isOnline] == false.
     */
    override val isOnline: Boolean
        get() = false
}

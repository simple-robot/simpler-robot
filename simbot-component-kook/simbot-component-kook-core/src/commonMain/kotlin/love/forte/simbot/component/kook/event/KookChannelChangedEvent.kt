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

package love.forte.simbot.component.kook.event

import love.forte.simbot.common.time.Timestamp
import love.forte.simbot.component.kook.KookCategoryChannel
import love.forte.simbot.component.kook.KookChatChannel
import love.forte.simbot.component.kook.KookGuild
import love.forte.simbot.component.kook.KookMember
import love.forte.simbot.event.ChangeEvent
import love.forte.simbot.event.ChannelEvent
import love.forte.simbot.event.FuzzyEventTypeImplementation
import love.forte.simbot.event.OrganizationSourceEvent
import love.forte.simbot.kook.event.*
import love.forte.simbot.kook.objects.SimpleChannel
import love.forte.simbot.suspendrunner.STP
import love.forte.simbot.kook.event.Event as KEvent

/**
 * KOOK 系统事件中与 _频道变更_ 相关的事件的simbot事件基准类。
 *
 * 涉及的 KOOK 原始事件 (的 [SystemExtra] 子类型) 有：
 * - [AddedChannelEventExtra]
 * - [UpdatedChannelEventExtra]
 * - [DeletedChannelEventExtra]
 *
 * @author ForteScarlet
 *
 * @see KookSystemEvent
 * @see ChangeEvent
 * @see OrganizationSourceEvent
 */
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public abstract class KookChannelChangedEvent : KookSystemEvent(), ChangeEvent, OrganizationSourceEvent {
    /**
     * 事件涉及子频道所属的频道服务器。
     */
    @STP
    abstract override suspend fun source(): KookGuild

    /**
     * @see KEvent.msgTimestamp
     */
    override val time: Timestamp
        get() = Timestamp.ofMilliseconds(sourceEvent.msgTimestamp)
}

/**
 * 某频道服务器中新增了一个频道后的事件。
 *
 * @see ChannelEvent
 * @see KookChannelChangedEvent
 * @see AddedChannelEventExtra
 */
@OptIn(FuzzyEventTypeImplementation::class)
public abstract class KookAddedChannelEvent : KookChannelChangedEvent(), ChannelEvent {

    /**
     * 原事件对象
     *
     * @see AddedChannelEventExtra
     */
    abstract override val sourceEvent: KEvent<AddedChannelEventExtra>

    /**
     * @see AddedChannelEventExtra.body
     */
    override val sourceBody: SimpleChannel
        get() = sourceEvent.extra.body

    /**
     * 操作者，即此频道的创建者。
     *
     * 创建者获取自 [sourceBody.userId][SimpleChannel.userId],
     * 如果在此事件实例化的过程中此人离开频道服务器导致内置缓存被清理，则可能得到null。
     *
     */
    public abstract val operator: KookMember?

    /**
     * 增加的频道。
     */
    @STP
    abstract override suspend fun content(): KookChatChannel

    /**
     * 事件涉及子频道所属的频道服务器。
     */
    @STP
    abstract override suspend fun source(): KookGuild
}

/**
 * 某频道发生了信息变更。
 *
 * @see UpdatedChannelEventExtra
 */
@OptIn(FuzzyEventTypeImplementation::class)
public abstract class KookUpdatedChannelEvent : KookChannelChangedEvent() {

    /**
     * @see UpdatedChannelEventExtra.body
     */
    override val sourceBody: SimpleChannel
        get() = sourceEvent.extra.body

    /**
     * @see UpdatedChannelEventExtra
     */
    abstract override val sourceEvent: KEvent<UpdatedChannelEventExtra>

    /**
     * 变更后的频道信息
     */
    @STP
    abstract override suspend fun content(): KookChatChannel
}

/**
 * 某频道被删除的事件。
 *
 * @see DeletedChannelEventExtra
 */
@OptIn(FuzzyEventTypeImplementation::class)
public abstract class KookDeletedChannelEvent : KookChannelChangedEvent(), ChannelEvent {
    abstract override val sourceEvent: KEvent<DeletedChannelEventExtra>

    override val sourceBody: DeletedChannelEventBody
        get() = sourceEvent.extra.body

    /**
     * 已经被删除的频道。
     */
    @STP
    abstract override suspend fun content(): KookChatChannel

    /**
     * 事件涉及子频道所属的频道服务器。
     */
    @STP
    abstract override suspend fun source(): KookGuild
}

/**
 * KOOK 系统事件中与 _频道分组变更_ 相关的事件的simbot事件基准类。
 *
 * 涉及的 KOOK 原始事件 (的 [SystemExtra] 子类型) 有：
 * - [AddedChannelEventExtra]
 * - [UpdatedChannelEventExtra]
 * - [DeletedChannelEventExtra]
 *
 * @author ForteScarlet
 *
 * @see KookSystemEvent
 * @see ChangeEvent
 * @see OrganizationSourceEvent
 */
@OptIn(FuzzyEventTypeImplementation::class)
public abstract class KookCategoryChangedEvent : KookSystemEvent(), ChangeEvent, OrganizationSourceEvent {
    /**
     * 事件涉及子频道所属的频道服务器。
     */
    @STP
    abstract override suspend fun source(): KookGuild

    /**
     * @see KEvent.msgTimestamp
     */
    override val time: Timestamp
        get() = Timestamp.ofMilliseconds(sourceEvent.msgTimestamp)
}

/**
 * 某频道服务器中新增了一个频道分组后的事件。
 *
 * @see AddedChannelEventExtra
 */
public abstract class KookAddedCategoryEvent : KookCategoryChangedEvent() {
    /**
     * 原事件对象
     *
     * @see AddedChannelEventExtra
     */
    abstract override val sourceEvent: KEvent<AddedChannelEventExtra>

    /**
     * @see AddedChannelEventExtra.body
     */
    override val sourceBody: SimpleChannel
        get() = sourceEvent.extra.body

    /**
     * 操作者，即此频道的创建者。
     *
     * 创建者获取自 [sourceBody.userId][SimpleChannel.userId],
     * 如果在此事件实例化的过程中此人离开频道服务器导致内置缓存被清理，则可能得到null。
     *
     */
    public abstract val operator: KookMember?

    /**
     * 增加的频道分组。
     */
    @STP
    abstract override suspend fun content(): KookCategoryChannel
}

/**
 * 某频道分组发生了信息变更。
 *
 * @see UpdatedChannelEventExtra
 */
public abstract class KookUpdatedCategoryEvent : KookCategoryChangedEvent() {

    /**
     * @see UpdatedChannelEventExtra.body
     */
    override val sourceBody: SimpleChannel
        get() = sourceEvent.extra.body

    /**
     * @see UpdatedChannelEventExtra
     */
    abstract override val sourceEvent: KEvent<UpdatedChannelEventExtra>

    /**
     * 变更后的频道分组信息
     */
    @STP
    abstract override suspend fun content(): KookCategoryChannel
}

/**
 * 某频道分组被删除的事件。
 *
 * @see DeletedChannelEventExtra
 */
@OptIn(FuzzyEventTypeImplementation::class)
public abstract class KookDeletedCategoryEvent : KookCategoryChangedEvent(), ChannelEvent {
    abstract override val sourceEvent: KEvent<DeletedChannelEventExtra>

    override val sourceBody: DeletedChannelEventBody
        get() = sourceEvent.extra.body

    /**
     * 已经被删除的频道分组。
     */
    @STP
    abstract override suspend fun content(): KookCategoryChannel

    /**
     * 事件涉及子频道所属的频道服务器。
     */
    @STP
    abstract override suspend fun source(): KookGuild
}









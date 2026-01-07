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

import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.kook.KookChatChannel
import love.forte.simbot.component.kook.KookGuild
import love.forte.simbot.component.kook.KookMember
import love.forte.simbot.event.ChangeEvent
import love.forte.simbot.event.ChannelEvent
import love.forte.simbot.event.FuzzyEventTypeImplementation
import love.forte.simbot.kook.event.PingEventExtraBody
import love.forte.simbot.kook.event.PinnedMessageEventExtra
import love.forte.simbot.kook.event.UnpinnedMessageEventExtra
import love.forte.simbot.message.MessageContent
import love.forte.simbot.suspendrunner.ST
import love.forte.simbot.suspendrunner.STP
import love.forte.simbot.kook.event.Event as KEvent


/**
 * 与频道消息置顶相关的事件。
 * 涉及的原始事件有：
 * - [PinnedMessageEventExtra]
 * - [UnpinnedMessageEventExtra]
 *
 */
@OptIn(FuzzyEventTypeImplementation::class)
public abstract class KookMessagePinEvent : KookChannelChangedEvent(), ChangeEvent, ChannelEvent {
    /**
     * 此事件涉及的频道信息。
     */
    @STP
    abstract override suspend fun content(): KookChatChannel

    /**
     * 事件涉及子频道所属的频道服务器。
     */
    @STP
    abstract override suspend fun source(): KookGuild

    /**
     * 此事件涉及的操作者。会通过 [operatorId] 获取。
     *
     * 假若在此事件触发前的瞬间此人离开频道，则可能造成无法获取的情况。
     */
    public abstract val operator: KookMember?

    /**
     * 涉及消息的ID
     */
    public abstract val msgId: ID

    /**
     * 操作人ID
     */
    public abstract val operatorId: ID

    /**
     * 涉及频道ID
     */
    public abstract val channelId: ID

    /**
     * 通过 [msgId] 查询这条被置顶的消息。
     */
    @ST
    public abstract suspend fun queryMsg(): MessageContent
}


/**
 *
 * 新消息置顶事件。
 * 代表一个新的消息被设置为了目标频道的置顶消息。
 *
 * @see PinnedMessageEventExtra
 */
public abstract class KookPinnedMessageEvent : KookMessagePinEvent() {
    abstract override val sourceEvent: KEvent<PinnedMessageEventExtra>

    override val sourceBody: PingEventExtraBody
        get() = sourceEvent.extra.body

    /**
     * 涉及消息的ID。
     */
    override val msgId: ID
        get() = sourceBody.msgId.ID

    /**
     * 操作者ID。
     */
    override val operatorId: ID
        get() = sourceBody.operatorId.ID

    /**
     * 频道ID。
     */
    override val channelId: ID
        get() = sourceBody.channelId.ID
}

/**
 *
 * 消息取消置顶事件。代表一个新的消息被设置为了目标频道的置顶消息。
 *
 * @see UnpinnedMessageEventExtra
 */
public abstract class KookUnpinnedMessageEvent : KookMessagePinEvent() {

    abstract override val sourceEvent: KEvent<UnpinnedMessageEventExtra>

    override val sourceBody: PingEventExtraBody
        get() = sourceEvent.extra.body

    override val msgId: ID
        get() = sourceBody.msgId.ID

    override val operatorId: ID
        get() = sourceBody.operatorId.ID

    override val channelId: ID
        get() = sourceBody.channelId.ID
}


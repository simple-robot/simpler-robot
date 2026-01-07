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
import love.forte.simbot.common.time.Timestamp
import love.forte.simbot.component.kook.message.KookMessageContent
import love.forte.simbot.component.kook.message.KookUpdatedMessageContent
import love.forte.simbot.event.FuzzyEventTypeImplementation
import love.forte.simbot.kook.event.*


/**
 * KOOK 系统事件中与 _消息更新_ 相关的事件的simbot事件基准类。
 *
 * 涉及的 KOOK 原始事件 (的 [SystemExtra] 子类型) 有：
 * - [UpdatedMessageEventExtra]
 * - [UpdatedPrivateMessageEventExtra]
 *
 * @author ForteScarlet
 *
 * @see KookSystemEvent
 */
@OptIn(FuzzyEventTypeImplementation::class)
public abstract class KookUpdatedMessageEvent : KookSystemEvent() {
    /**
     * 源事件
     */
    abstract override val sourceEvent: Event<SystemExtra>

    /**
     * 源事件中的事件体
     */
    abstract override val sourceBody: Any?

    /**
     * 被更新的消息的ID
     */
    public abstract val updatedMsgId: ID

    /**
     * 被更新后的消息的原始正文文本
     */
    public abstract val updatedContent: String

    /**
     * 由更新后的消息文本 [updatedContent] 解析得到的 [KookMessageContent]。
     */
    public abstract val updatedMessageContent: KookUpdatedMessageContent

    /**
     * 消息被更新的时间
     */
    abstract override val time: Timestamp
}


/**
 * KOOK中一个频道消息被更新的事件。
 *
 * @see KookDeletedMessageEvent
 *
 */
public abstract class KookUpdatedChannelMessageEvent : KookUpdatedMessageEvent() {
    /**
     * 频道消息被更新事件的源事件
     */
    abstract override val sourceEvent: Event<UpdatedMessageEventExtra>

    /**
     * [sourceEvent] 的事件体。
     *
     * @see UpdatedMessageEventExtra.Body
     */
    override val sourceBody: UpdatedMessageEventExtra.Body
        get() = sourceEvent.extra.body

    /**
     * 被更新的消息的ID
     */
    override val updatedMsgId: ID
        get() = sourceBody.msgId.ID

    /**
     * 消息更新时间。来自 [Event.msgTimestamp]
     */
    override val time: Timestamp
        get() = Timestamp.ofMilliseconds(sourceEvent.msgTimestamp)

    override val updatedContent: String
        get() = sourceBody.content

    override val updatedMessageContent: KookUpdatedMessageContent by lazy(LazyThreadSafetyMode.PUBLICATION) {
        KookUpdatedMessageContent(
            bot = bot,
            isDirect = false,
            chatCode = null,
            rawContent = sourceBody.content,
            msgId = sourceBody.msgId,
            mention = sourceBody.mention,
            mentionRoles = sourceBody.mentionRoles,
            isMentionAll = sourceBody.isMentionAll,
            isMentionHere = sourceBody.isMentionHere,
        )
    }
}

/**
 * KOOK中一个私聊消息被更新的事件。
 *
 * @see KookDeletedMessageEvent
 *
 */
public abstract class KookUpdatedPrivateMessageEvent : KookUpdatedMessageEvent() {
    /**
     * 私聊消息被更新事件的源事件
     */
    abstract override val sourceEvent: Event<UpdatedPrivateMessageEventExtra>

    /**
     * [sourceEvent] 的事件体。
     *
     * @see UpdatedPrivateMessageEventExtra.Body
     */
    override val sourceBody: UpdatedPrivateMessageEventExtra.Body
        get() = sourceEvent.extra.body

    /**
     * 被更新的消息的ID
     */
    override val updatedMsgId: ID
        get() = sourceBody.msgId.ID

    /**
     * 被更新消息的创建者ID
     */
    public val authorId: ID
        get() = sourceBody.authorId.ID

    /**
     * 被更新的消息的目标用户ID
     */
    public val targetId: ID
        get() = sourceBody.targetId.ID

    /**
     * 私聊 code
     */
    public val chatCode: String
        get() = sourceBody.chatCode

    /**
     * 消息更新时间。来自 [DeletedPrivateMessageEventExtra.Body.deletedAt]
     */
    override val time: Timestamp
        get() = Timestamp.ofMilliseconds(sourceBody.updatedAt)

    override val updatedContent: String
        get() = sourceBody.content

    override val updatedMessageContent: KookUpdatedMessageContent by lazy(LazyThreadSafetyMode.PUBLICATION) {
        KookUpdatedMessageContent(
            bot = bot,
            isDirect = true,
            chatCode = sourceBody.chatCode,
            rawContent = sourceBody.content,
            msgId = sourceBody.msgId,
            mention = emptyList(),
            mentionRoles = emptyList(),
            isMentionAll = false,
            isMentionHere = false,
        )
    }
}



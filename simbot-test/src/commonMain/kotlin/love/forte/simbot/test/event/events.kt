/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
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

package love.forte.simbot.test.event

import love.forte.simbot.ability.DeleteOption
import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.bot.Bot
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.UUID
import love.forte.simbot.common.time.Timestamp
import love.forte.simbot.event.BotEvent
import love.forte.simbot.event.Event
import love.forte.simbot.event.MessageEvent
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.message.MessageReceipt
import love.forte.simbot.test.bot.TestBot
import love.forte.simbot.test.message.TestMessageContent

/**
 * 用于测试的 [Event] 基类
 *
 */
public interface BaseTestEvent : Event {
    override val time: Timestamp
        get() = testTimestamp

    public companion object {
        @OptIn(ExperimentalSimbotAPI::class)
        public var testTimestamp: Timestamp = Timestamp.now()
    }
}

/**
 * 用于测试的 [Event] 实现
 */
public open class TestEvent(override val id: ID = UUID.random()) : Event, BaseTestEvent


/**
 * 用于测试的 [MessageEvent] 实现
 */
public open class TestMessageEvent(
    override var id: ID = UUID.random(),
    override var bot: Bot = TestBot(),
    override var authorId: ID = UUID.random(),
    override var messageContent: MessageContent = TestMessageContent(),
    public var onReplyText: (String) -> TestMessageReceipt = { TestMessageReceipt() },
    public var onReplyMessage: (Message) -> TestMessageReceipt = { TestMessageReceipt() },
    public var onReplyMessageContent: (MessageContent) -> TestMessageReceipt = { TestMessageReceipt() },
) : MessageEvent, BaseTestEvent {
    override suspend fun reply(text: String): TestMessageReceipt =
        onReplyText(text)

    override suspend fun reply(message: Message): TestMessageReceipt =
        onReplyMessage(message)

    override suspend fun reply(messageContent: MessageContent): TestMessageReceipt =
        onReplyMessageContent(messageContent)
}

public open class TestMessageReceipt(public var onDelete: (Array<out DeleteOption>) -> Unit = {}) : MessageReceipt {
    override suspend fun delete(vararg options: DeleteOption) {
        onDelete(options)
    }

}

/**
 * 用于测试的 [BotEvent] 实现
 */
public open class TestBotEvent(override val id: ID, override val bot: Bot) : BotEvent,
    BaseTestEvent

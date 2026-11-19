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

package ability

import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import love.forte.simbot.ability.ReplyOption
import love.forte.simbot.ability.ReplySupport
import love.forte.simbot.ability.StandardReplyOption
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.message.MessageReceipt
import love.forte.simbot.message.Text
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

/**
 * 验证回复接口兼容重载对标准选项的保证。
 */
class ReplySupportTests {
    /**
     * 不支持标准选项的实现应在发送前失败，组件扩展选项仍保留原来的兼容行为。
     */
    @Test
    fun defaultOverloadsRejectNoReferenceBeforeDelegating() = runTest {
        val receipt = mockk<MessageReceipt>()
        val sent = mutableListOf<Any>()
        val replier = object : ReplySupport {
            override suspend fun reply(text: String): MessageReceipt = receipt.also { sent += text }
            override suspend fun reply(message: Message): MessageReceipt = receipt.also { sent += message }
            override suspend fun reply(messageContent: MessageContent): MessageReceipt =
                receipt.also { sent += messageContent }

            override suspend fun reply(
                text: String,
                vararg options: ReplyOption
            ): MessageReceipt {
                TODO("Not yet implemented")
            }

            override suspend fun reply(
                message: Message,
                vararg options: ReplyOption
            ): MessageReceipt {
                TODO("Not yet implemented")
            }

            override suspend fun reply(
                messageContent: MessageContent,
                vararg options: ReplyOption
            ): MessageReceipt {
                TODO("Not yet implemented")
            }
        }
        val message = Text.of("message")
        val content = mockk<MessageContent>()
        val noReference = StandardReplyOption.NO_REFERENCE

        assertFailsWith<UnsupportedOperationException> { replier.reply("text", noReference) }
        assertFailsWith<UnsupportedOperationException> { replier.reply(message, noReference) }
        assertFailsWith<UnsupportedOperationException> { replier.reply(content, noReference) }
        assertEquals(emptyList(), sent)

        val componentOption = object : ReplyOption {}
        assertSame(receipt, replier.reply("text", componentOption))
        assertSame(receipt, replier.reply(message, componentOption))
        assertSame(receipt, replier.reply(content, componentOption))
        assertEquals(listOf("text", message, content), sent)
    }
}

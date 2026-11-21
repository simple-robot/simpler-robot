/*
 *     Copyright (c) 2026. ForteScarlet.
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

package love.forte.simbot.component.kook.message

import io.ktor.client.engine.mock.*
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import love.forte.simbot.ability.SendOption
import love.forte.simbot.component.kook.KookComponent
import love.forte.simbot.component.kook.bot.KookBotConfiguration
import love.forte.simbot.component.kook.bot.internal.KookBotImpl
import love.forte.simbot.event.Event
import love.forte.simbot.event.EventProcessor
import love.forte.simbot.event.EventResult
import love.forte.simbot.event.InteractionMessage
import love.forte.simbot.interception.InterceptorManager
import love.forte.simbot.kook.stdlib.BotConfiguration
import love.forte.simbot.kook.stdlib.BotFactory
import love.forte.simbot.kook.stdlib.Ticket
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertSame

class KookMessageSendContextTests {
    @Test
    fun targetEqualityIncludesTargetType() {
        val first = listOf(
            KookMessageSendTarget.channel("id"),
            KookMessageSendTarget.user("id"),
            KookMessageSendTarget.chatCode("id"),
        )
        val second = listOf(
            KookMessageSendTarget.channel("id"),
            KookMessageSendTarget.user("id"),
            KookMessageSendTarget.chatCode("id"),
        )
        assertEquals(3, first.toSet().size)
        first.zip(second).forEach { (left, right) ->
            assertEquals(left, right)
            assertEquals(left.hashCode(), right.hashCode())
            assertEquals(left.toString(), right.toString())
        }
        assertNotEquals(KookMessageSendTarget.channel("id"), KookMessageSendTarget.channel("other"))
    }

    @Test
    fun factoryAndCopiesSnapshotOptionsAndRetainOriginalState() = runTest {
        val engine = MockEngine { error("No HTTP request expected") }
        val configuration = BotConfiguration().apply {
            clientEngine = engine
            wsEngine = engine
            coroutineContext = backgroundScope.coroutineContext
        }
        val bot = KookBotImpl(
            object : EventProcessor {
                override fun push(event: Event) = emptyFlow<EventResult>()
            },
            BotFactory.create(Ticket.botWsTicket("bot", "token"), configuration),
            KookComponent(),
            KookBotConfiguration(configuration),
            InterceptorManager(),
        )
        val source = Any()
        val message = InteractionMessage.valueOf("original")
        val destination = KookMessageSendTarget.channel("channel")
        val option = KookChannelMessageSendOption.nonce("original")
        val options = mutableListOf<SendOption>(option)
        val initial = KookMessageSendContext.create(bot, source, message, destination, options)
        options.clear()
        assertEquals(listOf(option), initial.options)
        assertSame(message, initial.originalMessage)
        assertSame(message, initial.message)

        val replacement = InteractionMessage.valueOf("replacement")
        val newOption = KookChannelMessageSendOption.nonce("replacement")
        val newOptions = mutableListOf<SendOption>(newOption)
        val copy = initial.withMessage(replacement).withOptions(newOptions)
        newOptions.clear()
        assertEquals(listOf(newOption), copy.options)
        assertSame(bot, copy.bot)
        assertSame(source, copy.source)
        assertSame(message, copy.originalMessage)
        assertSame(replacement, copy.message)
        assertSame(destination, copy.destination)
        assertEquals(null, copy.target())
        assertEquals(listOf(option), initial.options)
    }
}

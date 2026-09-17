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

package love.forte.simbot.component.qgguild.test

import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import love.forte.simbot.common.atomic.atomic
import love.forte.simbot.common.id.UUID
import love.forte.simbot.component.qguild.event.QGGroupMessageCreateEventPreReplyEvent
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.event.QGGroupMessageCreateEventImpl
import love.forte.simbot.event.ChatGroupMessageEventPreReplyEvent
import love.forte.simbot.event.InteractionMessage
import love.forte.simbot.qguild.event.GroupMessageCreate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 *
 * @author ForteScarlet
 */
class QGGroupMessageCreateInteractionTests : AbstractInteractionTests() {

    private fun event(bot: QGBotImpl): QGGroupMessageCreateEventImpl {
        val sourceEvent = GroupMessageCreate(
            id = "event-id",
            s = 1,
            data = GroupMessageCreate.Data(
                id = "message-id",
                author = GroupMessageCreate.Author("member-openid"),
                content = "source",
                timestamp = "2023-11-06T13:37:18+08:00",
                groupOpenid = "group-openid"
            )
        )

        return QGGroupMessageCreateEventImpl(bot, "{}", sourceEvent, sourceEvent.id)
    }

    private suspend fun MockRequestHandleScope.respondSendOk(req: HttpRequestData): HttpResponseData {
        val body = req.body.toByteArray().decodeToString()
        val json = Json.decodeFromString(JsonElement.serializer(), body).jsonObject
        assertEquals("TEXT", json["content"]?.jsonPrimitive?.content)
        assertEquals("message-id", json["msg_id"]?.jsonPrimitive?.content)

        return respondOk(
            """
                {"id":"${UUID.random()}","timestamp":1698435200}
            """.trimIndent()
        )
    }

    @Test
    fun testQGGroupMessageCreateReplyInteraction() = runTest {
        testInteractionEvent<_, QGGroupMessageCreateEventPreReplyEvent>(
            mockClient = { respondSendOk(it) },
            prepare = {
                atomic(false) to event(it)
            },
            listener = { (called, event), e, _, _ ->
                called.value = true
                assertIs<ChatGroupMessageEventPreReplyEvent>(e)
                assertEquals(event, e.content)
                val message = e.message
                assertIs<InteractionMessage.Text>(message)
                assertEquals("TEXT", message.text)
            }
        ) { (called, event), _ ->
            event.reply("TEXT")
            assertTrue(called.value)
        }
    }
}

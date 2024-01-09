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

import kotlinx.serialization.json.Json
import love.forte.simbot.common.id.IntID.Companion.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.id.UUID
import love.forte.simbot.message.*
import love.forte.simbot.message.OfflineImage.Companion.toOfflineImage
import love.forte.simbot.resource.toResource
import kotlin.test.Test
import kotlin.test.assertEquals

class MessagesTests {

    @Test
    fun standardMessageSerializationTest() {
        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
            serializersModule = Messages.standardSerializersModule
            prettyPrint = true
        }

        val messages = buildMessages {
            add("Hello".toText())
            add(AtAll)
            add(At(1.ID))
            add(UUID.random().toString().encodeToByteArray().toResource().toOfflineImage())
            add(UUID.random().toString().encodeToByteArray().toResource().toOfflineResourceImage())
            add(Face("FACE".ID))
            add(Emoji(UUID.random()))
            add(RemoteIDImage(UUID.random()))

            addIntoMessages()

            add("World".toText())
        }

        val jsonStr = json.encodeMessagesToString(messages)

        println(jsonStr)

        val decodedMessages = json.decodeMessagesFromString(jsonStr)

        assertEquals(messages.size, decodedMessages.size)
        assertEquals(messages, decodedMessages)
    }


}

internal expect fun MessagesBuilder.addIntoMessages()

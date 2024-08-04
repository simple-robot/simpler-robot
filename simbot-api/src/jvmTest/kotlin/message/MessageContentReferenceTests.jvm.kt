/*
 *     Copyright (c) 2024. ForteScarlet.
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

package message

import io.mockk.coVerify
import io.mockk.every
import io.mockk.spyk
import kotlinx.coroutines.runBlocking
import love.forte.simbot.message.MessageContent
import love.forte.simbot.message.emptyMessages
import kotlin.test.Test


/**
 *
 * @author ForteScarlet
 */
class MessageContentReferenceTests {

    @Test
    fun contentReferenceTest() {
        val content = spyk<MessageContent>()
        every { content.messages } returns emptyMessages()

        runBlocking { content.reference() }
        coVerify { content.messages }
    }

}

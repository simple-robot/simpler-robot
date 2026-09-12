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

package test

import io.ktor.http.*
import love.forte.simbot.qguild.api.message.DeleteChannelMessageApi
import love.forte.simbot.qguild.api.message.group.GroupMessageDeleteApi
import love.forte.simbot.qguild.api.message.user.UserMessageDeleteApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MessageDeleteApiTests {
    @Test
    fun deleteApisUseTheirDocumentedResources() {
        val channel = DeleteChannelMessageApi.create("channel-openid", "message-id", true)
        val group = GroupMessageDeleteApi.create("group-openid", "message-id")
        val user = UserMessageDeleteApi.create("user-openid", "message-id")

        assertEquals(HttpMethod.Delete, channel.method)
        assertEquals("/channels/channel-openid/messages/message-id", channel.url.encodedPath)
        assertEquals("true", channel.url.parameters["hidetip"])
        assertNull(channel.body)
        assertEquals("/v2/groups/group-openid/messages/message-id", group.url.encodedPath)
        assertNull(group.body)
        assertEquals("/v2/users/user-openid/messages/message-id", user.url.encodedPath)
        assertNull(user.body)
    }
}

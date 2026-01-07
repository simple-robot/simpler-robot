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

package love.forte.simbot.component.onebot.v11.core.utils

import love.forte.simbot.common.id.IntID.Companion.ID
import love.forte.simbot.component.onebot.v11.message.segment.OneBotAt
import love.forte.simbot.component.onebot.v11.message.segment.OneBotDice
import love.forte.simbot.component.onebot.v11.message.segment.OneBotReply
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertIs


/**
 *
 * @author ForteScarlet
 */
class ReplyMessageSegmentListTests {

    @Test
    fun resolveReplyMessageSegmentListWithoutReplyTest() {
        val list = listOf(
            OneBotAt.create("1"),
            OneBotAt.createAtAll(),
            OneBotDice,
        )

        val newList = resolveReplyMessageSegmentList(list, 0.ID)

        assertEquals(4, newList.size)
        assertIs<OneBotReply>(newList.first())
        assertContentEquals(list, newList.subList(1, newList.size))
    }

    @Test
    fun resolveReplyMessageSegmentListWithReplyTest() {
        val list = listOf(
            OneBotAt.create("1"),
            OneBotReply.create(10.ID),
            OneBotDice,
        )

        val newList = resolveReplyMessageSegmentList(list, 0.ID)

        println(newList)

        assertEquals(3, newList.size)
        assertContentEquals(list, newList)
    }

}

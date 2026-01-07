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

package love.forte.simbot.component.onebot.v11.message

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import love.forte.simbot.common.id.IntID.Companion.ID
import love.forte.simbot.component.onebot.v11.message.segment.OneBotFace
import love.forte.simbot.component.onebot.v11.message.segment.OneBotMessageSegment
import love.forte.simbot.component.onebot.v11.message.segment.OneBotMessageSegmentSerializer
import love.forte.simbot.component.onebot.v11.message.segment.OneBotText
import love.forte.simbot.message.messageElementPolymorphic
import kotlin.test.Test
import kotlin.test.assertEquals


/**
 *
 * @author ForteScarlet
 */
class ListElementSerializationTests {
    @Suppress("VariableNaming")
    private val defaultJson = Json {
        isLenient = true
        isLenient = true
        ignoreUnknownKeys = true
        allowSpecialFloatingPointValues = true
        prettyPrint = false
        serializersModule = SerializersModule {
            messageElementPolymorphic {
                includeAllComponentMessageElementImpls()
            }
            polymorphic(OneBotMessageElement::class) {
                includeAllComponentMessageElementImpls()
            }
            polymorphic(OneBotMessageSegment::class) {
                includeAllOneBotSegmentImpls()
            }
        }
    }

    @Test
    fun listSegmentTest() {
        val elementList: List<OneBotMessageSegment> = listOf(
            OneBotText.create("Text"),
            OneBotFace.create(123.ID),
        )

        val jsonString = defaultJson.encodeToString(OneBotMessageSegmentSerializer, elementList)
        assertEquals(
            """[{"type":"text","data":{"text":"Text"}},{"type":"face","data":{"id":"123"}}]""",
            jsonString
        )
    }

}

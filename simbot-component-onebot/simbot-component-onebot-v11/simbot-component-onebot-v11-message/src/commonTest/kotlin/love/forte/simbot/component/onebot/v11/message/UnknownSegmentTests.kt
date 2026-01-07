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

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import love.forte.simbot.annotations.FragileSimbotAPI
import love.forte.simbot.component.onebot.v11.message.segment.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs


/**
 *
 * @author ForteScarlet
 */
class UnknownSegmentTests {
    @OptIn(ExperimentalSerializationApi::class, FragileSimbotAPI::class)
    val json = Json {
        isLenient = true
        ignoreUnknownKeys = true
        serializersModule = SerializersModule {
            polymorphic(OneBotMessageSegment::class) {
                includeAllOneBotSegmentImpls()
                defaultDeserializer { OneBotUnknownSegmentDeserializer }
            }
            polymorphicDefaultSerializer(OneBotMessageSegment::class) { base ->
                if (base is OneBotUnknownSegment) {
                    OneBotUnknownSegmentPolymorphicSerializer(base.type)
                } else {
                    null
                }
            }
        }
        prettyPrint = true
        prettyPrintIndent = "\t"
    }


    @OptIn(FragileSimbotAPI::class)
    @Test
    fun decodeTest() {
        val segment = json.decodeFromString(
            PolymorphicSerializer(OneBotMessageSegment::class),
            """
            {
		"data": {
			"file": "1718187789891.mp4",
			"path": "",
			"file_id": "/d5e14a94-86be-49fd-abe3-c03817b5ac27",
			"file_size": "2808485"
		},
		"type": "file"
	}
        """.trimIndent()
        )

        assertIs<OneBotUnknownSegment>(segment)

        val obj = json.encodeToJsonElement(PolymorphicSerializer(OneBotMessageSegment::class), segment)
        val objType = obj.jsonObject["type"]?.jsonPrimitive?.content
        assertEquals("file", objType)
    }


}

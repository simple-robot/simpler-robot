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

package love.forte.simbot.component.onebot.v11.core.bot

import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.overwriteWith
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import love.forte.simbot.component.onebot.v11.core.OneBot11
import love.forte.simbot.component.onebot.v11.message.OneBotMessageElement
import love.forte.simbot.component.onebot.v11.message.segment.DefaultOneBotMessageSegmentElement
import love.forte.simbot.component.onebot.v11.message.segment.OneBotRps
import kotlin.test.Test
import kotlin.test.assertContentEquals


/**
 *
 * @author ForteScarlet
 */
class DecoderJsonTests {

    @Test
    fun decoderJsonOverwriteWithIncludeTest() {
        val newSerializersModule = SerializersModule {
            include(OneBot11.serializersModule)
            polymorphic(OneBotMessageElement::class) {
                subclass(TestOneBotMessageElementImpl.serializer())
            }
        }

        val json = Json(OneBot11.DefaultJson) {
            serializersModule = serializersModule overwriteWith newSerializersModule
        }

        doTest(json)
    }

    @Test
    fun decoderJsonOverwriteWithoutIncludeTest() {
        val newSerializersModule = SerializersModule {
            // include(OneBot11.serializersModule)
            polymorphic(OneBotMessageElement::class) {
                subclass(TestOneBotMessageElementImpl.serializer())
            }
        }

        val json = Json(OneBot11.DefaultJson) {
            serializersModule = serializersModule overwriteWith newSerializersModule
        }

        doTest(json)
    }


    private fun doTest(json: Json) {
        val list = listOf(
            DefaultOneBotMessageSegmentElement(OneBotRps),
            TestOneBotMessageElementImpl
        )

        val serializer = ListSerializer(PolymorphicSerializer(OneBotMessageElement::class))
        val jsonStr = json.encodeToString(serializer, list)
        val decoded = json.decodeFromString(serializer, jsonStr)

        assertContentEquals(list, decoded)
    }
}

@Serializable
private object TestOneBotMessageElementImpl : OneBotMessageElement

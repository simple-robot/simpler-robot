import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.overwriteWith
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kotlin.test.Test
import kotlin.test.assertIs

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

@Serializable
abstract class Base

@Serializable
@SerialName("T1")
class T1 : Base()

@Serializable
@SerialName("T2")
class T2 : Base()

class SerTest {
    @Test
    fun serialTest() {
        // My distribution
        val defaultModule = SerializersModule {
            polymorphic(Base::class) {
                subclass(T1.serializer())
                subclass(T2.serializer())
                defaultDeserializer { T2.serializer() }
            }
        }
        // Clients
        @Serializable
        @SerialName("T3")
        class T3 : Base()

        @Serializable
        @SerialName("T4")
        class T4 : Base()

        val clientModule = defaultModule overwriteWith SerializersModule {
            polymorphic(Base::class) {
                subclass(T3.serializer())
                subclass(T4.serializer())
                defaultDeserializer { T4.serializer() }
            }
        }

        val json = Json {
            isLenient = true
            serializersModule = clientModule
        }

        assertIs<T4>(json.decodeFromString(Base.serializer(), """{}"""))
        assertIs<T1>(json.decodeFromString(Base.serializer(), """{"type": "T1"}"""))
        assertIs<T2>(json.decodeFromString(Base.serializer(), """{"type": "T2"}"""))
        assertIs<T3>(json.decodeFromString(Base.serializer(), """{"type": "T3"}"""))
    }
}

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

import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.*
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.subclass
import kotlinx.serialization.properties.Properties
import love.forte.simbot.bot.SerializableBotConfiguration
import love.forte.simbot.bot.serializableBotConfigurationPolymorphic
import kotlin.coroutines.*
import kotlin.test.Test
import kotlin.test.assertEquals


class BotConfigurationSerTests {

    @Serializable
    @SerialName("test")
    data class TestImpl(
        val name: String,
        val size: Int,
    ) : SerializableBotConfiguration()

    /**
     * 一个未实装的测试，尝试探索不通过 `JsonClassDiscriminator`、可以支持更多格式的配置文件的反序列化处理方案。
     */
    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun testJson() {
        val testImpl = TestImpl("forte", 1)

        val json = Json {
            // isLenient = true
            ignoreUnknownKeys = true
            serializersModule = SerializersModule {
                serializableBotConfigurationPolymorphic {
                    subclass(TestImpl.serializer())
                }
            }
        }

        val configFromJson = json.decodeWithComponentId {
            // language=JSON
            json.decodeFromString(it, """{"component":"test","name":"forte","size":1}""")
        }

        assertEquals(testImpl, configFromJson)

        val prop = Properties(
            SerializersModule {
                serializableBotConfigurationPolymorphic {
                    subclass(TestImpl.serializer())
                }
            }
        )
        val map = buildMap {
            put("component", "test")
            put("name", "forte")
            put("size", "1")
        }
        val configFromMap = prop.decodeWithComponentId {
            prop.decodeFromStringMap(it, map)
        }
        assertEquals(configFromMap, testImpl)
    }
}

@OptIn(ExperimentalSerializationApi::class)
fun SerialFormat.decodeWithComponentId(
    decoder: (DeserializationStrategy<SerializableBotConfiguration>) -> SerializableBotConfiguration,
): SerializableBotConfiguration {
    val func: (suspend () -> String?) = {
        println("fun1")
        suspendCancellableCoroutine { c ->
            println("fun2")
            val ser = ComponentIdExtractSerializer(c)
            decoder(ser)
        }.also {
            println("fun3: $it")
        }
    }

    var res: Result<String?>? = null

    func.startCoroutine(object : Continuation<String?> {
        override val context: CoroutineContext get() = EmptyCoroutineContext

        override fun resumeWith(result: Result<String?>) {
            println("result: $result")
            res = result
        }
    })

    val str = requireNotNull(res).getOrThrow()

    val polSer = serializersModule.getPolymorphic(SerializableBotConfiguration::class, str)
        ?: throw IllegalArgumentException("Unknown component id: $str")

    return decoder(polSer)
}

class ComponentIdExtractSerializer(
    private val continuation: CancellableContinuation<String?>,
) : DeserializationStrategy<SerializableBotConfiguration> {
    override val descriptor: SerialDescriptor by lazy(LazyThreadSafetyMode.PUBLICATION) {
        buildClassSerialDescriptor(
            "love.forte.simbot.ComponentIdExtractSerializer",
        ) {
            element("component", String.serializer().descriptor)
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): SerializableBotConfiguration {
        kotlin.runCatching {
            decoder.decodeStructure(descriptor) {
                if (decodeSequentially()) {
                    val serialName = decodeStringElement(descriptor, 0)
                    println("decodeStringElement decodeSequentially(0)")
                    continuation.resume(serialName)
                } else {
                    var serialName: String? = null
                    mainLoop@ while (true) {
                        when (val index = decodeElementIndex(descriptor)) {
                            CompositeDecoder.DECODE_DONE -> {
                                break@mainLoop
                            }

                            0 -> {
                                serialName = decodeStringElement(descriptor, index)
                            }

                            else -> {
                                // Skip all
                            }
                        }
                    }

                    continuation.resume(serialName)
                }
            }
        }.getOrElse { e ->
            if (continuation.isActive) {
                continuation.resumeWithException(e)
            }
        }

        return FakeSerializableBotConfiguration
    }

    private object FakeSerializableBotConfiguration : SerializableBotConfiguration()
}

/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.test

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.subclass
import kotlinx.serialization.properties.Properties
import kotlinx.serialization.protobuf.ProtoBuf
import love.forte.simbot.ID
import love.forte.simbot.message.At
import love.forte.simbot.message.Messages
import love.forte.simbot.message.Text
import love.forte.simbot.message.messages
import kotlin.test.BeforeTest
import kotlin.test.Test


/**
 *
 * @author ForteScarlet
 */
class MessageSerializerTest {
    private val a1 = At(1145141919810.ID)
    private val a2 = At(1149159218.ID)
    private val t1 = Text { "hi" }
    private val messages = messages(a1, t1, a2)

    @BeforeTest
    fun pre() {
        Messages.registrar {
            subclass(At.serializer())
        }

        println(messages)
    }

    @Test
    fun messageJsonTest() {
        val json = Json { serializersModule = Messages.serializersModule }
        val jsonStr = json.encodeToString(Messages.serializer, messages)
        println(jsonStr)
        val messagesDecoded: Messages = json.decodeFromString(Messages.serializer, jsonStr)
        println(messagesDecoded)
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun messagePropertiesTest() {
        val prop = Properties(Messages.serializersModule)
        val map = prop.encodeToMap(Messages.serializer, messages)
        println(map)
        val messagesDecoded: Messages = prop.decodeFromMap(Messages.serializer, map)
        println(messagesDecoded)
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun messageProtobufTest() {
        val pb = ProtoBuf {
            serializersModule = Messages.serializersModule
        }
        val byteArray = pb.encodeToByteArray(Messages.serializer, messages)
        println(byteArray)

        val messagesDecoded: Messages = pb.decodeFromByteArray(Messages.serializer, byteArray)
        println(messagesDecoded)
    }

}

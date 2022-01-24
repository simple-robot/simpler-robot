/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *
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

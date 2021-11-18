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
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.subclass
import kotlinx.serialization.properties.Properties
import kotlinx.serialization.protobuf.ProtoBuf
import love.forte.simbot.SimbotComponent
import love.forte.simbot.message.*
import kotlin.reflect.KClass
import kotlin.test.BeforeTest
import kotlin.test.Test


@Serializable
@SerialName("test.at")
class At(override val target: Long) : love.forte.simbot.message.At<Long, At> {
    override val key: Message.Key<At> get() = Key

    companion object Key : AbstractKey<At>(SimbotComponent, castFunc = { doSafeCast<At>(it) }) {
        override val elementType: KClass<At> get() = At::class
    }

    override fun toString(): String = "At(target=$target)"


    override fun equals(other: Any?): Boolean {
        return when (other) {
            other === this -> true
            is At -> target == other.target
            else -> false
        }
    }

    override fun hashCode(): Int = target.hashCode()
}


/**
 *
 * @author ForteScarlet
 */
class MessageSerializerTest {
    private val a1 = At(1145141919810)
    private val a2 = At(1149159218)
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

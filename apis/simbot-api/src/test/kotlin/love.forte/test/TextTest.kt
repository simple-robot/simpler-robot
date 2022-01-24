/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.test

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import love.forte.simbot.message.*
import kotlin.test.Test

/**
 *
 * @author ForteScarlet
 */
class TextTest {

    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun textTest() {
        Messages.registrar {
        }

        val t1 = Text()
        val t2 = Text { "Abc" }
        val t3 = "23333".toText()

        println(t1 + t2)
        println(t2 + t3)
        println(t1 + t2 + t3 + ", Hello World")


        println(Json.encodeToString(t3 + ", Mua~"))

        val messages = listOf(t3, t3, t3, t3).toMessages()


        println(messages)

        val list: List<Message.Element<Text>> = listOf(t3, t3, t3, t3)

        val json = Json { serializersModule = Messages.serializersModule }

        // ListSerializer()

        println(json.encodeToString(list))
        println(json.encodeToString(Messages.Companion.serializer, messages))

        // println(Json.encodeToString(ListSerializer(ContextualSerializer(Message.Element::class)), messages))

    }

}
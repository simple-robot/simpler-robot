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
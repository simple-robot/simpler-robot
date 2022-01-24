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
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.properties.Properties
import kotlinx.serialization.properties.encodeToStringMap
import java.io.StringReader
import kotlin.test.Test

/**
 *
 * @author ForteScarlet
 */
class PropSerializerTest {

    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun test() {
        val p = Properties(SerializersModule { })

        val jp = java.util.Properties().also {
            it.load(StringReader(properties))
        }

        println(jp)
        val strMap = mutableMapOf<String, String>()
        for (mutableEntry in jp) {
            strMap[mutableEntry.key.toString()] = mutableEntry.value.toString()
        }


        val i = VerifyInfo("A", "B", "C")
        val map = p.encodeToStringMap(i)
        println(map)

        val info = p.decodeFromStringMap(
            VerifyInfo.serializer(),
            strMap
            // jp.mapKeys { it.toString().trim() }.mapValues { it.toString().trim() }
        )

        println(info)
    }
}

val properties = """
appId=APP_ID
appKey=APP_KEY
token=APP_ID.TOKEN
""".trimIndent()

@Serializable
data class VerifyInfo(
    val appId: String,
    val appKey: String,
    val token: String
)
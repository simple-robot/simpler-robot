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
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.properties.Properties
import kotlinx.serialization.properties.encodeToStringMap
import org.junit.Test
import java.io.StringReader

/**
 *
 * @author ForteScarlet
 */
class PropSerializerTest {

    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun test(){
        val p = Properties(SerializersModule {  })

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

        val info = p.decodeFromStringMap(VerifyInfo.serializer(),
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
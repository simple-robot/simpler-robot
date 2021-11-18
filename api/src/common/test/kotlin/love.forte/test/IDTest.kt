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

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import love.forte.simbot.*
import kotlin.test.Test

@Serializable
data class User(
    val id: DoubleID,
    val age: Int,
)

/**
 *
 * @author ForteScarlet
 */
class IDTest {

    @Test
    fun idSerializerTest() {
        // val user = User(100L.ID, 24)
        val user = User(5.2355.ID, 24)

        println(user)

        val json = Json
        println(json.encodeToString(user))


    }


}
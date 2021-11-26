/*
 *  Copyright (c) 2021 ForteScarlet <https://github.com/ForteScarlet>
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

import love.forte.simbot.AttributeHashMap
import love.forte.simbot.attribute
import love.forte.simbot.set
import kotlin.test.Test


/**
 *
 * @author ForteScarlet
 */
class AttributeTest {

    private val map = AttributeHashMap()

    @Test
    fun test() {
        val attr1 = attribute<User1>("user")
        val attr2 = attribute<Foo>("foo")

        map[attr1] = User1("Forte")
        map[attr2] = Foo(2)

        println(map)
    }


}


data class User1(val name: String)
data class Foo(val age: Int)
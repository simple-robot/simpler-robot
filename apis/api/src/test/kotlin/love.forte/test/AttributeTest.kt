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


    @Test
    fun test() {
        val fooAttr = attribute<Foo>("foo")

        val map = AttributeHashMap()
        val foo = Foo()
        map[fooAttr] = foo
        val foo1 = map[fooAttr]!!
        val foo2 = map[attribute<Foo>("foo")]!! // by a new instance

        println(foo1 === foo2)
    }


}


class Foo
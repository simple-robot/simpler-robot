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

import love.forte.simbot.AttributeMutableMap
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

        val map = AttributeMutableMap()
        val foo = Foo()
        map[fooAttr] = foo
        val foo1 = map[fooAttr]!!
        val foo2 = map[attribute<Foo>("foo")]!! // by a new instance

        assert(foo1 === foo2)
    }


}


private class Foo
/*
 * Copyright (c) 2022 ForteScarlet <ForteScarlet@163.com>
 *
 * 本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x 、simbot3 等) 的一部分。
 * simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 * 发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 * 你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 * https://www.gnu.org/licenses
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 * https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 */

package love.forli.test

import love.forte.simboot.annotation.Filter
import love.forte.simboot.spring.autoconfigure.utils.SpringAnnotationTool
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

private const val VALUE_1 = "1"
private const val VALUE_2 = "2"
private const val VALUE_3 = "Hello"

private class A {
    @Filter(VALUE_1)
    @Filter(VALUE_2)
    @MyFilter
    fun b() {
    }
}

@Filter(VALUE_3)
private annotation class MyFilter

class AnnotationTest {

    @Test
    fun repeatableTest() {
        val tool = SpringAnnotationTool()
        val func = A::b
        val annotations = tool.getAnnotations(func, Filter::class)
        assertEquals(3, annotations.size)

        val values = annotations.map { it.value }
        assertContains(values, VALUE_1)
        assertContains(values, VALUE_2)
        assertContains(values, VALUE_3)
    }
}


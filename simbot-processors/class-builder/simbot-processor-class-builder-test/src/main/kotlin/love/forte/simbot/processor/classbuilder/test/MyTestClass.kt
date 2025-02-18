/*
 *     Copyright (c) 2025. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package love.forte.simbot.processor.classbuilder.test

import love.forte.simbot.processor.classbuilder.annotation.ClassBuilder
import love.forte.simbot.processor.classbuilder.testlib.MyTestLibClass

@DslMarker
annotation class TestDslMark

@ClassBuilder(marks = [TestDslMark::class])
class MyTestClass<T>(
    val name: String,
    val size: Int,
    val other: TestClass2,
    val typeLib: MyTestLibClass,

    val otherNullable: TestClass2?,
    val typeLibNullable: MyTestLibClass?,

    val sizeList: List<Int>,
    val nameSet: Set<String?>,
    val sizeListNullable: List<Int>?,
    val nameSetNullable: Set<String?>?,
    val sizeCollection: Collection<Int>,
    val nameCollection: Collection<String?>,
    val sizeCollectionNullable: Collection<Int>?,
    val nameCollectionNullable: Collection<String?>?,
    val sizeArray: Array<Int>,
    val nameArray: Array<String?>,
    val sizeArrayNullable: Array<Int>?,
    val nameArrayNullable: Array<String?>?,

    val typedList: List<T>,
    val typedListNullable: List<T>?,

    val typedSet: Set<T>,
    val typedSetNullable: Set<T>?,

    val typedCol: Collection<T>,
    val typedColNullable: Collection<T>?,

    val typedArray: Array<T>,
    val typedArrayNullable: Array<T>?,

    val sizeMap: Map<Int, Long>,
    val nameMap: Map<String?, Long>,
    val sizeMapNullable: Map<Int, Long>?,
    val nameMapNullable: Map<String?, Long>?,

    val otherBuilderList: List<TestClass2>,

    val typeLibList: List<MyTestLibClass>,

    vararg val nameVararg: String,
) {
    var length: Int? = null
    lateinit var testClass2: TestClass2
}

@ClassBuilder(marks = [TestDslMark::class])
class TestClass2 {
    var name: String? = null
    var times: Int = 1
}

inline fun <T> MyTestClass(block: MyTestClassBuilder<T>.() -> Unit): MyTestClass<T> {
    return MyTestClassBuilder<T>().apply(block).build()
}

fun a() {
    MyTestClass<Byte> {
        size = 5
        addAllNameSet("a", "b", "c")

        typeLib {
            size(2)
        }
    }
}

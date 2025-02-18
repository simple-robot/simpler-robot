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
import kotlin.properties.Delegates

@DslMarker
annotation class TestDslMark

@ClassBuilder(marks = [TestDslMark::class], open = true, internal = true)
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
    val valNotBuildable: String = "valNotBuildable"

    lateinit var propName: String
    var propSize by Delegates.notNull<Int>()
    lateinit var propOther: TestClass2
    lateinit var propTypeLib: MyTestLibClass

    var propOtherNullable: TestClass2? = null
    var propTypeLibNullable: MyTestLibClass? = null

    lateinit var propSizeList: List<Int>
    lateinit var propNameSet: Set<String?>
    var propSizeListNullable: List<Int>? = null
    var propNameSetNullable: Set<String?>? = null
    lateinit var propSizeCollection: Collection<Int>
    lateinit var propNameCollection: Collection<String?>
    var propSizeCollectionNullable: Collection<Int>? = null
    var propNameCollectionNullable: Collection<String?>? = null
    lateinit var propSizeArray: Array<Int>
    lateinit var propNameArray: Array<String?>
    var propSizeArrayNullable: Array<Int>? = null
    var propNameArrayNullable: Array<String?>? = null

    lateinit var propTypedList: List<T>
    var propTypedListNullable: List<T>? = null

    lateinit var propTypedSet: Set<T>
    var propTypedSetNullable: Set<T>? = null

    lateinit var propTypedCol: Collection<T>
    var propTypedColNullable: Collection<T>? = null

    lateinit var propTypedArray: Array<T>
    var propTypedArrayNullable: Array<T>? = null

    lateinit var propSizeMap: Map<Int, Long>
    lateinit var propNameMap: Map<String?, Long>
    var propSizeMapNullable: Map<Int, Long>? = null
    var propNameMapNullable: Map<String?, Long>? = null

    lateinit var propOtherBuilderList: List<TestClass2>

    lateinit var propTypeLibList: List<MyTestLibClass>
}

@ClassBuilder(marks = [TestDslMark::class])
class TestClass2 {
    var name: String? = null
    var times: Int = 1
}

internal inline fun <T> MyTestClass(block: MyTestClassBuilder<T>.() -> Unit): MyTestClass<T> {
    return MyTestClassBuilder<T>().apply(block).build()
}

class User<T>(val values: List<T>)


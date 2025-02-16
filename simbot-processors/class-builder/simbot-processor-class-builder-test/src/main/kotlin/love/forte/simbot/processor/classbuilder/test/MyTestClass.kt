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

import love.forte.simbot.processor.classbuilder.annotation.BuilderFor
import love.forte.simbot.processor.classbuilder.annotation.ClassBuilder

@DslMarker
annotation class TestDslMark

@ClassBuilder(marks = [TestDslMark::class])
class MyTestClass(
    val name: String,
    val size: Int
) {
    var length: Int? = null
    lateinit var testClass2: TestClass2
}

@ClassBuilder(marks = [TestDslMark::class])
class TestClass2 {
    var name: String? = null
    var times: Int = 1
}

@BuilderFor(TestClass2::class)
class TestClass2Builder1

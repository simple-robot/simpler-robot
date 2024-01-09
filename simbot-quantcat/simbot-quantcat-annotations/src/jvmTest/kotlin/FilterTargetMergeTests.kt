/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
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

import love.forte.simbot.quantcat.annotations.Filter

/**
 *
 * @author ForteScarlet
 */
class FilterTargetMergeTests {


    @Filter(targets = [Filter.Targets(bots = ["A1", "A2"]), Filter.Targets(bots = ["B1", "B2"], atBot = true)])
    private fun func1() {
    }

    @Filter(targets = [Filter.Targets(bots = ["A3", "A4"]), Filter.Targets(bots = ["B3", "B4"])])
    private fun func2() {
    }

    @Filter(targets = [Filter.Targets()])
    private fun func3() {
    }

    @Filter(targets = [Filter.Targets()])
    private fun func4() {
    }

    @Filter
    @Filter
    fun func5() {
    }

    //
    // @Test
    // fun multiAnnotationTest() {
    //     val javaMethod = ::func5.javaMethod!!
    //
    //     val annotations = javaMethod.getAnnotationsByType(Filter::class.java)
    //     println(annotations.toList())
    //     assertEquals(2, annotations.size)
    // }

}

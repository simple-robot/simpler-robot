/*
 *     Copyright (c) 2024. ForteScarlet.
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

package love.forte.simbot.common.collection

import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.annotations.Param
import org.jetbrains.kotlinx.lincheck.check
import org.jetbrains.kotlinx.lincheck.paramgen.IntGen
import org.jetbrains.kotlinx.lincheck.strategy.managed.modelchecking.ModelCheckingOptions
import kotlin.test.Test


/**
 *
 * @author ForteScarlet
 */
@Param(name = "k", gen = IntGen::class, conf = "1:10")
@Param(name = "v", gen = IntGen::class, conf = "1:20")
class JvmConcurrentMapLincheckTest {
    private val map = concurrentMutableMap<Int, Int>()

    @Operation
    fun put(@Param(name = "k") key: Int, @Param(name = "v") value: Int) =
        map.put(key, value)

    @Operation
    fun computeIfAbsent(@Param(name = "k") key: Int, @Param(name = "v") value: Int) =
        map.computeValueIfAbsent(key) { value }

    @Operation
    fun computeIfPresent(@Param(name = "k") key: Int, @Param(name = "v") value: Int) =
        map.computeValueIfPresent(key) { _, _ -> value }

    @Operation
    fun compute(@Param(name = "k") key: Int, @Param(name = "v") value: Int) =
        map.computeValue(key) { _, _ -> value }

    @Operation
    fun remove(@Param(name = "k") key: Int) =
        map.remove(key)

    @Operation
    fun remove(@Param(name = "k") key: Int, @Param(name = "v") value: Int) =
        map.removeValue(key) { value }

    @Operation
    fun get(@Param(name = "k") key: Int) = map[key]

    @Test
    fun modelCheckingTest() = ModelCheckingOptions().check(this::class)
}

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

abstract class JvmConcurrentQueueLincheckTests {
    @Test
    open fun modelCheckingTest() = ModelCheckingOptions().check(this::class)
}

@OptIn(ExperimentalSimbotCollectionApi::class)
@Param(name = "e", gen = IntGen::class, conf = "1:5")
class JvmConcurrentQueueLincheckTest : JvmConcurrentQueueLincheckTests() {
    private val queue = createConcurrentQueue<Int>()

    @Operation
    fun add(@Param(name = "e") e: Int) = queue.add(e)

    @Operation
    fun remove(@Param(name = "e") e: Int) = queue.remove(e)

    @Operation
    fun removeIf(@Param(name = "e") e: Int) = queue.removeIf { it == e }
}

@OptIn(ExperimentalSimbotCollectionApi::class)
@Param(name = "e", gen = IntGen::class, conf = "1:5")
@Param(name = "priority", gen = IntGen::class, conf = "1:5")
class JvmPriorityConcurrentQueueLincheckTest : JvmConcurrentQueueLincheckTests() {
    private val queue = createPriorityConcurrentQueue<Int>()

    @Operation
    fun add(
        @Param(name = "priority") p: Int,
        @Param(name = "e") e: Int,
    ) = queue.add(p, e)

    @Operation
    fun remove(
        @Param(name = "priority") p: Int,
        @Param(name = "e") e: Int,
    ) = queue.remove(p, e)

    @Operation
    fun removeIf(@Param(name = "e") e: Int) = queue.removeIf { it == e }

    @Operation
    fun removeIf(@Param(name = "priority") p: Int, @Param(name = "e") e: Int) = queue.removeIf(p) { it == e }

    @Operation
    fun remove(@Param(name = "e") e: Int) = queue.remove(e)
}

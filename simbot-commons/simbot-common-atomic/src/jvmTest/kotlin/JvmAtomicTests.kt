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

import love.forte.simbot.common.atomic.AtomicULong
import love.forte.simbot.common.atomic.atomic
import love.forte.simbot.common.atomic.atomicUL
import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.check
import org.jetbrains.kotlinx.lincheck.strategy.managed.modelchecking.ModelCheckingOptions
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressOptions
import kotlin.test.Test

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

abstract class AtomicStressTest {
    @Test
    fun stressTest() = StressOptions().check(this::class)
    @Test
    fun modelCheckingTest() = ModelCheckingOptions().check(this::class)
}

class AtomicLongTest : AtomicStressTest() {
    private val atomic = atomic(0L)

    @Operation
    fun inc1() = atomic.getAndIncrement()

    @Operation
    fun inc2() = atomic.incrementAndGet()

    @Operation
    fun dec1() = atomic.getAndDecrement()

    @Operation
    fun dec2() = atomic.decrementAndGet()

    @Operation
    fun get() = atomic.value


}

class AtomicIntTest : AtomicStressTest() {
    private val atomic = atomic(0)

    @Operation
    fun inc1() = atomic.getAndIncrement()

    @Operation
    fun inc2() = atomic.incrementAndGet()

    @Operation
    fun dec1() = atomic.getAndDecrement()

    @Operation
    fun dec2() = atomic.decrementAndGet()

    @Operation
    fun get() = atomic.value
}

class AtomicUIntTest : AtomicStressTest() {
    private val atomic = atomic(0u)

    @Operation
    fun inc1() = atomic.getAndIncrement()

    @Operation
    fun inc2() = atomic.incrementAndGet()

    @Operation
    fun dec1() = atomic.getAndDecrement()

    @Operation
    fun dec2() = atomic.decrementAndGet()

    @Operation
    fun get() = atomic.value
}

class AtomicULongTest : AtomicStressTest() {
    private val atomic: AtomicULong = atomicUL(0u)

    @Operation
    fun inc1() = atomic.getAndIncrement()

    @Operation
    fun inc2() = atomic.incrementAndGet()

    @Operation
    fun dec1() = atomic.getAndDecrement()

    @Operation
    fun dec2() = atomic.decrementAndGet()

    @Operation
    fun get() = atomic.value
}

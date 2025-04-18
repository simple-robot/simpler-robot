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

import love.forte.plugin.suspendtrans.annotation.JvmAsync
import love.forte.plugin.suspendtrans.annotation.JvmBlocking
import love.forte.simbot.suspendrunner.ST
import love.forte.simbot.suspendrunner.STP
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaMethod
import kotlin.test.Test

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

interface MyTestInterface {
    @ST
    suspend fun run1()

    @STP
    suspend fun run2()

    @JvmBlocking
    @JvmAsync
    suspend fun run3()
}

/**
 *
 * @author ForteScarlet
 */
class ReflectTests {
    @Test
    fun testAcceptSupportGeneratedMethods() {
        val members = MyTestInterface::class.members

        // Check Kotlin functions exist
        val functions = members.filterIsInstance<KFunction<*>>()
        val functionNames = functions.map { it.name }.toSet()
        assert(functionNames.containsAll(setOf("run1", "run2", "run3"))) {
            "Expected Kotlin functions run1, run2, run3 but got: $functionNames"
        }

        // Check generated blocking/async variants exist
        assert(
            functionNames.containsAll(
                setOf(
                    "run1Blocking",
                    "run1Async",
                    "run1Reserve",
                    "run3Blocking",
                    "run3Async"
                )
            )
        ) { "Expected blocking/async variants but got: $functionNames" }

        // Check Java methods exist
        val javaMethods = functions.mapNotNull { it.javaMethod }.map { it.name }.toSet()
        assert(
            javaMethods.containsAll(
                setOf(
                    "run1",
                    "run1Blocking",
                    "run1Async",
                    "run1Reserve",
                    "run2",
                    "run3",
                    "run3Blocking",
                    "run3Async"
                )
            )
        ) { "Expected Java methods but got: $javaMethods" }

        // Check property getters exist
        val properties = members.filterIsInstance<KProperty<*>>()
        val propertyNames = properties.map { it.name }.toSet()
        assert(propertyNames.containsAll(setOf("run2", "run2Async", "run2Reserve"))) {
            "Expected property getters but got: $propertyNames"
        }

        // Check property getter Java methods
        val propertyJavaMethods = properties.mapNotNull { it.getter.javaMethod }.map { it.name }.toSet()
        assert(propertyJavaMethods.containsAll(setOf("getRun2", "getRun2Async", "getRun2Reserve"))) {
            "Expected property getter Java methods but got: $propertyJavaMethods"
        }
    }
}

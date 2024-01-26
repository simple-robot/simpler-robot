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
import love.forte.simbot.suspendrunner.ST
import love.forte.simbot.suspendrunner.STP
import love.forte.simbot.suspendrunner.reserve.SuspendReserve
import java.lang.reflect.Modifier
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KTypeParameter
import kotlin.reflect.full.memberProperties
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue


@ST
interface STTrans1 {
    suspend fun run1()
    suspend fun run1(value: String): String
}

@STP
interface STPTrans1 {
    suspend fun run1(): Int
    suspend fun run2(): String
}

interface STTrans2 {
    @ST(blockingSuffix = "Bk", asyncSuffix = "Ay", reserveSuffix = "Rs")
    suspend fun run1()

    @ST(blockingSuffix = "Bk", asyncSuffix = "Ay", reserveSuffix = "Rs")
    suspend fun run1(value: String): String
}

interface STPTrans2 {
    @STP(blockingSuffix = "Bk", asyncSuffix = "Ay", reserveSuffix = "Rs")
    suspend fun run1(): Int

    @STP(blockingSuffix = "Bk", asyncSuffix = "Ay", reserveSuffix = "Rs")
    suspend fun run2(): String
}

interface STTrans3 {
    @ST(
        blockingBaseName = "apply1",
        blockingSuffix = "Bk",
        asyncBaseName = "apply1",
        asyncSuffix = "Ay",
        reserveBaseName = "apply1",
        reserveSuffix = "Rs"
    )
    suspend fun run1()

    @ST(
        blockingBaseName = "apply1",
        blockingSuffix = "Bk",
        asyncBaseName = "apply1",
        asyncSuffix = "Ay",
        reserveBaseName = "apply1",
        reserveSuffix = "Rs"
    )
    suspend fun run1(value: String): String
}

interface STPTrans3 {
    @STP(
        blockingBaseName = "apply1",
        blockingSuffix = "Bk",
        asyncBaseName = "apply1",
        asyncSuffix = "Ay",
        reserveBaseName = "apply1",
        reserveSuffix = "Rs"
    )
    suspend fun run1(): Int

    @STP(
        blockingBaseName = "apply2",
        blockingSuffix = "Bk",
        asyncBaseName = "apply2",
        asyncSuffix = "Ay",
        reserveBaseName = "apply2",
        reserveSuffix = "Rs"
    )
    suspend fun run2(): String
}

open class Foo
open class Bar : Foo()

@STP
interface ITypedTrans1<T : Foo> {
    suspend fun value(): T
}

@STP
interface TypedTrans1Impl<T : Bar> : ITypedTrans1<T> {
    override suspend fun value(): T
}

/**
 *
 * @author ForteScarlet
 */
class SuspendTransformTests {

    @Test
    fun `interface suspend trans function test`() {
        with(STTrans1::class.java) {
            val blockingMethod = getMethod("run1Blocking")
            val asyncMethod = getMethod("run1Async")
            val reserveMethod = getMethod("run1Reserve")

            assertEquals(Void.TYPE, blockingMethod.returnType)
            assertEquals(CompletableFuture::class.java, asyncMethod.returnType)
            assertEquals(SuspendReserve::class.java, reserveMethod.returnType)

            assertFalse(Modifier.isAbstract(blockingMethod.modifiers))
            assertFalse(Modifier.isAbstract(asyncMethod.modifiers))
            assertFalse(Modifier.isAbstract(reserveMethod.modifiers))
        }

        with(STTrans1::class.java) {
            val blockingMethod = getMethod("run1Blocking", String::class.java)
            val asyncMethod = getMethod("run1Async", String::class.java)
            val reserveMethod = getMethod("run1Reserve", String::class.java)

            assertEquals(String::class.java, blockingMethod.returnType)
            assertEquals(CompletableFuture::class.java, asyncMethod.returnType)
            assertEquals(SuspendReserve::class.java, reserveMethod.returnType)

            assertFalse(Modifier.isAbstract(blockingMethod.modifiers))
            assertFalse(Modifier.isAbstract(asyncMethod.modifiers))
            assertFalse(Modifier.isAbstract(reserveMethod.modifiers))
        }
    }

    @Test
    fun `interface suspend trans property test`() {
        with(STPTrans1::class.memberProperties) {
            assertTrue(any { it.name == "run1" && it.returnType.classifier == Int::class })
            assertTrue(any { it.name == "run2" && it.returnType.classifier == String::class })
        }

        // run1
        with(STPTrans1::class.java) {
            val blockingPropertyMethod = getMethod("getRun1")
            val asyncPropertyMethod = getMethod("getRun1Async")
            val reservePropertyMethod = getMethod("getRun1Reserve")

            assertEquals(Int::class.javaPrimitiveType, blockingPropertyMethod.returnType)
            assertEquals(CompletableFuture::class.java, asyncPropertyMethod.returnType)
            assertEquals(SuspendReserve::class.java, reservePropertyMethod.returnType)

            assertFalse(Modifier.isAbstract(blockingPropertyMethod.modifiers))
            assertFalse(Modifier.isAbstract(asyncPropertyMethod.modifiers))
            assertFalse(Modifier.isAbstract(reservePropertyMethod.modifiers))
        }

        // run2
        with(STPTrans1::class.java) {
            val blockingPropertyMethod = getMethod("getRun2")
            val asyncPropertyMethod = getMethod("getRun2Async")
            val reservePropertyMethod = getMethod("getRun2Reserve")

            assertEquals(String::class.java, blockingPropertyMethod.returnType)
            assertEquals(CompletableFuture::class.java, asyncPropertyMethod.returnType)
            assertEquals(SuspendReserve::class.java, reservePropertyMethod.returnType)

            assertFalse(Modifier.isAbstract(blockingPropertyMethod.modifiers))
            assertFalse(Modifier.isAbstract(asyncPropertyMethod.modifiers))
            assertFalse(Modifier.isAbstract(reservePropertyMethod.modifiers))
        }

    }

    @Test
    fun `interface suspend trans function with suffix test`() {
        with(STTrans2::class.java) {
            val blockingMethod = getMethod("run1Bk")
            val asyncMethod = getMethod("run1Ay")
            val reserveMethod = getMethod("run1Rs")

            assertEquals(Void.TYPE, blockingMethod.returnType)
            assertEquals(CompletableFuture::class.java, asyncMethod.returnType)
            assertEquals(SuspendReserve::class.java, reserveMethod.returnType)

            assertFalse(Modifier.isAbstract(blockingMethod.modifiers))
            assertFalse(Modifier.isAbstract(asyncMethod.modifiers))
            assertFalse(Modifier.isAbstract(reserveMethod.modifiers))
        }

        with(STTrans2::class.java) {
            val blockingMethod = getMethod("run1Bk", String::class.java)
            val asyncMethod = getMethod("run1Ay", String::class.java)
            val reserveMethod = getMethod("run1Rs", String::class.java)

            assertEquals(String::class.java, blockingMethod.returnType)
            assertEquals(CompletableFuture::class.java, asyncMethod.returnType)
            assertEquals(SuspendReserve::class.java, reserveMethod.returnType)

            assertFalse(Modifier.isAbstract(blockingMethod.modifiers))
            assertFalse(Modifier.isAbstract(asyncMethod.modifiers))
            assertFalse(Modifier.isAbstract(reserveMethod.modifiers))
        }
    }

    @Test
    fun `interface suspend trans property with suffix test`() {
        with(STPTrans2::class.memberProperties) {
            assertTrue(any { it.name == "run1Bk" && it.returnType.classifier == Int::class })
            assertTrue(any { it.name == "run2Bk" && it.returnType.classifier == String::class })
        }

        // run1
        with(STPTrans2::class.java) {
            val blockingPropertyMethod = getMethod("getRun1Bk")
            val asyncPropertyMethod = getMethod("getRun1Ay")
            val reservePropertyMethod = getMethod("getRun1Rs")

            assertEquals(Int::class.javaPrimitiveType, blockingPropertyMethod.returnType)
            assertEquals(CompletableFuture::class.java, asyncPropertyMethod.returnType)
            assertEquals(SuspendReserve::class.java, reservePropertyMethod.returnType)

            assertFalse(Modifier.isAbstract(blockingPropertyMethod.modifiers))
            assertFalse(Modifier.isAbstract(asyncPropertyMethod.modifiers))
            assertFalse(Modifier.isAbstract(reservePropertyMethod.modifiers))
        }

        // run2
        with(STPTrans2::class.java) {
            val blockingPropertyMethod = getMethod("getRun2Bk")
            val asyncPropertyMethod = getMethod("getRun2Ay")
            val reservePropertyMethod = getMethod("getRun2Rs")

            assertEquals(String::class.java, blockingPropertyMethod.returnType)
            assertEquals(CompletableFuture::class.java, asyncPropertyMethod.returnType)
            assertEquals(SuspendReserve::class.java, reservePropertyMethod.returnType)

            assertFalse(Modifier.isAbstract(blockingPropertyMethod.modifiers))
            assertFalse(Modifier.isAbstract(asyncPropertyMethod.modifiers))
            assertFalse(Modifier.isAbstract(reservePropertyMethod.modifiers))
        }

    }

    @Test
    fun `interface suspend trans function with baseName and suffix test`() {
        with(STTrans3::class.java) {
            val blockingMethod = getMethod("apply1Bk")
            val asyncMethod = getMethod("apply1Ay")
            val reserveMethod = getMethod("apply1Rs")

            assertEquals(Void.TYPE, blockingMethod.returnType)
            assertEquals(CompletableFuture::class.java, asyncMethod.returnType)
            assertEquals(SuspendReserve::class.java, reserveMethod.returnType)

            assertFalse(Modifier.isAbstract(blockingMethod.modifiers))
            assertFalse(Modifier.isAbstract(asyncMethod.modifiers))
            assertFalse(Modifier.isAbstract(reserveMethod.modifiers))
        }

        with(STTrans3::class.java) {

            val blockingMethod = getMethod("apply1Bk", String::class.java)
            val asyncMethod = getMethod("apply1Ay", String::class.java)
            val reserveMethod = getMethod("apply1Rs", String::class.java)

            assertEquals(String::class.java, blockingMethod.returnType)
            assertEquals(CompletableFuture::class.java, asyncMethod.returnType)
            assertEquals(SuspendReserve::class.java, reserveMethod.returnType)

            assertFalse(Modifier.isAbstract(blockingMethod.modifiers))
            assertFalse(Modifier.isAbstract(asyncMethod.modifiers))
            assertFalse(Modifier.isAbstract(reserveMethod.modifiers))
        }
    }

    @Test
    fun `interface suspend trans property with baseName and suffix test`() {
        with(STPTrans3::class.memberProperties) {
            assertTrue(any { it.name == "apply1Bk" && it.returnType.classifier == Int::class })
            assertTrue(any { it.name == "apply2Bk" && it.returnType.classifier == String::class })
        }

        // run1
        with(STPTrans3::class.java) {
            val blockingPropertyMethod = getMethod("getApply1Bk")
            val asyncPropertyMethod = getMethod("getApply1Ay")
            val reservePropertyMethod = getMethod("getApply1Rs")

            assertEquals(Int::class.javaPrimitiveType, blockingPropertyMethod.returnType)
            assertEquals(CompletableFuture::class.java, asyncPropertyMethod.returnType)
            assertEquals(SuspendReserve::class.java, reservePropertyMethod.returnType)

            assertFalse(Modifier.isAbstract(blockingPropertyMethod.modifiers))
            assertFalse(Modifier.isAbstract(asyncPropertyMethod.modifiers))
            assertFalse(Modifier.isAbstract(reservePropertyMethod.modifiers))
        }

        // run2
        with(STPTrans3::class.java) {
            val blockingPropertyMethod = getMethod("getApply2Bk")
            val asyncPropertyMethod = getMethod("getApply2Ay")
            val reservePropertyMethod = getMethod("getApply2Rs")

            assertEquals(String::class.java, blockingPropertyMethod.returnType)
            assertEquals(CompletableFuture::class.java, asyncPropertyMethod.returnType)
            assertEquals(SuspendReserve::class.java, reservePropertyMethod.returnType)

            assertFalse(Modifier.isAbstract(blockingPropertyMethod.modifiers))
            assertFalse(Modifier.isAbstract(asyncPropertyMethod.modifiers))
            assertFalse(Modifier.isAbstract(reservePropertyMethod.modifiers))
        }

    }

    @Test
    fun `typed interface test`() {
        with(ITypedTrans1::class) {
            assertTrue(memberProperties.any {
                it.name == "value" && with(it.returnType.classifier) {
                    this is KTypeParameter && this.upperBounds.any { b -> b.classifier == Foo::class }
                }
            })
            assertTrue(memberProperties.any {
                it.name == "valueAsync" && it.returnType.classifier == CompletableFuture::class
            })
            assertTrue(memberProperties.any {
                it.name == "valueReserve" && it.returnType.classifier == SuspendReserve::class
            })
        }
        with(TypedTrans1Impl::class) {
            assertTrue(memberProperties.any {
                it.name == "value" && with(it.returnType.classifier) {
                    this is KTypeParameter && this.upperBounds.any { b -> b.classifier == Bar::class }
                }
            })
            assertTrue(memberProperties.any {
                it.name == "valueAsync" && it.returnType.classifier == CompletableFuture::class
            })
            assertTrue(memberProperties.any {
                it.name == "valueReserve" && it.returnType.classifier == SuspendReserve::class
            })
        }
    }

}

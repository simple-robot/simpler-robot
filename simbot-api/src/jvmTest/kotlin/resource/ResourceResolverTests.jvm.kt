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

package resource

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.test.runTest
import love.forte.simbot.resource.AbstractJvmResourceValueResolver
import love.forte.simbot.resource.Resource
import love.forte.simbot.resource.ResourceResolver.Companion.resolve
import love.forte.simbot.resource.toResource
import love.forte.simbot.resource.toStringResource
import java.io.File
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.test.Test
import kotlin.test.assertEquals

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

/**
 *
 * @author ForteScarlet
 */
class ResourceResolverTestsJvm {
    private enum class Value {
        UKN,
        BA,
        S,
        F,
        P,
        U
    }

    @Test
    fun jvmResolverTest() = runTest {
        val path = Files.createTempFile("PRE", "SUF")
        path.toFile().deleteOnExit()

        val resource = object : AbstractJvmResourceValueResolver<Continuation<Value>>() {
            override fun resolveUnknownInternal(resource: Resource, context: Continuation<Value>) {
                context.resume(Value.UKN)
            }

            override fun resolveFile(file: File, context: Continuation<Value>) {
                context.resume(Value.F)
            }

            override fun resolvePath(path: Path, context: Continuation<Value>) {
                context.resume(Value.P)
            }

            override fun resolveByteArray(byteArray: ByteArray, context: Continuation<Value>) {
                context.resume(Value.BA)
            }

            override fun resolveString(string: String, context: Continuation<Value>) {
                context.resume(Value.S)
            }

            override fun resolveURINotFileScheme(uri: URI, context: Continuation<Value>) {
                context.resume(Value.U)
            }
        }

        assertEquals(
            Value.P,
            suspendCancellableCoroutine { c ->
                resource.resolve(path.toResource(), c)
            }
        )
        assertEquals(
            Value.F,
            suspendCancellableCoroutine { c ->
                resource.resolve(path.toFile().toResource(), c)
            }
        )
        assertEquals(
            Value.BA,
            suspendCancellableCoroutine { c ->
                resource.resolve(byteArrayOf().toResource(), c)
            }
        )
        assertEquals(
            Value.S,
            suspendCancellableCoroutine { c ->
                resource.resolve("".toStringResource(), c)
            }
        )
        assertEquals(
            Value.U,
            suspendCancellableCoroutine { c ->
                resource.resolve(URI.create("https://baidu.com").toResource(), c)
            }
        )
        assertEquals(
            Value.UKN,
            suspendCancellableCoroutine { c ->
                resource.resolve(
                    object : Resource {
                        override fun data(): ByteArray {
                            return byteArrayOf()
                        }
                    },
                    c
                )
            }
        )

    }
}

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

package message

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.test.runTest
import love.forte.simbot.message.JvmOfflineImageValueResolver
import love.forte.simbot.message.OfflineFileImage.Companion.toOfflineImage
import love.forte.simbot.message.OfflineImage
import love.forte.simbot.message.OfflineImage.Companion.toOfflineImage
import love.forte.simbot.message.OfflineImageResolver.Companion.resolve
import love.forte.simbot.message.OfflinePathImage.Companion.toOfflineImage
import love.forte.simbot.message.OfflineURIImage.Companion.toOfflineImage
import love.forte.simbot.resource.Resource
import love.forte.simbot.resource.toResource
import java.io.File
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.test.Test
import kotlin.test.assertEquals


/**
 *
 * @author ForteScarlet
 */
class JvmOfflineImageResolverTests {
    private enum class Value {
        UKN_I,
        UKN_R,
        BA,
        S,
        F,
        P,
        U
    }

    @Test
    fun jvmOfflineImageValueResolverTest() = runTest {
        val path = Files.createTempFile("PRE", "SUF")
        path.toFile().deleteOnExit()

        val resolver = object : JvmOfflineImageValueResolver<Continuation<Value>>() {
            override fun resolveUnknownInternal(image: OfflineImage, context: Continuation<Value>) {
                context.resume(Value.UKN_I)
            }

            override fun resolveUnknownInternal(resource: Resource, context: Continuation<Value>) {
                context.resume(Value.UKN_R)
            }

            override fun resolveByteArray(byteArray: ByteArray, context: Continuation<Value>) {
                context.resume(Value.BA)
            }

            override fun resolveString(string: String, context: Continuation<Value>) {
                context.resume(Value.S)
            }

            override fun resolveFile(file: File, context: Continuation<Value>) {
                context.resume(Value.F)
            }

            override fun resolvePath(path: Path, context: Continuation<Value>) {
                context.resume(Value.P)
            }

            override fun resolveURINotFileScheme(uri: URI, context: Continuation<Value>) {
                context.resume(Value.U)
            }
        }

        assertEquals(
            Value.UKN_I,
            suspendCancellableCoroutine { c ->
                resolver.resolve(
                    object : OfflineImage {
                        override fun data(): ByteArray = byteArrayOf()
                    },
                    c
                )
            }
        )
        assertEquals(
            Value.UKN_R,
            suspendCancellableCoroutine { c ->
                resolver.resolve(
                    object : Resource {
                        override fun data(): ByteArray = byteArrayOf()
                    }.toOfflineImage(),
                    c
                )
            }
        )
        assertEquals(
            Value.F,
            suspendCancellableCoroutine { c ->
                resolver.resolve(
                    path.toFile().toOfflineImage(),
                    c
                )
            }
        )
        assertEquals(
            Value.F,
            suspendCancellableCoroutine { c ->
                resolver.resolve(
                    path.toFile().toResource().toOfflineImage(),
                    c
                )
            }
        )
        assertEquals(
            Value.P,
            suspendCancellableCoroutine { c ->
                resolver.resolve(
                    path.toOfflineImage(),
                    c
                )
            }
        )
        assertEquals(
            Value.P,
            suspendCancellableCoroutine { c ->
                resolver.resolve(
                    path.toResource().toOfflineImage(),
                    c
                )
            }
        )
        assertEquals(
            Value.U,
            suspendCancellableCoroutine { c ->
                resolver.resolve(
                    URI.create("https://baidu.com").toOfflineImage(),
                    c
                )
            }
        )
        assertEquals(
            Value.U,
            suspendCancellableCoroutine { c ->
                resolver.resolve(
                    URI.create("https://baidu.com").toResource().toOfflineImage(),
                    c
                )
            }
        )
    }

}

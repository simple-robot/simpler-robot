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
import love.forte.simbot.message.OfflineByteArrayImage
import love.forte.simbot.message.OfflineImage
import love.forte.simbot.message.OfflineImage.Companion.toOfflineImage
import love.forte.simbot.message.OfflineImageResolver
import love.forte.simbot.message.OfflineImageResolver.Companion.resolve
import love.forte.simbot.message.OfflineResourceImage
import love.forte.simbot.resource.Resource
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.test.Test
import kotlin.test.assertEquals


/**
 *
 * @author ForteScarlet
 */
class OfflineImageResolverTests {
    private enum class Value {
        UKN,
        BA,
        R
    }

    @Test
    fun offlineImageTest() = runTest {
        val resolver = object : OfflineImageResolver<Continuation<Value>> {
            override fun resolveUnknown(image: OfflineImage, context: Continuation<Value>) {
                context.resume(Value.UKN)
            }

            override fun resolveByteArray(image: OfflineByteArrayImage, context: Continuation<Value>) {
                context.resume(Value.BA)
            }

            override fun resolveResource(image: OfflineResourceImage, context: Continuation<Value>) {
                context.resume(Value.R)
            }
        }
        assertEquals(
            Value.UKN,
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
            Value.BA,
            suspendCancellableCoroutine { c ->
                resolver.resolve(
                    byteArrayOf().toOfflineImage(),
                    c
                )
            }
        )
        assertEquals(
            Value.R,
            suspendCancellableCoroutine { c ->
                resolver.resolve(
                    object : Resource {
                        override fun data(): ByteArray = byteArrayOf()
                    }.toOfflineImage(),
                    c
                )
            }
        )
    }

}

/*
 *     Copyright (c) 2026. ForteScarlet.
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

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.test.runTest
import kotlin.concurrent.AtomicReference
import kotlin.coroutines.resume
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.milliseconds

/**
 *
 * @author ForteScarlet
 */
class AtomicTest {
    private data class Data<T, R>(val value: T, val continuation: CancellableContinuation<R>)
    private class Session<T, R>(val channel: Channel<Data<T, R>>)
    class MapBox<K, V>(val map: Map<K, V>)

    private fun <K, V> Map<K, V>.box(): MapBox<K, V> = MapBox(this)

    @Test
    fun nativeAtomicTest() = runTest {
        val pJob = Job()
        val key = Any()
        val mapRef = AtomicReference(emptyMap<Any, Session<Int, String>>().box())
        withContext(Dispatchers.Default) {
            val channel = Channel<Data<Int, String>>()
            val scope = CoroutineScope(Dispatchers.Default + pJob)
            val sJob = Job(pJob)
            val session = Session(channel)

            sJob.invokeOnCompletion {
                channel.close(it)
                // remove session
                do {
                    val oldMapBox = mapRef.value
                    val newMap = oldMapBox.map.toMutableMap().apply {
                        removeValue(key) { session }
                    }
                } while (!mapRef.compareAndSet(expected = oldMapBox, newValue = newMap.box()))
            }

            scope.launch {
                withContext(Dispatchers.Default) {
                    withTimeoutOrNull(50.milliseconds) {
                        session.channel.receive()
                    }
                }

                session.channel.receive().also { (value, continuation) ->
                    continuation.resume(value.toString())
                }
                session.channel.receive().also { (value, continuation) ->
                    continuation.resume(value.toString())
                }
                session.channel.receive().also { (value, continuation) ->
                    continuation.resume(value.toString())
                }
                sJob.complete()
            }

            withContext(Dispatchers.Default) {
                delay(100.milliseconds)
            }

            coroutineScope {
                val v1 = suspendCancellableCoroutine { c ->
                    launch { channel.send(Data(1, c)) }
                }
                val v2 = suspendCancellableCoroutine { c ->
                    launch { channel.send(Data(2, c)) }
                }
                val v3 = suspendCancellableCoroutine { c ->
                    launch { channel.send(Data(3, c)) }
                }

                assertEquals("1", v1)
                assertEquals("2", v2)
                assertEquals("3", v3)

                sJob.join()

                assertNull(mapRef.value.map[key])
            }
        }

    }

}

private inline fun <K, V> MutableMap<K, V>.removeValue(
    key: K,
    crossinline target: () -> V
): Boolean {
    val targetValue = target()
    val iter = iterator()
    while (iter.hasNext()) {
        val entry = iter.next()
        if (entry.key == key && entry.value == targetValue) {
            iter.remove()
            return true
        }
    }

    return false
}

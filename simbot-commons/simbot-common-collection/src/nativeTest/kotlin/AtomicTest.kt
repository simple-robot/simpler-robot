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
    fun a() = runTest {
        val pJob = Job()
        val key = Any()
        val mapRef = AtomicReference(emptyMap<Any, Session<Int, String>>().box())
        withContext(Dispatchers.Default) {
            val channel = Channel<Data<Int, String>>()
            val scope = CoroutineScope(Dispatchers.Default + pJob)
            val sJob = Job(pJob)
            val session = Session(channel)

            sJob.invokeOnCompletion {
                channel.cancel(it?.let { e -> CancellationException(e.message, e) })
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

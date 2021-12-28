package test2

import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.suspendCancellableCoroutine


lateinit var continuation: CancellableContinuation<Int>

suspend fun run(): Int = suspendCancellableCoroutine { c ->
    c.invokeOnCancellation { println("ioc1") }
    continuation = c
}

suspend fun main() {
    val deferred = CompletableDeferred<Int>()
    deferred.invokeOnCompletion { println("handler1") }
    deferred.invokeOnCompletion { println("handler2") }

    deferred.complete(1)
    // coroutineScope {
    //     launch {
    //         println("num: ${run()}")
    //     }
    //     launch {
    //         delay(500)
    //         continuation.resume(1)
    //     }.join()
    // }
}
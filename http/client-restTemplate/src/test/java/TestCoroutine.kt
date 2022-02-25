/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     TestCoroutine.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *  *
 *  *
 *
 */

import kotlin.coroutines.*

/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     TestCoroutine.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *  *
 *  *
 *
 */





fun main() {


}


interface Wait<T> {
    // wakeup
    fun wakeup(value: T)
}


@RestrictsSuspension
interface WaitScope<T> {
    // wait.
    suspend fun wait(): T
}


fun <T> waitFor(block: suspend WaitScope<T>.() -> Unit): Wait<T> {
    return WaitImpl(block)
}


internal sealed class WaitState {
    object Init : WaitState()
    class Waiting<T>(val continuation: Continuation<T>) : WaitState()
    class Ready<T>(val continuation: Continuation<T>, val value: T) : WaitState()
    object Done : WaitState()
}


class WaitImpl<T>(
    private val block: suspend WaitScope<T>.() -> Unit,
) : Wait<T>, WaitScope<T>, Continuation<Any?> {

    override val context: CoroutineContext = EmptyCoroutineContext

    private var state: WaitState = WaitState.Init

    // init {
    //     val coroutineBlock: suspend WaitScope<T>.() -> Unit = { block() }
    //     val start = coroutineBlock.createCoroutine(this, this)
    //     state = WaitState.Waiting(start)
    // }

    /**
     * Resumes the execution of the corresponding coroutine passing a successful or failed [result] as the
     * return value of the last suspension point.
     */
    override fun resumeWith(result: Result<Any?>) {
        println("done.")
        state = WaitState.Done
        result.getOrThrow()
    }

    override fun wakeup(value: T) {
        println("state on wakeup: $state")
        when(val currentState = state) {
            is WaitState.Waiting<*> -> {
                currentState as WaitState.Waiting<T>
                println("weakup: is waiting.")
                state = WaitState.Ready(currentState.continuation, value)
                println("set ready. $state")
                currentState.continuation.resume(value)
                println("resume: $value")
            }
            else -> {}
        }
    }

    override suspend fun wait(): T {
        return suspendCoroutine { continuation ->
            when(val nowState = state) {
                is WaitState.Ready<*> -> {
                    println("wait ready!")
                    // nowState as WaitState.Ready<T>
                    println("wait get: ${nowState.value}")
                }
                is WaitState.Init -> {
                    state = WaitState.Waiting(continuation)
                }
                else -> {}
            }
        }
    }

}
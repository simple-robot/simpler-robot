/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     Generator.kt
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
 *  * File     Generator.kt
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


interface Generator<T> {
    operator fun iterator(): Iterator<T>
}


@RestrictsSuspension
interface GeneratorScope<T> {
    suspend fun yield(value: T)
}


class GeneratorImpl<T>(private val block: suspend GeneratorScope<T>.(T) -> Unit, private val value: T) : Generator<T> {
    override fun iterator(): Iterator<T> {
        return GeneratorIterator(block, value)
    }
}

fun <T> generator(block: suspend GeneratorScope<T>.(T) -> Unit): (T) -> Generator<T> {
    return { parameter: T ->
        GeneratorImpl(block, parameter)
    }
}


/**
 * [Generator] 的内部状态
 */
sealed class State {
    class NotReady(val continuation: Continuation<Unit>) : State()
    class Ready<T>(val continuation: Continuation<Unit>, val nextValue: T) : State()
    object Done : State()
}


/**
 * [Generator] 的迭代器，主要的生成逻辑。
 */
class GeneratorIterator<T>(
    private val block: suspend GeneratorScope<T>.(T) -> Unit,
    private val parameter: T,
) : GeneratorScope<T>, Iterator<T>, Continuation<Any?> {
    override val context: CoroutineContext = EmptyCoroutineContext

    private var state: State

    init {
        println("init")
        val coroutineBlock: suspend GeneratorScope<T>.() -> Unit = { block(parameter) }
        val start = coroutineBlock.createCoroutine(this, this)
        state = State.NotReady(start)
    }

    override suspend fun yield(value: T) = suspendCoroutine<Unit> { continuation ->
        println("yield")
        state = when (state) {
            is State.NotReady -> State.Ready(continuation, value)
            is State.Ready<*> -> throw IllegalStateException("Cannot yield while ready.")
            State.Done -> throw IllegalStateException("Cannot yield while done.")
        }
    }

    private fun resume() {
        println("resume")
        when (val currentState = state) {
            is State.NotReady -> currentState.continuation.resume(Unit)
        }
    }

    override fun hasNext(): Boolean {
        println("hasNext")
        resume()
        return state != State.Done
    }

    override fun next(): T {
        println("next")
        return when (val currentState = state) {
            is State.NotReady -> {
                resume()
                return next()
            }
            is State.Ready<*> -> {
                state = State.NotReady(currentState.continuation)
                @Suppress("UNCHECKED_CAST")
                (currentState as State.Ready<T>).nextValue
            }
            State.Done -> throw IndexOutOfBoundsException("No value left.")
        }
    }

    override fun resumeWith(result: Result<Any?>) {
        println("resumeWith")
        state = State.Done
        result.getOrThrow()
    }

}










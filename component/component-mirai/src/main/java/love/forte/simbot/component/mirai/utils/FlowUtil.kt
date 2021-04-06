/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */

package love.forte.simbot.component.mirai.utils

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.stream.consumeAsFlow
import java.util.*
import java.util.function.*
import java.util.stream.*


internal class FlowStream<T>(private val flow: Flow<T>) : Stream<T> {
    override fun close() {
        // ?
    }

    private suspend fun mutableList(): MutableList<T> =
        flow.toList().let { if (it is MutableList) it else it.toMutableList() }

    override fun iterator(): MutableIterator<T> = runBlocking {
        mutableList().iterator()
    }

    override fun spliterator(): Spliterator<T> = runBlocking {
        mutableList().spliterator()
    }

    override fun isParallel(): Boolean = false

    override fun sequential(): Stream<T> = this

    @Deprecated("FlowStream cannot parallel. return self.", ReplaceWith("this"))
    override fun parallel(): Stream<T> = this

    @Deprecated("FlowStream cannot unordered. return self.", ReplaceWith("this"))
    override fun unordered(): Stream<T> = this

    override fun onClose(closeHandler: Runnable?): Stream<T> {
        return closeHandler?.let { c -> FlowStream(flow.onCompletion { c.run() }) } ?: this
    }

    override fun filter(predicate: Predicate<in T>?): Stream<T> {
        return predicate?.let { p -> FlowStream(flow.filter { v -> p.test(v) }) } ?: this
    }

    override fun <R : Any?> map(mapper: java.util.function.Function<in T, out R>?): Stream<R> {
        return mapper?.let { m -> FlowStream(flow.map { v -> m.apply(v) }) } ?: throw NullPointerException("mapper")
    }

    override fun mapToInt(mapper: ToIntFunction<in T>?): IntStream {
        TODO("Not yet implemented")
    }

    override fun mapToLong(mapper: ToLongFunction<in T>?): LongStream {
        TODO("Not yet implemented")
    }

    override fun mapToDouble(mapper: ToDoubleFunction<in T>?): DoubleStream {
        TODO("Not yet implemented")
    }

    @FlowPreview
    override fun <R : Any?> flatMap(mapper: java.util.function.Function<in T, out Stream<out R>>?): Stream<R> {
        return mapper?.let { m -> FlowStream(flow.flatMapConcat { v -> m.apply(v).consumeAsFlow() }) }
            ?: throw NullPointerException("mapper")
    }

    override fun flatMapToInt(mapper: java.util.function.Function<in T, out IntStream>?): IntStream {
        TODO("Not yet implemented")
    }

    override fun flatMapToLong(mapper: java.util.function.Function<in T, out LongStream>?): LongStream {
        TODO("Not yet implemented")
    }

    override fun flatMapToDouble(mapper: java.util.function.Function<in T, out DoubleStream>?): DoubleStream {
        TODO("Not yet implemented")
    }

    // @FlowPreview
    // override fun <R : Any?> flatMap(mapper: Function<in T, out Stream<out R>>?): Stream<R> {
    //     return mapper?.let { m -> FlowStream(flow.flatMapConcat { v -> m.apply(v).consumeAsFlow() }) }
    //         ?: throw NullPointerException("mapper")
    // }
    //
    // override fun flatMapToInt(mapper: Function<in T, out IntStream>?): IntStream {
    //     TODO("Not yet implemented")
    // }
    //
    // override fun flatMapToLong(mapper: Function<in T, out LongStream>?): LongStream {
    //     TODO("Not yet implemented")
    // }
    //
    // override fun flatMapToDouble(mapper: Function<in T, out DoubleStream>?): DoubleStream {
    //     TODO("Not yet implemented")
    // }

    override fun distinct(): Stream<T> {
        return FlowStream(flow.distinctUntilChanged())
    }


    @Deprecated("FlowStream cannot sorted. return self.", ReplaceWith("this"))
    override fun sorted(): Stream<T> = this

    @Deprecated("FlowStream cannot sorted. return self.", ReplaceWith("this"))
    override fun sorted(comparator: Comparator<in T>?): Stream<T> = this

    override fun peek(action: Consumer<in T>?): Stream<T> {
        return action?.let { a -> FlowStream(flow.onEach { v -> a.accept(v) }) } ?: this
    }

    override fun limit(maxSize: Long): Stream<T> {
        return FlowStream(flow.take(maxSize.toInt()))
    }

    override fun skip(n: Long): Stream<T> {
        return FlowStream(flow.drop(n.toInt()))
    }

    override fun forEach(action: Consumer<in T>?) {
        runBlocking {
            action?.let { a -> flow.collect { v -> a.accept(v) } }
        }
    }

    override fun forEachOrdered(action: Consumer<in T>?) {
        forEach(action)
    }

    override fun toArray(): Array<Any> = runBlocking {
        val list = flow.toList()
        Array(list.size) { i -> list[i] as Any }
    }


    @Suppress("UNCHECKED_CAST")
    override fun <A : Any?> toArray(generator: IntFunction<Array<A>>?): Array<A> {
        return generator?.let { g ->
            runBlocking {
                val list = flow.toList()
                val arr = g.apply(list.size)
                list.forEachIndexed { i, t -> arr[i] = t as A }
                arr
            }
        } ?: throw NullPointerException("generator")
    }

    override fun reduce(identity: T, accumulator: BinaryOperator<T>?): T {
        return accumulator?.let { a -> runBlocking { flow.reduce { acc, value -> a.apply(acc, value) } } }
            ?: throw NullPointerException("accumulator")
    }

    override fun reduce(accumulator: BinaryOperator<T>?): Optional<T> {
        return accumulator?.let { a ->
            Optional.ofNullable(runBlocking { flow.reduce { acc, value -> a.apply(acc, value) } })
        } ?: throw NullPointerException("accumulator")

    }

    override fun <U : Any?> reduce(identity: U, accumulator: BiFunction<U, in T, U>?, combiner: BinaryOperator<U>?): U {
        TODO("Not yet implemented")
    }

    override fun <R : Any?> collect(
        supplier: Supplier<R>?,
        accumulator: BiConsumer<R, in T>?,
        combiner: BiConsumer<R, R>?,
    ): R {
        TODO("Not yet implemented")
    }

    override fun <R : Any?, A : Any?> collect(collector: Collector<in T, A, R>?): R {
        TODO("Not yet implemented")
    }

    override fun min(comparator: Comparator<in T>?): Optional<T> {
        TODO("Not yet implemented")
    }

    override fun max(comparator: Comparator<in T>?): Optional<T> {
        TODO("Not yet implemented")
    }

    override fun count(): Long {
        TODO("Not yet implemented")
    }

    override fun anyMatch(predicate: Predicate<in T>?): Boolean {
        TODO("Not yet implemented")
    }

    override fun allMatch(predicate: Predicate<in T>?): Boolean {
        TODO("Not yet implemented")
    }

    override fun noneMatch(predicate: Predicate<in T>?): Boolean {
        TODO("Not yet implemented")
    }

    override fun findFirst(): Optional<T> {
        TODO("Not yet implemented")
    }

    override fun findAny(): Optional<T> {
        TODO("Not yet implemented")
    }
}


/**
 * [Flow] as/to [Stream].
 */
public suspend fun <T> Flow<T>.asStream(): Stream<T> {
    return this.toList().stream()
}

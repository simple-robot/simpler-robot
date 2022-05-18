/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 */

package love.forte.simbot.utils.sequence

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.*
import love.forte.simbot.Api4J
import love.forte.simbot.BlockingApi
import love.forte.simbot.utils.runInBlocking
import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference
import java.util.stream.Stream
import kotlin.coroutines.CoroutineContext
import kotlin.streams.asStream

/**
 * 通过 [Kotlin Flow][Flow] 对 [ItemFlow] 进行基础实现。
 *
 * // TODO 描述 scope 和 asSequence.
 *
 *
 * @author ForteScarlet
 */
internal class SimpleItemFlow<out V>(private var flow: Flow<V>) : ItemFlow<V>, CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO
    
    override fun filter(matcher: suspend (V) -> Boolean): ItemFlow<V> = also {
        flow = flow.filter(matcher)
    }
    
    @BlockingApi
    override fun filter(matcher: Matcher<V>): ItemFlow<V> = filter { matcher(it) }
    
    override fun <T> map(mapper: suspend (V) -> T): ItemFlow<T> {
        return SimpleItemFlow(flow.map(mapper))
    }
    
    @BlockingApi
    override fun <T> map(mapper: Mapper<V, T>): ItemFlow<T> {
        return map { mapper(it) }
    }
    
    override suspend fun collect(visitor: suspend (V) -> Unit) {
        flow.collect { visitor(it) }
    }
    
    
    @BlockingApi
    override fun collect(visitor: Visitor<V>): Unit = runInBlocking {
        flow.collect {
            visitor(it)
        }
    }
    
    override suspend fun <C : MutableCollection<in V>> collection(destination: C): C {
        return flow.toCollection(destination)
    }
    
    @BlockingApi
    override fun <C : MutableCollection<in V>> collectTo(destination: C): C = runInBlocking { collection(destination) }
    
    @BlockingApi
    override fun toList(): List<V> = collectTo(mutableListOf())
    
    override suspend fun collectToList(): List<V> = flow.toList()
    
    override fun asFlow(): Flow<V> = flow
    
    private inner class WeakSeq(seq: Sequence<*>, q: ReferenceQueue<Sequence<*>>, val channel: ReceiveChannel<V>) :
        WeakReference<Sequence<*>>(seq, q)
    
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun flowToSequence(): Sequence<V> {
        val lock = Any()
        var closed = false
        var job: Job? = null
        
        val channel: ReceiveChannel<V> = produce {
            invokeOnClose {
                synchronized(lock) {
                    closed = true
                    job?.cancel()
                }
            }
            flow.collect {
                send(it)
            }
            close()
        }
        
        
        val channelIterator = channel.iterator()
        val seq = sequence {
            while (runInBlocking { channelIterator.hasNext() }) {
                val next = runInBlocking { channelIterator.next() }
                yield(next)
            }
            channel.cancel()
        }
        synchronized(lock) {
            if (!closed) {
                val q = ReferenceQueue<Sequence<*>>()
                val weak = WeakSeq(seq, q, channel)
                job = launch {
                    // check the q
                    while (isActive && !closed) {
                        weak.get()
                        val poll = q.poll()
                        if (poll != null) {
                            channel.cancel()
                            break
                        }
                        delay(500)
                    }
                    job?.cancel()
                }
            }
        }
        
        
        
        
        return seq
    }
    
    @BlockingApi
    override fun asSequence(): Sequence<V> = flowToSequence()
    
    
    @Api4J
    override fun asStream(): Stream<out V> = flowToSequence().asStream()
}
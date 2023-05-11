/*
 * Copyright (c) 2022-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.utils.item

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.stream.consumeAsFlow
import love.forte.simbot.Api4J
import love.forte.simbot.utils.runInNoScopeBlocking
import java.util.function.Consumer
import java.util.stream.Collectors
import java.util.stream.Stream
import kotlin.streams.asSequence


/**
 * 使用 [Stream] 实现 [Items] 的基本约定。
 *
 * @author ForteScarlet
 */
@Api4J
public class StreamItems<T> private constructor(private val streamFactory: (Items.PreprocessingProperties) -> Stream<T>) : BaseItems<T, StreamItems<T>>() {
    override val self: StreamItems<T>
        get() = this
    
    private val stream: Stream<T> get() = streamFactory(preprocessingProperties)
    
    override fun asFlow(): Flow<T> {
        return stream.consumeAsFlow()
    }
    override fun asSequence(): Sequence<T> {
        return stream.asSequence()
    }
    
    @Api4J
    override fun asStream(): Stream<out T> {
        return stream
    }
    
    override suspend fun collect(collector: suspend (T) -> Unit) {
        stream.forEach {
            runInNoScopeBlocking { collector(it) }
        }
    }
    
    @Api4J
    override fun collect(collector: Consumer<in T>) {
        stream.forEach(collector)
    }
    
    @Api4J
    override fun <C : MutableCollection<in T>> collectTo(collector: C): C {
        return stream.collect(Collectors.toCollection { collector })
    }
    
    @Api4J
    override fun collectToList(): List<T> {
        return stream.collect(Collectors.toList())
    }
    
    public companion object {
    
        /**
         * 通过最基本的 [构建函数][streamFactory] 创建一个以 [Stream] 为目标的 [Items] 实例。
         *
         * 需要自行处理 [Items.PreprocessingProperties] 参数所提供的内容。或者参考 [newEffectedInstance].
         *
         */
        @JvmStatic
        public fun <T> newInstance(streamFactory: (Items.PreprocessingProperties) -> Stream<T>): Items<T> {
            return StreamItems(streamFactory)
        }
    
        /**
         * 通过最基本的构建函数创建一个以 [Stream] 为目标的 [Items] 实例。
         *
         * [构建函数][streamFactory] 创建的 [Stream] 最终会被自动通过 [Items.PreprocessingProperties.effectOn] 所作用。
         *
         */
        @JvmStatic
        public fun <T> newEffectedInstance(streamFactory: () -> Stream<T>): Items<T> {
            return newInstance { pre ->
                pre.effectOn(streamFactory())
            }
        }
    
    }
    
}

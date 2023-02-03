/*
 * Copyright (c) 2022-2023 ForteScarlet <ForteScarlet@163.com>
 *
 * 本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x 、simbot3 等) 的一部分。
 * simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 * 发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 * 你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 * https://www.gnu.org/licenses
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 * https://www.gnu.org/licenses/lgpl-3.0-standalone.html
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

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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ChannelIterator
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.experimental.ExperimentalTypeInference

/**
 * 提供构建 [ChannelIterator] 的函数来构建一个 [Items].
 *
 * 需要自行处理 [Items.PreprocessingProperties] 所提供的预处理参数。
 */
@JvmSynthetic
public fun <T> itemsBy(factory: (Items.PreprocessingProperties) -> ChannelIterator<T>): Items<T> {
    return SimpleChannelIteratorItems(factory)
}

/**
 * 通过 [produce] 构建 [ChannelIterator] 来得到一个 [Items] 实例。
 *
 * 需要自行处理 [Items.PreprocessingProperties] 所提供的预处理参数。
 *
 * @see produce
 * @see itemsBy
 */
@OptIn(ExperimentalCoroutinesApi::class)
public inline fun <T> CoroutineScope.produceItems(
    context: CoroutineContext = EmptyCoroutineContext,
    capacity: Int = 0,
    crossinline block: suspend ProducerScope<T>.(Items.PreprocessingProperties) -> Unit,
): Items<T> {
    return itemsBy { pre ->
        produce(context, capacity) {
            block(pre)
        }.iterator()
    }
}

/**
 * 提供 [CoroutineScope] 和构建 [Flow] 的函数 [flowFactory] 来构建 [Items].
 *
 * 需要自行处理 [Items.PreprocessingProperties] 所提供的预处理参数。
 */
public inline fun <T> CoroutineScope.itemsByFlow(crossinline flowFactory: (Items.PreprocessingProperties) -> Flow<T>): Items<T> {
    return FlowItems(this) {
        flowFactory(it)
    }
}

/**
 * 提供 [CoroutineScope] 和构建 [Flow] 的函数 [flowFactory] 来构建 [Items].
 *
 * 不提供 [Items.PreprocessingProperties], 取而代之的是自动将 [Items.PreprocessingProperties] 作用于 [flowFactory]
 * 所构建出来的 [Flow] 实例上。
 *
 * @see Items.PreprocessingProperties.effectOn
 */
public inline fun <T> CoroutineScope.effectedItemsByFlow(crossinline flowFactory: () -> Flow<T>): Items<T> {
    return itemsByFlow { prop ->
        prop.effectOn(flowFactory())
    }
}

/**
 * 提供构建 [Sequence] 的函数 [sequenceFactory] 来构建 [Items].
 *
 * 需要自行处理 [Items.PreprocessingProperties] 所提供的预处理参数。
 */
public inline fun <T> itemsBySequence(crossinline sequenceFactory: (Items.PreprocessingProperties) -> Sequence<T>): Items<T> {
    return SequenceItems {
        sequenceFactory(it)
    }
}

/**
 * 提供构建 [Sequence] 的函数 [sequenceFactory] 来构建 [Items].
 *
 * 不提供 [Items.PreprocessingProperties], 取而代之的是自动将 [Items.PreprocessingProperties] 作用于 [sequenceFactory]
 * 所构建出来的 [Sequence] 实例上。
 *
 * @see Items.PreprocessingProperties.effectOn
 */
public inline fun <T> effectedItemsBySequence(crossinline sequenceFactory: () -> Sequence<T>): Items<T> {
    return itemsBySequence { prop ->
        prop.effectOn(sequenceFactory())
    }
}




/**
 * 以构建 [Flow] 的方式构建 [Items].
 */
@OptIn(ExperimentalTypeInference::class)
public inline fun <T> CoroutineScope.flowItems(@BuilderInference crossinline block: suspend FlowCollector<T>.(Items.PreprocessingProperties) -> Unit): Items<T> {
    return itemsByFlow { pre ->
        flow {
            block(pre)
        }
    }
}

/**
 * 以构建 [Flow] 的方式构建 [Items].
 *
 * 不提供 [Items.PreprocessingProperties], 取而代之的是自动将 [Items.PreprocessingProperties] 作用于 [block]
 * 所构建出来的 [Flow] 实例上。
 */
@OptIn(ExperimentalTypeInference::class)
public inline fun <T> CoroutineScope.effectedFlowItems(@BuilderInference crossinline block: suspend FlowCollector<T>.() -> Unit): Items<T> {
    return itemsByFlow { prop ->
        prop.effectedFlow { block() }
    }
}

/**
 * 以构建 [Sequence] 的方式构建 [Items].
 */
@OptIn(ExperimentalTypeInference::class)
public inline fun <T> sequenceItems(@BuilderInference crossinline block: suspend SequenceScope<T>.(Items.PreprocessingProperties) -> Unit): Items<T> {
    return itemsBySequence { pre ->
        sequence {
            block(pre)
        }
    }
}

/**
 * 以构建 [Sequence] 的方式构建 [Items].
 *
 * 不提供 [Items.PreprocessingProperties], 取而代之的是自动将 [Items.PreprocessingProperties] 作用于 [block]
 * 所构建出来的 [Sequence] 实例上。
 */
@OptIn(ExperimentalTypeInference::class)
public inline fun <T> effectedSequenceItems(@BuilderInference crossinline block: suspend SequenceScope<T>.() -> Unit): Items<T> {
    return itemsBySequence { prop ->
        prop.effectedSequence { block() }
    }
}


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
import kotlinx.coroutines.flow.map
import love.forte.simbot.Api4J
import love.forte.simbot.utils.runInNoScopeBlocking
import java.util.stream.Stream

/**
 *
 * 允许为一个 [Items] 提供一个转化函数的 [Items] 代理实现。
 *
 * @author ForteScarlet
 */
public class TransformItems<B, T>(
    private val baseItems: Items<B>,
    private val transform: suspend (B) -> T,
) : Items<T> {
    override fun limit(count: Int): Items<T> {
        baseItems.limit(count)
        return this
    }
    
    override fun offset(count: Int): Items<T> {
        baseItems.offset(count)
        return this
    }
    
    override fun batch(size: Int): Items<T> {
        baseItems.batch(size)
        return this
    }
    
    override suspend fun collect(collector: suspend (T) -> Unit) {
        baseItems.collect { collector(transform(it)) }
    }
    
    override fun asFlow(): Flow<T> {
        return baseItems.asFlow().map(transform)
    }
    
    override fun asSequence(): Sequence<T> {
        return baseItems.asSequence().map { runInNoScopeBlocking { transform(it) } }
    }
    
    @Api4J
    override fun asStream(): Stream<out T> {
        return baseItems.asStream().map { runInNoScopeBlocking { transform(it) } }
    }
}

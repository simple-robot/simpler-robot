/*
 * Copyright (c) 2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.collection

import kotlinx.coroutines.flow.Flow
import java.util.stream.Stream
import kotlin.streams.asStream

/**
 * 一个可收集序列器。
 *
 * [Collectable] 应当支持将自身（或其中实际的元素收集方式）
 * 转化为可挂起的流（[Flow]）或同步序列（[Sequence]）。
 *
 * [Collectable] 本身可能含有一个热流或冷流，因此 [Collectable] 不保证可以多次使用，
 * 也不建议多次调用 [Collectable] 的转化函数。[Collectable] 应当仅至多调用一次转化函数。
 *
 * @author ForteScarlet
 */
public actual interface Collectable<out T> {
    public actual fun asFlow(): Flow<T>
    public actual fun asSequence(): Sequence<T>

    /**
     * 将自身中的元素（或收集器）转化为 [Stream]。
     */
    public fun toStream(): Stream<@UnsafeVariance T> = asSequence().asStream()
}

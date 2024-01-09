/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package love.forte.simbot.common.collection

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ChannelIterator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.produceIn


/**
 * Converts a [Flow] into an [Iterator] by providing custom implementations for the `hasNext` and `next` functions.
 *
 * @param producerScope The [CoroutineScope] that the produced [Flow] will be associated with.
 * @param hasNext A lambda expression that returns `true` if there are more elements in the [Flow], or `false` otherwise.
 * @param next A lambda expression that returns the next element in the [Flow].
 * @return An [Iterator] that can be used to iterate over the elements in the [Flow].
 */
public inline fun <T> Flow<T>.asIterator(
    producerScope: CoroutineScope,
    crossinline hasNext: ChannelIterator<T>.() -> Boolean,
    crossinline next: ChannelIterator<T>.() -> T
): Iterator<T> {
    val iterator = produceIn(producerScope).iterator()
    return iterator {
        while (hasNext(iterator)) {
            val nextValue = next(iterator)
            yield(nextValue)
        }
    }
}

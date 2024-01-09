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

/**
 * Copy and to immutable collections.
 */
public actual fun <T> Collection<T>.toImmutable(): Collection<T> {
    return if (this is Set<T>) {
        when {
            isEmpty() -> emptySet()
            size == 1 -> setOf(first())
            else -> toSet()
        }
    } else {
        when {
            isEmpty() -> emptyList()
            size == 1 -> listOf(first())
            else -> toList()
        }
    }
}

/**
 * 创建一个优先级并发队列 [PriorityConcurrentQueue] 。
 *
 * 优先级并发队列允许在多个线程中同时添加、读取和删除元素，
 * 并且元素的出队顺序会根据它们的优先级来确定。
 *
 * @return 返回一个新创建的优先级并发队列。
 */
public actual fun <T> createPriorityConcurrentQueue(): PriorityConcurrentQueue<T> = PriorityConcurrentQueueImpl()

/**
 * 创建一个普通的并发队列 [ConcurrentQueue] 。
 *
 * 并发队列允许在多个线程中同时添加、读取和删除元素。
 *
 * @return 返回一个新创建的并发队列。
 */
public actual fun <T> createConcurrentQueue(): ConcurrentQueue<T> = ConcurrentQueueImpl()

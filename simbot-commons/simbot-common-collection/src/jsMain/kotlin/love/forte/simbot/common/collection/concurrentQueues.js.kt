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


internal class ConcurrentQueueImpl<T> : ConcurrentQueue<T> {
    private val list = mutableListOf<T>()

    override fun add(value: T) {
        list.add(value)
    }

    override fun remove(value: T) {
        list.remove(value)
    }

    override fun removeIf(predicate: (T) -> Boolean) {
        list.removeAll(predicate)
    }

    override fun iterator(): Iterator<T> = list.toList().iterator()

    override fun toString(): String = list.toString()
}


internal class PriorityConcurrentQueueImpl<T> : PriorityConcurrentQueue<T> {
    private val lists = mutableMapOf<Int, MutableList<T>>()

    override fun add(priority: Int, value: T) {
        val list = lists.getOrPut(priority) { mutableListOf() }
        list.add(value)
    }

    override fun remove(priority: Int, target: T) {
        val list = lists[priority] ?: return
        if (list.removedAndEmpty(target)) {
            // removed and empty, remove list
            lists.remove(priority)
        }
    }

    override fun removeIf(priority: Int, predicate: (T) -> Boolean) {
        val list = lists[priority] ?: return
        if (list.removedAllAndEmpty(predicate)) {
            // removed and empty, remove list
            lists.remove(priority)
        }
    }

    override fun remove(target: T) {
        with(lists.values.iterator()) {
            while (hasNext()) {
                val value = next()
                if (value.remove(target)) {
                    if (value.isEmpty()) {
                        remove()
                    }
                    break
                }
            }
        }
    }

    override fun removeIf(predicate: (T) -> Boolean) {
        lists.values.removeAll { list -> list.removedAllAndEmpty(predicate) }
    }

    private fun <T> MutableList<T>.removedAndEmpty(target: T): Boolean = remove(target) && isEmpty()
    private fun <T> MutableList<T>.removedAllAndEmpty(predicate: (T) -> Boolean): Boolean =
        removeAll(predicate) && isEmpty()

    override fun iterator(): Iterator<T> {
        val sorted = lists.toMap().entries.sortedBy { it.key }
        return iterator {
            sorted.forEach { (_, v) ->
                v.forEach { yield(it) }
            }
        }
        // return lists.toMap().asSequence().sortedBy { it.key }.flatMap { it.value }.iterator()
    }

    override fun toString(): String = lists.toString()
}

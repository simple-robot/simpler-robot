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

import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.ConcurrentSkipListMap

internal class ConcurrentQueueImpl<T> : ConcurrentQueue<T> {
    private val queue = ConcurrentLinkedQueue<T>()

    override fun add(value: T) {
        queue.add(value)
    }

    override fun remove(value: T) {
        queue.remove(value)
    }

    override fun removeIf(predicate: (T) -> Boolean) {
        queue.removeIf(predicate)
    }

    override fun iterator(): Iterator<T> = queue.iterator()

    override fun toString(): String = queue.toString()
}

/**
 *
 * @author ForteScarlet
 */
internal class PriorityConcurrentQueueImpl<T> : PriorityConcurrentQueue<T> {
    private val queueMap = ConcurrentSkipListMap<Int, ConcurrentLinkedQueue<T>>()

    override fun add(priority: Int, value: T) {
        val queue = queueMap.computeIfAbsent(priority) { ConcurrentLinkedQueue() }
        queue.add(value)
    }

    override fun remove(priority: Int, target: T) {
        queueMap.compute(priority) { _, q ->
            if (q != null) {
                q.remove(target)
                q.takeIf { it.isNotEmpty() }
            } else {
                null
            }
        }
    }

    override fun removeIf(priority: Int, predicate: (T) -> Boolean) {
        queueMap.compute(priority) { _, q ->
            if (q != null) {
                q.removeIf(predicate)
                q.takeIf { it.isNotEmpty() }
            } else {
                null
            }
        }
    }

    override fun remove(target: T) {
        for (entry in queueMap) {
            val (priority, queue) = entry
            if (queue.remove(target)) {
                queueMap.compute(priority) { _, queue0 ->
                    queue0?.takeIf { it.isNotEmpty() }
                }
                break
            }
        }
    }

    override fun removeIf(predicate: (T) -> Boolean) {
        for (entry in queueMap) {
            val (priority, queue) = entry
            if (queue.removeIf(predicate)) {
                queueMap.compute(priority) { _, queue0 ->
                    queue0?.takeIf { it.isNotEmpty() }
                }
            }
        }
    }

    override fun iterator(): Iterator<T> {
        return Iter()
    }

    private inner class Iter : Iterator<T> {
        private val entries = queueMap.entries.iterator()

        @Volatile
        private var currentIter: Iterator<T>? = nextIter()

        private fun nextIter(): Iterator<T>? {
            return entries.takeIf { it.hasNext() }?.next()?.value?.iterator()
        }

        override fun hasNext(): Boolean {
            var ci = currentIter
            while (ci != null && !ci.hasNext()) {
                ci = nextIter()
                currentIter = ci
            }

            return ci != null
        }

        override fun next(): T {
            if (!hasNext()) {
                throw NoSuchElementException()
            }

            return currentIter!!.next()
        }
    }

    override fun toString(): String = queueMap.toString()

}

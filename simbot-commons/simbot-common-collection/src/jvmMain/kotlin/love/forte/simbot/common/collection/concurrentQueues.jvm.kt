/*
 *     Copyright (c) 2024-2026. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
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
import java.util.concurrent.atomic.AtomicInteger

@OptIn(ExperimentalSimbotCollectionApi::class)
internal class ConcurrentQueueImpl<T> : ConcurrentQueue<T> {
    private val queue = ConcurrentLinkedQueue<T>()

    override val size: Int
        get() = queue.size

    override fun isEmpty(): Boolean =
        queue.isEmpty()

    override fun add(value: T) {
        queue.add(value)
    }

    override fun remove(value: T) {
        queue.remove(value)
    }

    override fun removeIf(predicate: (T) -> Boolean) {
        queue.removeIf(predicate)
    }

    override fun clear() {
        queue.clear()
    }

    override fun iterator(): Iterator<T> = queue.iterator()

    override fun toString(): String = queue.toString()
}

/**
 *
 * @author ForteScarlet
 */
@OptIn(ExperimentalSimbotCollectionApi::class)
internal class PriorityConcurrentQueueImpl<T> : PriorityConcurrentQueue<T> {
    private val queueMap = ConcurrentSkipListMap<Int, PriorityBucket<T>>()

    override val size: Int
        get() {
            var count = 0L
            for (bucket in queueMap.values) {
                count += bucket.queue.size
                if (count >= Int.MAX_VALUE) {
                    return Int.MAX_VALUE
                }
            }
            return count.toInt()
        }

    override fun isEmpty(priority: Int): Boolean =
        queueMap[priority]?.let { it.isClosed() || it.queue.isEmpty() } ?: true

    override fun isEmpty(): Boolean =
        queueMap.values.all { it.isClosed() || it.queue.isEmpty() }

    override fun add(priority: Int, value: T) {
        while (true) {
            val bucket = bucketFor(priority)
            if (bucket.addIfActive(value) { queueMap[priority] === bucket }) {
                if (bucket.queue.isEmpty()) {
                    removeBucketIfEmpty(priority, bucket)
                }
                return
            }

            removeBucketIfEmpty(priority, bucket)
        }
    }

    override fun remove(priority: Int, target: T) {
        val bucket = queueMap[priority] ?: return
        try {
            bucket.queue.remove(target)
        } finally {
            removeBucketIfEmpty(priority, bucket)
        }
    }

    override fun removeIf(priority: Int, predicate: (T) -> Boolean) {
        val bucket = queueMap[priority] ?: return
        try {
            bucket.queue.removeIf(predicate)
        } finally {
            removeBucketIfEmpty(priority, bucket)
        }
    }

    override fun remove(target: T) {
        for ((priority, bucket) in queueMap) {
            val removed = try {
                bucket.queue.remove(target)
            } finally {
                removeBucketIfEmpty(priority, bucket)
            }
            if (removed) {
                return
            }
        }
    }

    override fun removeIf(predicate: (T) -> Boolean) {
        for ((priority, bucket) in queueMap) {
            try {
                bucket.queue.removeIf(predicate)
            } finally {
                removeBucketIfEmpty(priority, bucket)
            }
        }
    }

    override fun clear() {
        queueMap.clear()
    }

    override fun iterator(): Iterator<T> {
        return Iter()
    }

    private inner class Iter : Iterator<T> {
        private val entries = queueMap.entries.iterator()

        private var currentIter: Iterator<T>? = nextIter()

        private fun nextIter(): Iterator<T>? {
            return entries.takeIf { it.hasNext() }?.next()?.value?.queue?.iterator()
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

    override fun toString(): String {
        val iterator = this.iterator()
        if (!iterator.hasNext()) {
            return "[]"
        }

        val sb = StringBuilder()
        sb.append('[')

        while (true) {
            val value = iterator.next()
            sb.append(if (value === this) "(this queue)" else value)
            if (!iterator.hasNext()) {
                sb.append(']')
                return sb.toString()
            }

            sb.append(',').append(' ')
        }
    }

    private fun bucketFor(priority: Int): PriorityBucket<T> {
        while (true) {
            val current = queueMap[priority]
            val bucket = current ?: PriorityBucket<T>().let { candidate ->
                queueMap.putIfAbsent(priority, candidate) ?: candidate
            }
            if (!bucket.isClosed()) {
                return bucket
            }

            removeClosedBucket(priority, bucket)
        }
    }

    private fun removeBucketIfEmpty(priority: Int, bucket: PriorityBucket<T>) {
        if (bucket.closeIfEmpty()) {
            removeClosedBucket(priority, bucket)
        }
    }

    private fun removeClosedBucket(priority: Int, bucket: PriorityBucket<T>) {
        queueMap.remove(priority, bucket)
    }

    private class PriorityBucket<T>(
        val queue: ConcurrentLinkedQueue<T> = ConcurrentLinkedQueue(),
        private val state: AtomicInteger = AtomicInteger(BUCKET_ACTIVE)
    ) {
        fun isClosed(): Boolean = state.get() == BUCKET_CLOSED

        fun addIfActive(value: T, isCurrent: () -> Boolean): Boolean {
            if (!enterAdd()) {
                return false
            }

            try {
                if (!isCurrent()) {
                    return false
                }
                queue.add(value)
                return true
            } finally {
                leaveAdd()
            }
        }

        @Suppress("ReturnCount")
        fun closeIfEmpty(): Boolean {
            while (true) {
                val current = state.get()
                when {
                    current == BUCKET_CLOSED -> return true
                    current > BUCKET_ACTIVE -> return false
                    current == BUCKET_ACTIVE -> {
                        if (queue.isNotEmpty()) {
                            return false
                        }
                        if (!state.compareAndSet(BUCKET_ACTIVE, BUCKET_CLOSING)) {
                            continue
                        }
                    }
                }

                if (queue.isEmpty()) {
                    if (state.compareAndSet(BUCKET_CLOSING, BUCKET_CLOSED)) {
                        return true
                    }
                } else if (state.compareAndSet(BUCKET_CLOSING, BUCKET_ACTIVE)) {
                    return false
                }
            }
        }

        private fun enterAdd(): Boolean {
            while (true) {
                val current = state.get()
                if (current < BUCKET_ACTIVE) {
                    return false
                }
                check(current < Int.MAX_VALUE)

                if (state.compareAndSet(current, current + 1)) {
                    return true
                }
            }
        }

        private fun leaveAdd() {
            check(state.decrementAndGet() >= BUCKET_ACTIVE)
        }
    }

    private companion object {
        const val BUCKET_CLOSED = -1
        const val BUCKET_CLOSING = -2
        const val BUCKET_ACTIVE = 0
    }
}

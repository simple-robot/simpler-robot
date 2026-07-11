/*
 *     Copyright (c) 2026. ForteScarlet.
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

import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi

@OptIn(ExperimentalAtomicApi::class)
@ExperimentalSimbotCollectionApi
internal class ConcurrentQueueImpl<T> : ConcurrentQueue<T> {
    private class Node<T>(val value: T?, removed: Boolean = false) {
        val next: AtomicReference<Node<T>?> = AtomicReference(null)
        val removed: AtomicBoolean = AtomicBoolean(removed)
        val isRemoved: Boolean get() = removed.load()
    }

    private val head = Node<T>(value = null, removed = true)
    private val tail = AtomicReference(head)

    override val size: Int
        get() {
            var count = 0
            val stop = tail.load()
            var node = head.next.load()
            while (node != null) {
                if (!node.isRemoved) {
                    if (count == Int.MAX_VALUE) {
                        return Int.MAX_VALUE
                    }
                    count++
                }
                if (node === stop) {
                    break
                }
                node = node.next.load()
            }
            return count
        }

    override fun isEmpty(): Boolean = firstLiveNode() == null

    override fun add(value: T) {
        val node = Node(value)

        while (true) {
            val currentTail = tail.load()
            val next = currentTail.next.load()

            if (next == null) {
                if (currentTail.next.compareAndSet(expectedValue = null, newValue = node)) {
                    tail.compareAndSet(expectedValue = currentTail, newValue = node)
                    return
                }
            } else {
                tail.compareAndSet(expectedValue = currentTail, newValue = next)
            }
        }
    }

    override fun remove(value: T) {
        removeFirst(value)
    }

    fun removeFirst(value: T): Boolean {
        val stop = tail.load()
        var previous = head
        var node = head.next.load()
        while (node != null) {
            val next = node.next.load()
            if (node.isRemoved) {
                if (next != null) {
                    previous.next.compareAndSet(expectedValue = node, newValue = next)
                }
            } else if (node.value == value) {
                if (node.removed.compareAndSet(expectedValue = false, newValue = true)) {
                    if (next != null) {
                        previous.next.compareAndSet(expectedValue = node, newValue = next)
                    }
                    return true
                }
            } else {
                previous = node
            }
            if (node === stop) {
                break
            }
            node = next
        }

        return false
    }

    override fun removeIf(predicate: (T) -> Boolean) {
        removeAllIf(predicate)
    }

    fun removeAllIf(predicate: (T) -> Boolean): Boolean {
        var changed = false
        val stop = tail.load()
        var node = head.next.load()

        try {
            while (node != null) {
                val next = node.next.load()
                if (!node.isRemoved) {
                    @Suppress("UNCHECKED_CAST")
                    val value = node.value as T
                    if (predicate(value) && node.removed.compareAndSet(expectedValue = false, newValue = true)) {
                        changed = true
                    }
                }
                if (node === stop) {
                    break
                }
                node = next
            }
        } finally {
            if (changed) {
                unlinkRemovedNodes()
            }
        }

        return changed
    }

    override fun iterator(): Iterator<T> = QueueIterator(this, head.next.load())

    override fun clear() {
        val stop = tail.load()
        var node = head.next.load()
        while (node != null) {
            node.removed.store(true)
            if (node === stop) {
                break
            }
            node = node.next.load()
        }
        unlinkRemovedNodes()
    }

    override fun toString(): String = buildList {
        val stop = tail.load()
        var node = head.next.load()
        while (node != null) {
            if (!node.isRemoved) {
                @Suppress("UNCHECKED_CAST")
                add(node.value as T)
            }
            if (node === stop) {
                break
            }
            node = node.next.load()
        }
    }.toString()

    private fun firstLiveNode(): Node<T>? {
        val stop = tail.load()
        var node = head.next.load()
        while (node != null) {
            if (!node.isRemoved) {
                return node
            }
            if (node === stop) {
                break
            }
            node = node.next.load()
        }

        return null
    }

    private fun nextLiveNode(from: Node<T>?): Node<T>? {
        var node = from
        while (node != null) {
            if (!node.isRemoved) {
                return node
            }
            node = node.next.load()
        }

        return null
    }

    private fun nextAfter(node: Node<T>): Node<T>? = node.next.load()

    private fun unlinkRemovedNodes() {
        val stop = tail.load()
        var previous = head
        var node = previous.next.load()

        while (node != null) {
            val next = node.next.load()
            if (node.isRemoved && next != null) {
                previous.next.compareAndSet(expectedValue = node, newValue = next)
            } else {
                previous = node
            }
            if (node === stop) {
                break
            }
            node = next
        }

        val currentTail = tail.load()
        val next = currentTail.next.load()
        if (next != null) {
            tail.compareAndSet(expectedValue = currentTail, newValue = next)
        }
    }

    private class QueueIterator<T>(
        private val queue: ConcurrentQueueImpl<T>,
        start: Node<T>?
    ) : Iterator<T> {
        private var candidate: Node<T>? = start
        private var nextNode: Node<T>? = null

        override fun hasNext(): Boolean {
            val node = nextNode ?: queue.nextLiveNode(candidate)
            nextNode = node
            candidate = node
            return node != null
        }

        override fun next(): T {
            val node = nextNode ?: queue.nextLiveNode(candidate) ?: throw NoSuchElementException()
            nextNode = null
            candidate = queue.nextAfter(node)

            @Suppress("UNCHECKED_CAST")
            return node.value as T
        }
    }
}

@OptIn(ExperimentalAtomicApi::class)
@ExperimentalSimbotCollectionApi
internal class PriorityConcurrentQueueImpl<T> : PriorityConcurrentQueue<T> {
    private val buckets = AtomicReference<List<PriorityBucket<T>>>(emptyList())

    override val size: Int
        get() {
            var count = 0L
            for (bucket in buckets.load()) {
                count += bucket.queue.size
                if (count >= Int.MAX_VALUE) {
                    return Int.MAX_VALUE
                }
            }
            return count.toInt()
        }

    override fun isEmpty(priority: Int): Boolean =
        buckets.load().findBucket(priority)?.let { it.isClosed() || it.queue.isEmpty() } ?: true

    override fun isEmpty(): Boolean =
        buckets.load().all { it.isClosed() || it.queue.isEmpty() }

    override fun add(priority: Int, value: T) {
        while (true) {
            val bucket = bucketFor(priority)
            if (bucket.addIfActive(value) { buckets.load().findBucket(priority) === bucket }) {
                if (bucket.queue.isEmpty()) {
                    removeBucketIfEmpty(bucket)
                }
                return
            }

            removeBucketIfEmpty(bucket)
        }
    }

    override fun remove(priority: Int, target: T) {
        val bucket = buckets.load().findBucket(priority) ?: return
        try {
            bucket.queue.removeFirst(target)
        } finally {
            removeBucketIfEmpty(bucket)
        }
    }

    override fun removeIf(priority: Int, predicate: (T) -> Boolean) {
        val bucket = buckets.load().findBucket(priority) ?: return
        try {
            bucket.queue.removeAllIf(predicate)
        } finally {
            removeBucketIfEmpty(bucket)
        }
    }

    override fun remove(target: T) {
        for (bucket in buckets.load()) {
            val removed = try {
                bucket.queue.removeFirst(target)
            } finally {
                removeBucketIfEmpty(bucket)
            }
            if (removed) {
                return
            }
        }
    }

    override fun removeIf(predicate: (T) -> Boolean) {
        for (bucket in buckets.load()) {
            try {
                bucket.queue.removeAllIf(predicate)
            } finally {
                removeBucketIfEmpty(bucket)
            }
        }
    }

    override fun iterator(): Iterator<T> = PriorityIterator(buckets.load())

    override fun clear() {
        val oldBuckets = buckets.exchange(emptyList())
        oldBuckets.forEach { it.queue.clear() }
    }

    override fun toString(): String =
        buckets.load().associate { it.priority to it.queue.toList() }.toString()

    private fun bucketFor(priority: Int): PriorityBucket<T> {
        while (true) {
            val old = buckets.load()
            val index = old.findBucketIndex(priority)
            if (index >= 0) {
                val bucket = old[index]
                if (!bucket.isClosed()) {
                    return bucket
                }

                removeClosedBucket(bucket)
                continue
            }

            val bucket = PriorityBucket(priority, ConcurrentQueueImpl<T>())
            val newBuckets = old.insertAt(-index - 1, bucket)
            if (buckets.compareAndSet(expectedValue = old, newValue = newBuckets)) {
                return bucket
            }
        }
    }

    private fun removeBucketIfEmpty(bucket: PriorityBucket<T>) {
        if (bucket.closeIfEmpty()) {
            removeClosedBucket(bucket)
        }
    }

    private fun removeClosedBucket(bucket: PriorityBucket<T>) {
        while (true) {
            val old = buckets.load()
            if (!old.containsSame(bucket)) {
                return
            }

            val newBuckets = old.filterNot { it === bucket }
            if (buckets.compareAndSet(expectedValue = old, newValue = newBuckets)) {
                return
            }
        }
    }

    private class PriorityIterator<T>(
        private val buckets: List<PriorityBucket<T>>
    ) : Iterator<T> {
        private var bucketIndex = 0
        private var currentIterator: Iterator<T>? = null

        override fun hasNext(): Boolean {
            while (true) {
                val iterator = currentIterator
                if (iterator != null && iterator.hasNext()) {
                    return true
                }

                if (bucketIndex >= buckets.size) {
                    return false
                }

                currentIterator = buckets[bucketIndex++].queue.iterator()
            }
        }

        override fun next(): T {
            if (!hasNext()) {
                throw NoSuchElementException()
            }

            return currentIterator!!.next()
        }
    }
}

@OptIn(ExperimentalSimbotCollectionApi::class, ExperimentalAtomicApi::class)
private class PriorityBucket<T>(
    val priority: Int,
    val queue: ConcurrentQueueImpl<T>,
    private val state: AtomicInt = AtomicInt(BUCKET_ACTIVE)
) {
    fun isClosed(): Boolean = state.load() == BUCKET_CLOSED

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
            val current = state.load()
            when {
                current == BUCKET_CLOSED -> return true
                current > BUCKET_ACTIVE -> return false
                current == BUCKET_ACTIVE -> {
                    if (!queue.isEmpty()) {
                        return false
                    }
                    if (!state.compareAndSet(
                            expectedValue = BUCKET_ACTIVE,
                            newValue = BUCKET_CLOSING
                        )
                    ) {
                        continue
                    }
                }
            }

            if (queue.isEmpty()) {
                if (state.compareAndSet(expectedValue = BUCKET_CLOSING, newValue = BUCKET_CLOSED)) {
                    return true
                }
            } else if (state.compareAndSet(expectedValue = BUCKET_CLOSING, newValue = BUCKET_ACTIVE)) {
                return false
            }
        }
    }

    private fun enterAdd(): Boolean {
        while (true) {
            val current = state.load()
            if (current < BUCKET_ACTIVE) {
                return false
            }
            check(current < Int.MAX_VALUE)

            if (state.compareAndSet(expectedValue = current, newValue = current + 1)) {
                return true
            }
        }
    }

    private fun leaveAdd() {
        while (true) {
            val current = state.load()
            check(current > BUCKET_ACTIVE)
            if (state.compareAndSet(expectedValue = current, newValue = current - 1)) {
                return
            }
        }
    }
}

private const val BUCKET_CLOSED = -1
private const val BUCKET_CLOSING = -2
private const val BUCKET_ACTIVE = 0

private fun <T> List<T>.insertAt(index: Int, value: T): List<T> {
    return buildList(size + 1) {
        addAll(this@insertAt.subList(0, index))
        add(value)
        addAll(this@insertAt.subList(index, this@insertAt.size))
    }
}

private fun <T> List<PriorityBucket<T>>.containsSame(value: PriorityBucket<T>): Boolean {
    return any { it === value }
}

private fun <T> List<PriorityBucket<T>>.findBucket(priority: Int): PriorityBucket<T>? {
    val index = findBucketIndex(priority)
    return if (index >= 0) this[index] else null
}

private fun <T> List<PriorityBucket<T>>.findBucketIndex(priority: Int): Int {
    return binarySearch { it.priority.compareTo(priority) }
}

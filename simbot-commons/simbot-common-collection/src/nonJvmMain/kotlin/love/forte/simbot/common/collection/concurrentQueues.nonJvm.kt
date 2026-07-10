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
    private class Node<T>(
        val value: T?,
        removed: Boolean = false
    ) {
        val next: AtomicReference<Node<T>?> = AtomicReference(null)
        val removed: AtomicBoolean = AtomicBoolean(removed)
    }

    private class Step<T>(
        val node: Node<T>,
        val value: T?
    )

    private val head = Node<T>(value = null, removed = true)
    private val tail = AtomicReference(head)

    override val size: Int
        get() {
            var count = 0
            var node = head.next.load()
            while (node != null) {
                if (!node.removed.load()) {
                    count++
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
        var node = head.next.load()
        while (node != null) {
            if (!node.removed.load() && node.value == value) {
                if (node.removed.compareAndSet(expectedValue = false, newValue = true)) {
                    unlinkRemovedNodes()
                    return true
                }
            }
            node = node.next.load()
        }

        return false
    }

    override fun removeIf(predicate: (T) -> Boolean) {
        removeAllIf(predicate)
    }

    fun removeAllIf(predicate: (T) -> Boolean): Boolean {
        var changed = false
        var node = head.next.load()

        while (node != null) {
            val next = node.next.load()
            if (!node.removed.load()) {
                @Suppress("UNCHECKED_CAST")
                val value = node.value as T
                if (predicate(value) && node.removed.compareAndSet(expectedValue = false, newValue = true)) {
                    changed = true
                }
            }
            node = next
        }

        if (changed) {
            unlinkRemovedNodes()
        }

        return changed
    }

    override fun iterator(): Iterator<T> = QueueIterator(this, head.next.load())

    override fun clear() {
        var node = head.next.load()
        while (node != null) {
            node.removed.store(true)
            node = node.next.load()
        }
        unlinkRemovedNodes()
    }

    override fun toString(): String = buildList {
        var node = head.next.load()
        while (node != null) {
            if (!node.removed.load()) {
                @Suppress("UNCHECKED_CAST")
                add(node.value as T)
            }
            node = node.next.load()
        }
    }.toString()

    private fun firstLiveNode(): Node<T>? {
        var node = head.next.load()
        while (node != null) {
            if (!node.removed.load()) {
                return node
            }
            node = node.next.load()
        }

        return null
    }

    private fun nextLiveNode(from: Node<T>?): Step<T>? {
        var node = from
        while (node != null) {
            if (!node.removed.load()) {
                return Step(node, node.value)
            }
            node = node.next.load()
        }

        return null
    }

    private fun nextAfter(node: Node<T>): Node<T>? = node.next.load()

    private fun unlinkRemovedNodes() {
        var previous = head
        var node = previous.next.load()

        while (node != null) {
            val next = node.next.load()
            if (node.removed.load() && next != null) {
                previous.next.compareAndSet(expectedValue = node, newValue = next)
            } else {
                previous = node
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
        private var nextStep: Step<T>? = null

        override fun hasNext(): Boolean {
            val step = nextStep ?: queue.nextLiveNode(candidate)
            nextStep = step
            candidate = step?.node
            return step != null
        }

        override fun next(): T {
            val step = nextStep ?: queue.nextLiveNode(candidate) ?: throw NoSuchElementException()
            nextStep = null
            candidate = queue.nextAfter(step.node)

            @Suppress("UNCHECKED_CAST")
            return step.value as T
        }
    }
}

@OptIn(ExperimentalAtomicApi::class)
@ExperimentalSimbotCollectionApi
internal class PriorityConcurrentQueueImpl<T> : PriorityConcurrentQueue<T> {
    private val buckets = AtomicReference<List<PriorityBucket<T>>>(emptyList())

    override val size: Int
        get() = buckets.load().sumOf { it.queue.size }

    override fun isEmpty(priority: Int): Boolean =
        buckets.load().findBucket(priority)?.let { it.isClosed() || it.queue.isEmpty() } ?: true

    override fun isEmpty(): Boolean =
        buckets.load().all { it.isClosed() || it.queue.isEmpty() }

    override fun add(priority: Int, value: T) {
        while (true) {
            val bucket = bucketFor(priority)
            if (bucket.addIfActive(value) && buckets.load().containsSame(bucket) && !bucket.isClosed()) {
                return
            }
        }
    }

    override fun remove(priority: Int, target: T) {
        val bucket = buckets.load().findBucket(priority) ?: return
        if (bucket.queue.removeFirst(target)) {
            removeBucketIfEmpty(bucket)
        }
    }

    override fun removeIf(priority: Int, predicate: (T) -> Boolean) {
        val bucket = buckets.load().findBucket(priority) ?: return
        if (bucket.queue.removeAllIf(predicate)) {
            removeBucketIfEmpty(bucket)
        }
    }

    override fun remove(target: T) {
        for (bucket in buckets.load()) {
            if (bucket.queue.removeFirst(target)) {
                removeBucketIfEmpty(bucket)
                return
            }
        }
    }

    override fun removeIf(predicate: (T) -> Boolean) {
        for (bucket in buckets.load()) {
            if (bucket.queue.removeAllIf(predicate)) {
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
        buckets: List<PriorityBucket<T>>
    ) : Iterator<T> {
        private val buckets = buckets.toList()
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

    fun addIfActive(value: T): Boolean {
        if (!enterAdd()) {
            return false
        }

        try {
            queue.add(value)
            return true
        } finally {
            leaveAdd()
        }
    }

    fun closeIfEmpty(): Boolean {
        var closed: Boolean? = null
        while (closed == null) {
            if (!queue.isEmpty()) {
                closed = false
            } else {
                val current = state.load()
                closed = when {
                    current == BUCKET_CLOSED -> true
                    current != BUCKET_ACTIVE -> false
                    state.compareAndSet(expectedValue = BUCKET_ACTIVE, newValue = BUCKET_CLOSED) -> true
                    else -> null
                }
            }
        }

        return closed
    }

    private fun enterAdd(): Boolean {
        while (true) {
            val current = state.load()
            if (current == BUCKET_CLOSED) {
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
private const val BUCKET_ACTIVE = 0

private fun <T> List<T>.insertAt(index: Int, value: T): List<T> {
    return buildList(size + 1) {
        for (i in 0 until index) {
            add(this@insertAt[i])
        }
        add(value)
        for (i in index until this@insertAt.size) {
            add(this@insertAt[i])
        }
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
    var low = 0
    var high = lastIndex
    while (low <= high) {
        val mid = low + (high - low) / 2
        val midPriority = this[mid].priority
        when {
            midPriority < priority -> low = mid + 1
            midPriority > priority -> high = mid - 1
            else -> return mid
        }
    }

    return -(low + 1)
}

@OptIn(ExperimentalSimbotCollectionApi::class)
private fun <T> ConcurrentQueueImpl<T>.toList(): List<T> {
    return buildList {
        for (value in this@toList) {
            add(value)
        }
    }
}

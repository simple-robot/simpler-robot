@file:JvmSynthetic
package love.forte.simbot.utils

import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.ConcurrentSkipListSet

/**
 * Create a concurrent collection.
 */
public actual fun <T> concurrentCollection(): MutableCollection<T> {
    return ConcurrentLinkedQueue()
}

/**
 * Create a concurrent set.
 */
public actual fun <T> concurrentSet(): MutableSet<T> {
    return ConcurrentSkipListSet()
}
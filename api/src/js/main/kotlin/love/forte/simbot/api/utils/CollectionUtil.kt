package love.forte.simbot.api.utils

import kotlinx.atomicfu.locks.Lock
import kotlinx.atomicfu.locks.reentrantLock

/**
 * Create a concurrent set.
 */
public actual fun <T> concurrentSet(): MutableSet<T> {
    return mutableSetOf()
}

/**
 * Create a concurrent collection.
 */
public actual fun <T> concurrentCollection(): MutableCollection<T> {
    return mutableListOf()
}
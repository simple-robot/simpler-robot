@file:JvmSynthetic
package love.forte.simbot.api.utils

import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Create a concurrent collection.
 */
public actual fun <T> concurrentCollection(): MutableCollection<T> {
    return ConcurrentLinkedQueue()
}
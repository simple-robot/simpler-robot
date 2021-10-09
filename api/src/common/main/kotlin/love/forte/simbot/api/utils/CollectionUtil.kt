@file:kotlin.jvm.JvmSynthetic

package love.forte.simbot.api.utils

/**
 * Create a concurrent set.
 */
public expect fun <T> concurrentSet(): MutableSet<T>

/**
 * Create a concurrent collection.
 */
public expect fun <T> concurrentCollection(): MutableCollection<T>

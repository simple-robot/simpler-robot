@file:kotlin.jvm.JvmSynthetic

package love.forte.simbot.api.utils


/**
 * Create a concurrent collection.
 */
public expect fun <T> concurrentCollection(): MutableCollection<T>

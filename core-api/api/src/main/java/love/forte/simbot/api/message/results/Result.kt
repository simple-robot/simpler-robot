/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */
@file:JvmMultifileClass
@file:JvmName("Results")

package love.forte.simbot.api.message.results

import love.forte.simbot.api.message.containers.OriginalDataContainer
import love.forte.simbot.api.sender.Getter

/**
 *
 * **返回值** 。
 *
 * 一般可以代表在 [获取器][Getter] 中所得到的信息的值。
 *
 * @author ForteScarlet <ForteScarlet@163.com>
 * @date 2020/9/4
 * @since
 */
public interface Result : OriginalDataContainer {

    /**
     * 空的伴生对象。
     */
    companion object Empty : Result {
        override val originalData: String
            get() = "EmptyResult()"
    }
}


/**
 * 存在多个内容的**返回值** 。
 *
 * 一般可以代表在 [获取器][Getter] 中所得到的信息的值。
 *
 */
public interface MultipleResults<T : Result> : Result, Iterable<T> {

    /** 得到结果集合。可能会是空的，但不应为null。 */
    val results: List<T>

    /**
     * 习惯用法, 得到 [results] 的长度。
     */
    fun size(): Int = results.size

    /**
     * 获取一个迭代器
     */
    override operator fun iterator(): Iterator<T> = results.iterator()

    /**
     * 结果集是否为空
     */
    fun isEmpty(): Boolean = results.isEmpty()


    /**
     * 将 [results] 转化为 [java.util.stream.Stream]
     */
    fun stream(): java.util.stream.Stream<T> = results.stream()


    /**
     * 空的伴生对象。
     */
    companion object Empty : MultipleResults<Nothing> {
        override val originalData: String
            get() = "EmptyMultipleResults()"
        override val results: List<Nothing>
            get() = emptyList()
    }

}


/**
 * [MultipleResults].[size][MultipleResults.size].
 * @since 2.0.0
 */
public inline val <T : Result> MultipleResults<T>.size: Int get() = results.size


/**
 * 复数返回值类型之一。区别在于此为一个节点，每个节点都能继续向下获取剩余元素。
 *
 */
public interface NodeResult<T> : MultipleResults<NodeResult<T>> {
    /**
     * 当前元素。
     */
    val value: T

    /**
     * 此节点元素下的其他元素。
     */
    override val results: List<NodeResult<T>>

}


/**
 * 得到一个没有子节点的单节点result。
 */
public fun <T> singletonNodeResult(value: T): NodeResult<T> = SingletonNodeResult(value)


private data class SingletonNodeResult<T>(override val value: T) : NodeResult<T> {
    override val originalData: String
        get() = "SingletonNodeResult($value)"

    override fun toString(): String = originalData

    override val results: List<NodeResult<T>>
        get() = emptyList()
}


/**
 * 类似于 [love.forte.common.utils.Carrier] 的 Result实例，内部存在一个 [value] 值。
 *
 * 但是此类不提供carrier中那些 orElse 之类的方法。
 *
 */
public data class CarrierResult<T : Any?> internal constructor(val value: T) : Result {
    override val originalData: String = "Result($value)"
    override fun toString(): String = originalData

    companion object {
        private val TrueResult = CarrierResult(true)
        private val FalseResult = CarrierResult(false)
        private val NullResult = CarrierResult(null)

        @JvmStatic
        @Suppress("UNCHECKED_CAST")
        fun <T : Any?> valueOf(value: T): CarrierResult<T> {
            return when (value) {
                null -> NullResult as CarrierResult<T>
                true -> TrueResult as CarrierResult<T>
                false -> FalseResult as CarrierResult<T>
                else -> CarrierResult(value)
            }
        }


    }
}


public fun <T: Any?> T.toCarrierResult(): CarrierResult<T> = CarrierResult.valueOf(this)
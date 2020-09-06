/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     Receipt.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *  *
 *  *
 *
 */

package love.forte.simbot.common.api.carrier

import java.util.*

/**
 * 一个类似于 java 的 [java.util.Optional] 类,
 * 提供一些简单的api来对一个可能为null的值进行操作。
 *
 * 其代表了一个 `回执`, 而这个回执不确定其是否为 `null`。
 *
 */
@Suppress("UNCHECKED_CAST")
public data class Carrier<T>(private val value: T? = null) {
    /** 转化为java8的 [java.util.Optional] */
    public fun toOptional(): Optional<T> = Optional.ofNullable(value)

    /**
     * 得到 [value] 值
     * @return T?
     */
    fun getOrNull(): T? = value

    /**
     * 得到 [value] 值，或者抛出一个异常。默认为抛出 [NullPointerException]
     */
    @JvmOverloads
    fun getOrThrow(err: Throwable = NullPointerException("value is null.")): T = value ?: throw err


    /**
     * 得到 [value] 值，或者通过函数获取并抛出一个异常。默认为抛出 [NullPointerException]
     * @param err in java: ` getOrThrow(() -> new RuntimeException()) `
     */
    fun getOrThrow(err: () -> Throwable): T = value ?: throw err()


    /**
     * 得到 [value] 值，如果为null则获取一个默认值 [or]
     * @param or T
     * @return T
     */
    fun getOrElse(or: T): T = value ?: or


    /**
     * 得到 [value] 值，如果为null就通过 [or] 计算并获取一个默认值
     * @param or Function0<T>
     * @return T
     */
    fun getOrElse(or: () -> T): T = value ?: or()


    /**
     * 如果值不为null，则进行转化
     */
    fun <R> map(mapper: (T) -> R): Carrier<R> = value?.let { Carrier(mapper(it)) } ?: EMPTY as Carrier<R>


    /**
     * 如果值符合期望，包留，否则变为null
     * @param filter Function1<T, Boolean>
     * @return Carrier<T>
     */
    fun ifOr(filter: (T) -> Boolean): Carrier<T> = value?.let {
        if (filter(it)) this else EMPTY as Carrier<T>
    } ?: EMPTY as Carrier<T>


    companion object {
        private val EMPTY = Carrier<Any?>(null)

        @JvmStatic
        fun <T> empty(): Carrier<T> = EMPTY as Carrier<T>

        @JvmStatic
        fun <T> get(value: T?): Carrier<T> = value?.let { Carrier(value) } ?: EMPTY as Carrier<T>
    }
}

/**
 * 将一个任意的值转化为 [Carrier]
 */
@Suppress("RedundantVisibilityModifier")
public fun <T> T?.toCarrier(): Carrier<T> = Carrier.get(this)



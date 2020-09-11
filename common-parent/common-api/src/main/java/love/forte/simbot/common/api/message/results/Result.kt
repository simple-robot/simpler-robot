/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     Result.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.common.api.message.results

import love.forte.simbot.common.api.message.containers.OriginalDataContainer
import love.forte.simbot.common.api.sender.Getter

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
public interface Result: OriginalDataContainer




/**
 * 存在多个内容的**返回值** 。
 *
 * 一般可以代表在 [获取器][Getter] 中所得到的信息的值。
 *
 */
public interface MultipleResults<T: Result>: Result, Iterable<T> {

    /** 得到结果集合。可能会是空的，但不应为null。 */
    val results: List<T>

    /**
     * 得到 [results] 的长度
     */
    @JvmDefault
    val size: Int get() = results.size

    /**
     * 获取一个迭代器
     */
    @JvmDefault
    override fun iterator(): Iterator<T> = results.iterator()

    /**
     * 结果集是否为空
     */
    @JvmDefault
    fun isEmpty(): Boolean = results.isEmpty()


    /**
     * 将 [results] 转化为 [java.util.stream.Stream]
     */
    @JvmDefault
    fun stream(): java.util.stream.Stream<T> = results.stream()

}
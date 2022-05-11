/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 */

package love.forte.simbot.utils.sequence



/**
 * 常见的_匹配_函数接口，提供一个参数，判断其结果。
 *
 * `(V) -> Boolean`。
 *
 */
public fun interface Matcher<in V> : (V) -> Boolean {
    /**
     * 根据 [value] 判断是否匹配。
     */
    override operator fun invoke(value: V): Boolean
}


/**
 * 常见的_转化_函数接口，提供一个参数，转化其值。
 *
 * `(V) -> T`。
 *
 */
public fun interface Mapper<in V, out T> : (V) -> T {
    /**
     * 将 [V] 类型的 [value] 转化为目标类型 [T]。
     */
    override operator fun invoke(value: V): T
}

/**
 * 常见的_访问_接口，用于序列对结果进行逐一访问的情况，类似于使用在 `forEach` 、`collect` 等函数中的情况。
 */
public fun interface Visitor<in V> {
    /**
     * 处理逐条元素。
     */
    public operator fun invoke(value: V)
}
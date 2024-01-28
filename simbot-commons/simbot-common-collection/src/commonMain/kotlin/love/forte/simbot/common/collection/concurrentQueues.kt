/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
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

/**
 * 可以并发安全地操作元素地并发队列类型。
 *
 * 注意：非 JVM 平台的实现仍处于试验阶段。
 *
 * @author ForteScarlet
 */
@ExperimentalSimbotCollectionApi
public interface ConcurrentQueue<T> : Iterable<T> {
    /**
     * 获取元素数量
     */
    public val size: Int

    /**
     * 当前队列是否为空
     */
    public fun isEmpty(): Boolean

    /**
     * 添加指定元素
     */
    public fun add(value: T)

    /**
     * 移除指定元素
     */
    public fun remove(value: T)

    // pop? take?

    /**
     * 根据条件从列表中删除元素。
     *
     * @param predicate 用于确定是否应删除元素的条件。只有满足条件的元素才会被删除。
     */
    public fun removeIf(predicate: (T) -> Boolean)

    /**
     * 返回用于遍历此对象元素的迭代器。
     * 此迭代器应当可以安全的在遍历途中对队列本体内元素进行修改，
     * 但是不确保此迭代器可以看见这些变更（例如迭代器实际上为一个副本）。
     *
     * @return 允许遍历此对象元素的迭代器对象。
     */
    override fun iterator(): Iterator<T>

    /**
     * 清除其中的所有元素。
     */
    public fun clear()
}

/**
 * 表示一个基于优先级的并发队列，可以根据元素的优先级添加和删除元素。
 *
 * 注意：非 JVM 平台的实现仍处于试验阶段。
 *
 * @author ForteScarlet
 */
@ExperimentalSimbotCollectionApi
public interface PriorityConcurrentQueue<T> : Iterable<T> {
    /**
     * 获取元素数量
     */
    public val size: Int

    /**
     * 指定优先级下的队列是否为空
     */
    public fun isEmpty(priority: Int): Boolean

    /**
     * 整个队列是否为空
     */
    public fun isEmpty(): Boolean

    /**
     * 将具有指定优先级的元素添加到集合中。
     *
     * @param priority 元素的优先级
     * @param value 要添加的元素的值
     */
    public fun add(priority: Int, value: T)

    /**
     * 根据给定的优先级和目标对象，从列表中删除指定的项。
     *
     * @param priority 要删除的项目的优先级。
     * @param target 从列表中删除的目标对象。
     */
    public fun remove(priority: Int, target: T)

    /**
     * 根据优先级和条件从列表中删除元素。
     *
     * @param priority 要删除的元素的优先级。只有优先级高于或等于给定优先级的元素才会被删除。
     * @param predicate 用于确定是否应删除元素的条件。只有满足条件的元素才会被删除。
     */
    public fun removeIf(priority: Int, predicate: (T) -> Boolean)

    /**
     * 根据给定的目标对象，从列表中删除指定的项。
     *
     * @param target 从列表中删除的目标对象。
     */
    public fun remove(target: T)

    /**
     * 根据条件从列表中删除元素。
     *
     * @param predicate 用于确定是否应删除元素的条件。只有满足条件的元素才会被删除。
     */
    public fun removeIf(predicate: (T) -> Boolean)

    /**
     * 返回用于遍历此对象元素的迭代器。
     * 此迭代器应当可以安全的在遍历途中对队列本体内元素进行修改，
     * 但是不确保此迭代器可以看见这些变更（例如迭代器实际上为一个副本）。
     *
     * @return 允许遍历此对象元素的迭代器对象。
     */
    override fun iterator(): Iterator<T>

    /**
     * 清除其中的所有元素。
     */
    public fun clear()
}

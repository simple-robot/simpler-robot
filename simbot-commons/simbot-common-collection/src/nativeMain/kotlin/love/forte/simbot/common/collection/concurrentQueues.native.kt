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

import kotlin.concurrent.AtomicReference

@ExperimentalCollectionApi("native")
internal class ConcurrentQueueImpl<T> : ConcurrentQueue<T> {
    private val listRef: AtomicReference<List<T>> = AtomicReference(emptyList())

    override fun add(value: T) {
        listRef.update { old ->
            when (old.size) {
                0 -> listOf(value)
                else -> old + value
            }
        }
    }

    override fun remove(value: T) {
        listRef.update { old ->
            when (old.size) {
                // nothing to remove
                0 -> return
                // only one element
                1 -> if (old.first() == value) emptyList() else old
                else -> old - value
            }
        }
    }

    override fun removeIf(predicate: (T) -> Boolean) {
        listRef.update { old ->
            old.filterNot(predicate)
        }
    }

    override fun iterator(): Iterator<T> = listRef.value.iterator()

    override fun toString(): String = listRef.value.toString()
}

internal class PriorityConcurrentQueueImpl<T> : PriorityConcurrentQueue<T> {
    private data class ListWithPriority<T>(
        val priority: Int,
        val list: AtomicReference<List<T>>
    )

    private val lists = AtomicReference<List<ListWithPriority<T>>>(emptyList())

    private fun findByPriority(priority: Int): ListWithPriority<T>? =
        lists.value.find { it.priority == priority }

    override fun add(priority: Int, value: T) {
        do {
            val found = findByPriority(priority)

            val compared = if (found != null) {
                val foundList = found.list
                val expected = foundList.value
                val newValue = expected + value
                foundList.compareAndSet(expected, newValue)
            } else {
                val listValue = lists.value
                val addedNewValue = lists.compareAndSet(listValue, buildList {
                    addAll(listValue)
                    add(ListWithPriority(priority = priority, list = AtomicReference(listOf(value))))
                    sortBy { it.priority }
                })

                addedNewValue
            }
        } while (!compared)


    }


    override fun remove(priority: Int, target: T) {
        val found = findByPriority(priority)

        if (found != null) {
            do {
                val list = found.list.value
                val newList = list - target
            } while (!updateListForRemoveElement(found, list, newList).value)
        }
    }

    override fun removeIf(priority: Int, predicate: (T) -> Boolean) {
        val found = findByPriority(priority)

        if (found != null) {
            do {
                val list = found.list.value
                val newList = list.filterNot(predicate)
            } while (!updateListForRemoveElement(found, list, newList).value)
        }
    }

    override fun remove(target: T) {
        head@ while (true) {
            for (listWithPriority in lists.value) {
                while (true) {
                    val list = listWithPriority.list.value
                    val newList = list - target

                    when (val result = updateListForRemoveElement(listWithPriority, list, newList)) {
                        is ElementRemoveResult.RemoveTargetList -> {
                            // 如果企图直接更新 list 且更新成功（移除了当前的 listWithPriority），
                            // 则说明当前 lists 已经发生了改变，重新遍历
                            if (result.value) {
                                continue@head
                            }

                            // 想要删除 listWithPriority，但是删除失败，重新尝试
                            // Just do nothing.
                        }

                        // 没有 target 元素
                        is ElementRemoveResult.SameSize -> break

                        // 删除列表元素
                        is ElementRemoveResult.RemoveElement -> {
                            // 删除成功
                            if (result.value) {
                                return
                            }
                        }
                    }
                }
            }
        }
    }

    override fun removeIf(predicate: (T) -> Boolean) {
        head@ while (true) {
            for (listWithPriority in lists.value) {
                while (true) {
                    val list = listWithPriority.list.value
                    val newList = list.filterNot(predicate)

                    when (val result = updateListForRemoveElement(listWithPriority, list, newList)) {
                        is ElementRemoveResult.RemoveTargetList -> {
                            // 如果企图直接更新 list 且更新成功（移除了当前的 listWithPriority），
                            // 则说明当前 lists 已经发生了改变，重新遍历
                            if (result.value) {
                                continue@head
                            }

                            // 想要删除 listWithPriority，但是删除失败，重新尝试
                            // Just do nothing.
                        }

                        // 没有 target 元素
                        is ElementRemoveResult.SameSize -> break

                        // 删除列表元素
                        is ElementRemoveResult.RemoveElement -> {
                            // 删除成功，跳出当前 listWithPriority，进入下一个 listWithPriority
                            if (result.value) {
                                break
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * @return Is done
     */
    private fun updateListForRemoveElement(
        target: ListWithPriority<T>,
        expectList: List<T>,
        newList: List<T>
    ): ElementRemoveResult {
        return when {
            expectList.isEmpty() || newList.isEmpty() -> {
                // try to remove found
                val listsValue = lists.value
                val result = lists.compareAndSet(listsValue, listsValue - target)
                return ElementRemoveResult.RemoveTargetList.of(result)
            }

            expectList.size == newList.size -> {
                // nothing updated.
                ElementRemoveResult.SameSize
            }

            else -> {
                // remove element
                val result = target.list.compareAndSet(expectList, newList)
                ElementRemoveResult.RemoveElement.of(result)
            }
        }
    }

    private sealed class ElementRemoveResult {
        abstract val value: Boolean

        /**
         * 由于 expectList 或 newList 为空，所以尝试从 lists 中直接移除 target 时的响应
         */
        class RemoveTargetList private constructor(override val value: Boolean) : ElementRemoveResult() {
            companion object {
                val True = RemoveTargetList(true)
                val False = RemoveTargetList(false)
                fun of(value: Boolean) = if (value) True else False
            }
        }

        /**
         * 当 expectList 与 newList 内容长度相同时返回的恒为 true 的结果
         */
        data object SameSize : ElementRemoveResult() {
            override val value: Boolean
                get() = true
        }


        class RemoveElement private constructor(override val value: Boolean) : ElementRemoveResult() {
            companion object {
                val True = RemoveElement(true)
                val False = RemoveElement(false)
                fun of(value: Boolean) = if (value) True else False
            }
        }
    }


    override fun iterator(): Iterator<T> {
        return lists.value.asSequence().flatMap { it.list.value }.iterator()
    }
}


private inline fun <T> AtomicReference<T>.update(block: (T) -> T): T {
    while (true) {
        val old = value
        val newValue = block(old)
        if (compareAndSet(old, newValue)) {
            return old
        }
    }
}

/*
 *     Copyright (c) 2026. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
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

package love.forte.simbot.ability

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmInline

/**
 * [DeleteOption] 的标准选项实现。
 */
public enum class StandardDeleteOption : DeleteOption {
    /**
     * 如果是因为缺失目标而导致的删除失败，不抛出 [NoSuchElementException]，而是直接忽略。
     */
    IGNORE_ON_NO_SUCH_TARGET,

    /**
     * 忽略删除过程中产生的异常。
     * 不包括 [IGNORE_ON_NO_SUCH_TARGET] 和 [IGNORE_ON_UNSUPPORTED]
     * 中描述的 [NoSuchElementException] 和 [UnsupportedOperationException]，
     * 主要针对 [DeleteFailureException] 或参数校验、API请求过程中的异常。
     */
    IGNORE_ON_FAILURE,

    /**
     * 使用 [IGNORE_ON_FAILURE]。
     */
    @Deprecated("This will be removed in a future version", level = DeprecationLevel.ERROR)
    IGNORE_ON_ANY_FAILURE,

    /**
     * 忽略由于不支持而产生的 [UnsupportedOperationException] 异常。
     */
    IGNORE_ON_UNSUPPORTED;

    public companion object {

        /**
         * 分析 `options` 并得到一个基于 [StandardAnalysis] 的分析结果。
         *
         * @param onEach 在分析每个元素时对它们进行额外的操作。
         */
        public inline fun Array<out DeleteOption>.standardAnalysis(
            onEach: (DeleteOption) -> Unit = {}
        ): StandardAnalysis {
            var value = 0
            forEach { option ->
                if (option is StandardDeleteOption) {
                    value = value or (1 shl option.ordinal)
                }
                onEach(option)
            }

            return StandardAnalysis(value)
        }

        /**
         * 分析 `options` 并得到一个基于 [StandardAnalysis] 的分析结果，
         * 并在此分析结果中执行 [block]
         *
         * @param onEach 在分析每个元素时对它们进行额外的操作。
         */
        @OptIn(ExperimentalContracts::class)
        public inline fun Array<out DeleteOption>.inStandardAnalysis(
            onEach: (DeleteOption) -> Unit = {},
            block: StandardAnalysis.() -> Unit
        ): StandardAnalysis {
            contract {
                callsInPlace(block, InvocationKind.EXACTLY_ONCE)
            }

            return standardAnalysis(onEach).apply {
                block()
            }
        }
    }

    /**
     * 基于 [StandardAnalysis] 的分析结果，以非遍历的方式检测存在的 [StandardDeleteOption] 选项。
     */
    @JvmInline
    public value class StandardAnalysis @PublishedApi internal constructor(private val value: Int) {
        /**
         * 判断是否存在某个标准选项。
         */
        public operator fun contains(option: StandardDeleteOption): Boolean = value and (1 shl option.ordinal) != 0

        /**
         * 判断是否包含任意的 [StandardDeleteOption]
         */
        public val isEmpty: Boolean
            get() = value == 0

        /**
         * 判断是否包含所有的 [StandardDeleteOption]
         */
        @Suppress("UnnecessaryParentheses")
        public val isFull: Boolean
            get() = value == 0xffff shr (16 - entries.size)
    }
}

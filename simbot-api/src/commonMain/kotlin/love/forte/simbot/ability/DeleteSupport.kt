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

@file:JvmName("DeleteSupports")
@file:JvmMultifileClass

package love.forte.simbot.ability

import love.forte.simbot.suspendrunner.ST
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName


/**
 * 对“删除行为”的支持。
 *
 * @author ForteScarlet
 */
@ST
public interface DeleteSupport {

    /**
     * 进行删除行为。
     *
     * 在实现者本质上不支持删除行为时，抛出 [UnsupportedOperationException]。
     *
     * [delete] 行为如果遇到诸如权限不足、已被删除等API支持、但由于逻辑、业务原因而导致删除失败的情况下
     * （也就是说由API或可预知的原因所导致的异常），都建议抛出 [DeleteFailureException] 类型异常。
     *
     * “删除行为”的具体含义由实现者定义。例如在 `MessageContent` 中，代表删除、撤回、撤销这个消息，
     * 而在某个 `User` 类型下它可能代表踢出、移除这个用户。
     *
     * @param options 删除时的可选选项。不支持的选项将会被忽略。更多说明参考 [DeleteOption]。
     *
     * @throws UnsupportedOperationException 如果 [DeleteSupport] 被默认实现但实现者不支持此API时。
     * @throws DeleteFailureException 删除行为失败异常。
     * @throws NoSuchElementException 如果因目标缺失而删除失败
     * @throws Exception 可能产生任何异常。
     */
    public suspend fun delete(vararg options: DeleteOption)
}

/**
 * [DeleteSupport.delete] 的可选选项。
 *
 * [DeleteOption] 可以自由扩展，且如果遇到不支持的实现则会将其忽略。
 * 但是所有 [DeleteSupport.delete] 都应当尽可能支持 [StandardDeleteOption]
 * 中提供的标准选项，并在不支持某些标准选项的时候提供相关的说明。
 *
 * @see StandardDeleteOption
 */
public interface DeleteOption

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
        public inline fun Array<out DeleteOption>.standardAnalysis(onEach: (DeleteOption) -> Unit = {}): StandardAnalysis {
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
        public val isFull: Boolean
            get() = value == (0xffff shr (16 - StandardDeleteOption.entries.size))
    }
}

/**
 * @see DeleteSupport.delete
 */
public open class DeleteFailureException : IllegalStateException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}


/**
 * 判断 [StandardDeleteOption.StandardAnalysis] 中是否包含 [StandardDeleteOption.IGNORE_ON_NO_SUCH_TARGET]
 */
public inline val StandardDeleteOption.StandardAnalysis.isIgnoreOnNoSuchTarget: Boolean
    get() = StandardDeleteOption.IGNORE_ON_NO_SUCH_TARGET in this

/**
 * 判断 [StandardDeleteOption.StandardAnalysis] 中是否包含 [StandardDeleteOption.IGNORE_ON_FAILURE]
 */
public inline val StandardDeleteOption.StandardAnalysis.isIgnoreOnFailure: Boolean
    get() = StandardDeleteOption.IGNORE_ON_FAILURE in this

/**
 * 判断 [StandardDeleteOption.StandardAnalysis] 中是否包含 [StandardDeleteOption.IGNORE_ON_ANY_FAILURE]
 */
@Deprecated("This will be removed in a future version", level = DeprecationLevel.ERROR)
@Suppress("DEPRECATION_ERROR", "DeprecatedCallableAddReplaceWith")
public inline val StandardDeleteOption.StandardAnalysis.isIgnoreOnAnyFailure: Boolean
    get() = StandardDeleteOption.IGNORE_ON_ANY_FAILURE in this

/**
 * 判断 [StandardDeleteOption.StandardAnalysis] 中是否包含 [StandardDeleteOption.IGNORE_ON_UNSUPPORTED]
 */
public inline val StandardDeleteOption.StandardAnalysis.isIgnoreOnUnsupported: Boolean
    get() = StandardDeleteOption.IGNORE_ON_UNSUPPORTED in this

/**
 * 如果 [StandardDeleteOption.StandardAnalysis] 中是包含 [StandardDeleteOption.IGNORE_ON_NO_SUCH_TARGET] 则执行 [block]
 */
@OptIn(ExperimentalContracts::class)
public inline fun StandardDeleteOption.StandardAnalysis.ifIgnoreOnNoSuchTarget(block: () -> Unit): StandardDeleteOption.StandardAnalysis {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    if (isIgnoreOnNoSuchTarget) {
        block()
    }
    return this
}

/**
 * 如果 [StandardDeleteOption.StandardAnalysis] 中是包含 [StandardDeleteOption.IGNORE_ON_FAILURE] 则执行 [block]
 */
@OptIn(ExperimentalContracts::class)
public inline fun StandardDeleteOption.StandardAnalysis.ifIgnoreOnFailure(block: () -> Unit): StandardDeleteOption.StandardAnalysis {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    if (isIgnoreOnFailure) {
        block()
    }
    return this
}

/**
 * 如果 [StandardDeleteOption.StandardAnalysis] 中是包含 [StandardDeleteOption.IGNORE_ON_ANY_FAILURE] 则执行 [block]
 */
@OptIn(ExperimentalContracts::class)
@Suppress("DEPRECATION_ERROR")
@Deprecated("This will be removed in a future version", level = DeprecationLevel.ERROR)
public inline fun StandardDeleteOption.StandardAnalysis.ifIgnoreOnAnyFailure(block: () -> Unit): StandardDeleteOption.StandardAnalysis {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    if (isIgnoreOnAnyFailure) {
        block()
    }
    return this
}

/**
 * 如果 [StandardDeleteOption.StandardAnalysis] 中是包含 [StandardDeleteOption.IGNORE_ON_UNSUPPORTED] 则执行 [block]
 */
@OptIn(ExperimentalContracts::class)
public inline fun StandardDeleteOption.StandardAnalysis.ifIgnoreOnUnsupported(block: () -> Unit): StandardDeleteOption.StandardAnalysis {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    if (isIgnoreOnUnsupported) {
        block()
    }
    return this
}

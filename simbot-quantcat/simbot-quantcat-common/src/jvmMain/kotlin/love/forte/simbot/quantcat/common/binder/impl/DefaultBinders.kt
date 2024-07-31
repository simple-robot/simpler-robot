/*
 *     Copyright (c) 2024. ForteScarlet.
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

package love.forte.simbot.quantcat.common.binder.impl

import love.forte.simbot.event.EventListenerContext
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.logger.logger
import love.forte.simbot.quantcat.common.binder.BindException
import love.forte.simbot.quantcat.common.binder.ParameterBinder
import kotlin.reflect.KParameter


/**
 * 默认的空内容绑定器。
 *
 * - 当参数可选时，使用 [ParameterBinder.Ignore] 标记其直接使用默认值。
 * - 当参数标记为 nullable 时，直接提供 `null`。
 * - 否则，[arg] 将会返回一个 [BindException] 的 [Result.failure] 表示失败。
 */
public class EmptyBinder(
    private val parameter: KParameter,
) : ParameterBinder {
    private val resultProvider: () -> Result<Any?> = when {
        parameter.isOptional -> {
            val ignoreResult: Result<Any?> = Result.success(ParameterBinder.Ignore)
            ({ ignoreResult })
        }

        parameter.type.isMarkedNullable -> {
            val nullResult: Result<Any?> = Result.success(null)
            ({ nullResult })
        }

        else -> {
            { Result.failure(BindException("Parameter(#${parameter.index}) [$parameter] has no binder.")) }
        }
    }

    override fun arg(context: EventListenerContext): Result<Any?> {
        return resultProvider()
    }

    override fun toString(): String = "EmptyBinder(parameter=$parameter)"
}


/**
 * 组合多个binder的 [ParameterBinder].
 *
 * 在聚合绑定器中，会依次对所有的 [binders] 和 [spare] 使用 [ParameterBinder.arg] 来评估本次应绑定的参数，
 * 知道遇到第一个返回为 [Result.isSuccess] 的结果后终止评估并使用此结果。
 *
 * 评估过程的详细描述参考 [arg] 文档说明。
 */
public class MergedBinder(
    private val binders: List<ParameterBinder>, // not be empty
    private val spare: List<ParameterBinder>, // empty able
    private val parameter: KParameter,
) : ParameterBinder {
    init {
        require(binders.isNotEmpty()) { "'binders' must not be empty" }
    }

    private companion object {
        val logger = LoggerFactory.logger<MergedBinder>()
    }

    init {
        require(binders.isNotEmpty()) { "'binders' must not be empty" }
    }

    /**
     *
     * 使用内部所有的聚合 binder 对 [context] 进行评估并选出一个最先出现的可用值。
     *
     * 评估过程中：
     *
     * - 如果参数不可为 `null`、评估结果为成功但是内容为 `null`、同时参数是可选的，
     * 则会忽略此结果，视为无结果。
     * - 如果评估结果为失败，则暂记此异常，并视为无结果。
     *
     * 期间，遇到任何成功的、不符合上述会造成“无结果”条件的，
     * 直接返回此评估结果，不再继续评估。
     *
     * 当所有binder评估完成，但没有遇到任何结果：
     *
     * - 如果参数为可选，输出debug日志并使用 [ParameterBinder.Ignore] 标记直接使用默认值。
     * - 否则，返回 [Result.failure] 错误结果，并追加之前暂记的所有异常堆栈。
     */
    @Suppress("ReturnCount")
    override fun arg(context: EventListenerContext): Result<Any?> {
        var err: Throwable? = null
        val isOptional = parameter.isOptional

        fun ParameterBinder.invoke(): Result<Any?>? {
            val result = arg(context)
            if (result.isSuccess) {
                // if success, return.
                // 如果参数不可为 null、结果成功但是为 null、同时参数是可选的，
                // 则返回 `null` 以忽略此参数。
                return if (
                    result.getOrNull() == null &&
                    !parameter.type.isMarkedNullable &&
                    parameter.isOptional
                ) {
                    null
                } else {
                    result
                }
            }
            // failure
            val resultErr = result.exceptionOrNull()!!
            with(err) {
                if (this == null) {
                    err = resultErr
                } else {
                    addSuppressed(resultErr)
                }
            }
            return null
        }

        return kotlin.runCatching {
            for (binder in binders) {
                val result = binder.invoke()
                if (result != null) return result
            }
            for (binder in spare) {
                val result = binder.invoke()
                if (result != null) return result
            }
            if (isOptional) {
                if (logger.isTraceEnabled) {
                    logger.debug("Nothing binder success for listener {}", context.listener)
                    logger.trace("Nothing binder success for listener {})", context.listener, err)
                } else {
                    logger.debug(
                        "Nothing binder success for listener {}. Enable trace level logging to view detailed reasons.",
                        context.listener
                    )
                }
                return Result.success(ParameterBinder.Ignore)
            }

            Result.failure<Any?>(BindException("Nothing binder success for listener ${context.listener}", err))
        }.getOrElse { binderInvokeException ->
            err?.also {
                binderInvokeException.addSuppressed(it)
            }
            Result.failure(BindException("Binder invoke failure", binderInvokeException))
        }
    }
}

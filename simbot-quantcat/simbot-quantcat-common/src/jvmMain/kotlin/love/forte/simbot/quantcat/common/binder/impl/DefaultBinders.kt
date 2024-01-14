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

package love.forte.simbot.quantcat.common.binder.impl

import love.forte.simbot.event.EventListenerContext
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.logger.logger
import love.forte.simbot.quantcat.common.binder.BindException
import love.forte.simbot.quantcat.common.binder.ParameterBinder
import kotlin.reflect.KParameter


/**
 * 将会直接抛出错误的binder。
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

        else -> ({
            Result.failure(BindException("Parameter(#${parameter.index}) [$parameter] has no binder."))
        })
    }

    override fun arg(context: EventListenerContext): Result<Any?> {
        return resultProvider()
    }

    override fun toString(): String = "EmptyBinder(parameter=$parameter)"
}


/**
 * 组合多个binder的 [ParameterBinder].
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
        if (binders.isEmpty()) throw IllegalArgumentException("Binders cannot be empty.")
    }


    override fun arg(context: EventListenerContext): Result<Any?> {
        var err: Throwable? = null
        val isOptional = parameter.isOptional

        fun ParameterBinder.invoke(): Result<Any?>? {
            val result = arg(context)
            if (result.isSuccess) {
                // if success, return.
                return result
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

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

package love.forte.simbot.quantcat.common.binder

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import love.forte.simbot.event.EventListenerContext
import love.forte.simbot.event.EventResult
import love.forte.simbot.quantcat.common.listener.FunctionalEventListener
import java.lang.reflect.InvocationTargetException
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KCallable
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.callSuspendBy
import kotlin.reflect.full.instanceParameter

/**
 * 可以进行动态参数绑定的 [FunctionalEventListener],
 * 可以通过 [binders] 对 [caller] 进行参数绑定。
 *
 * 在 [invoke] 中，如果 [caller] 是可挂起的（`isSuspend = true`）, 则通过可挂起执行。
 * 如果不是可挂起的，则会通过 [runInterruptible] 在可中断中普通执行。
 * [runInterruptible] 会使用 [dispatcherContext] 作为调度器。
 *
 */
public abstract class FunctionalBindableEventListener(
    private val instance: Any?,
    /**
     * 当前监听函数所对应的执行器。
     */
    public final override val caller: KFunction<*>,
    private val dispatcherContext: CoroutineContext = Dispatchers.IO
) : FunctionalEventListener() {

    /**
     * binder数组，其索引下标应当与
     * [KCallable.parameters] (不包括 INSTANCE)
     * 的 [KParameter.index] 相对应。
     * 在使用 [binders] 时，会直接按照其顺序转化为对应的值。
     */
    protected abstract val binders: Array<ParameterBinder>

    /**
     * 对结果的数据类型进行转化。
     */
    protected open fun convertValue(value: Any?, parameter: KParameter): Any? {
        return value
    }

    /**
     * 对 [caller] 执行后的返回值进行处理并转化为 [EventResult]. 可覆盖并自定义结果逻辑。
     *
     * - 如果结果是 [EventResult] 类型，则会直接返回.
     * - 如果返回值为 `null`, 则会返回 [EventResult.invalid].
     *
     * 否则通过 [EventResult.of] 转化为 [EventResult].
     *
     * 当 [result] 为 [Unit] 时，将其视为 `null`。
     *
     */
    protected open fun resultProcess(result: Any?): EventResult {
        return when (result) {
            is EventResult -> result
            null, Unit -> EventResult.empty()
            else -> EventResult.of(result)
        }
    }

    private val fullParameters: Array<KParameter> = caller.parameters.toTypedArray()
    private val parameters: Array<KParameter> =
        fullParameters.filter { it.kind != KParameter.Kind.INSTANCE }.toTypedArray()
    private val instanceParameter: KParameter? = caller.instanceParameter
    private val isOptional = caller.parameters.any { it.isOptional }
    private val initialSize = if (isOptional) 0 else caller.parameters.size.initialSize

    private val funcCall: (suspend (EventListenerContext) -> EventResult)

    init {
        if (isOptional) {
            // invokeCallBy
            funcCall = if (caller.isSuspend) {
                { context ->
                    invokeCallBy(context) { args -> caller.callSuspendBy(args) }
                }
            } else {
                { context ->
                    invokeCallBy(context) { args ->
                        runInterruptible(dispatcherContext) { caller.callBy(args) }
                    }
                }
            }
        } else {
            // invokeCall
            funcCall = if (caller.isSuspend) {
                { context ->
                    invokeCall(context) { args -> caller.callSuspend(args = args) }
                }
            } else {
                { context ->
                    invokeCall(context) { args ->
                        runInterruptible(dispatcherContext) { caller.call(args = args) }
                    }
                }
            }

        }
    }

    /**
     * 可能存在的整合到 [handle] 中的逻辑匹配。
     */
    protected abstract suspend fun match(context: EventListenerContext): Boolean

    /**
     * 函数执行。
     */
    override suspend fun EventListenerContext.handle(): EventResult {
        if (!match(this)) {
            return EventResult.invalid()
        }

        return funcCall(this)
    }

    private inline fun invokeCall(
        context: EventListenerContext,
        callFunction: (args: Array<Any?>) -> Any?
    ): EventResult {
        val args = arrayOfNulls<Any?>(binders.size)
        // first instance
        if (instance != null && instanceParameter != null) {
            args[0] = instance
        } else {
            args[0] = binders[0].arg(context).getOrElse { e ->
                if (e is java.net.BindException) throw e
                else throw BindException(e)
            }.let { value ->
                convertValue(value, fullParameters[0])
            }
        }

        // others
        repeat(binders.size - 1) { i ->
            val index = i + 1
            args[index] = binders[index].arg(context).getOrElse { e ->
                if (e is java.net.BindException) throw e
                else throw BindException(e)
            }.let { value ->
                convertValue(value, fullParameters[index])
            }
        }

        val result = try {
            callFunction(args)
        } catch (e: InvocationTargetException) {
            throw e.targetException ?: e
        }

        return resultProcess(result)
    }

    private inline fun invokeCallBy(
        context: EventListenerContext,
        callFunction: (Map<KParameter, Any?>) -> Any?
    ): EventResult {
        val args = LinkedHashMap<KParameter, Any?>(initialSize)

        binders.forEachIndexed { i, b ->
            if (i == 0 && (instance != null && instanceParameter != null)) {
                // include instance.
                args[instanceParameter] = instance
                return@forEachIndexed
            }
            val value = b.arg(context).getOrElse { e ->
                if (e is java.net.BindException) throw e
                else throw BindException(e)
            }
            if (value != ParameterBinder.Ignore) {
                val p = fullParameters[i]
                args[p] = convertValue(value, p)
            }
        }

        val result = try {
            callFunction(args)
        } catch (e: InvocationTargetException) {
            throw e.targetException ?: e
        }

        return resultProcess(result)
    }

    private inline val Int.initialSize: Int
        get() = ((toFloat() / 0.75F) + 1.0F).toInt()
}

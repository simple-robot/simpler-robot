/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     ExceptionProcessorImpl.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.exception

import love.forte.simbot.exception.ExceptionHandle
import love.forte.simbot.exception.ExceptionHandleContext
import love.forte.simbot.exception.ExceptionProcessor
import love.forte.simbot.exception.ExceptionProcessorBuilder
import java.lang.reflect.ParameterizedType
import kotlin.reflect.jvm.kotlinFunction



/**
 * [ExceptionProcessor] 的core默认实现，通过解析实现类的泛型类型来决定对应的处理类型。
 *
 * 其中，如果类型为[RuntimeException]、[Exception]、[Throwable]，则会在对应类型无法处理的时候被使用。
 *
 */
public class CoreExceptionProcessor(handles: Array<out ExceptionHandle<*>>) : ExceptionProcessor {

    /**
     * 异常处理集合。
     * 其中不包含[RuntimeException]、[Exception]、[Throwable]类型的处理。
     */
    private val handleMap: Map<Class<out Throwable>, ExceptionHandle<*>>

    private val runtimeExceptionHandle: ExceptionHandle<RuntimeException>?
    private val exceptionHandle: ExceptionHandle<Exception>?
    private val throwableHandle: ExceptionHandle<Throwable>?

    /**
     * 初始化，将handles数组转化为其各自对应的捕获类型。
     */
    init {
        // handle map
        // val handleInitMap = ConcurrentHashMap<Class<out Throwable>, ExceptionHandle<*>>()

        var runtimeExceptionHandle: ExceptionHandle<RuntimeException>? = null
        var exceptionHandle: ExceptionHandle<Exception>? = null
        var throwableHandle: ExceptionHandle<Throwable>? = null

        val handleGrouping = handles.asSequence().map {
            val handleJavaClass = it::class.java

            // method.
            val doHandleMethod = kotlin.runCatching {
                handleJavaClass.getMethod("doHandle", ExceptionHandleContext::class.java)
            }.getOrElse { e -> throw IllegalStateException("cannot found method 'doHandle' in $handleJavaClass", e) }

            // catch type.
            val catchType = kotlin.runCatching {
                val parameterizedType = doHandleMethod.parameters[0].parameterizedType as ParameterizedType
                @Suppress("UNCHECKED_CAST")
                parameterizedType.actualTypeArguments[0] as Class<out Throwable>
            }.getOrElse { e ->

                val funcParamName: String =
                    kotlin.runCatching {
                        doHandleMethod.kotlinFunction?.parameters?.get(0)?.name ?: doHandleMethod.parameters[0].name
                    }.getOrDefault("context")

                throw IllegalStateException(
                    "cannot confirm generic type for method parameter '$funcParamName' in $handleJavaClass(index:0) 's method $doHandleMethod",
                    e
                )
            }

            catchType to it
        }.groupBy({ it.first }, { it.second })
            .asSequence()
            .filter { it.value.isNotEmpty() }
            .map {
                val value = it.value
                if (value.size > 1) {
                    val showJoin = value.joinToString(", ", "[", "]")
                    throw IllegalStateException("There cannot be multiple exception handles for an exception type. but exception type '${it.key.name}' has ${value.size}: $showJoin")
                } else it.key to value[0]
            }
            .filter {
                if (it.first == RuntimeException::class.java) {
                    @Suppress("UNCHECKED_CAST")
                    runtimeExceptionHandle = it.second as ExceptionHandle<RuntimeException>
                    false
                } else true
            }
            .filter {
                if (it.first == Exception::class.java) {
                    @Suppress("UNCHECKED_CAST")
                    exceptionHandle = it.second as ExceptionHandle<Exception>
                    false
                } else true
            }
            .filter {
                if (it.first == Throwable::class.java) {
                    @Suppress("UNCHECKED_CAST")
                    throwableHandle = it.second as ExceptionHandle<Throwable>
                    false
                } else true
            }
            .toMap()

        this.runtimeExceptionHandle = runtimeExceptionHandle
        this.exceptionHandle = exceptionHandle
        this.throwableHandle = throwableHandle

        // handleMap
        handleMap = handleGrouping
    }

    /**
     * 获取一个类型对应的异常处理器。如果获取不到则为null。
     */
    @Suppress("UNCHECKED_CAST")
    override fun <E : Throwable> getHandle(exType: Class<out E>): ExceptionHandle<E>? {
        return (
                when (exType) {
                    RuntimeException::class.java -> runtimeExceptionHandle
                    Exception::class.java -> exceptionHandle
                    Throwable::class.java -> throwableHandle
                    else -> null
                } ?: getHandleFromMap(exType) ?: when {
                    // is runtime
                    runtimeExceptionClass.isAssignableFrom(exType) -> runtimeExceptionHandle ?: exceptionHandle
                    ?: throwableHandle
                    // is normal
                    exceptionClass.isAssignableFrom(exType) -> exceptionHandle ?: throwableHandle
                    // is throwable
                    else -> throwableHandle
                }
                ) as? ExceptionHandle<E>
    }


    /**
     * 从 [handleMap] 中查询一个对应类型或对应子类型的异常处理器。
     */
    @Suppress("UNCHECKED_CAST")
    private fun <E : Throwable> getHandleFromMap(exType: Class<out E>): ExceptionHandle<E>? {
        return (handleMap[exType] ?: run {
            handleMap.entries.find { it.key.isAssignableFrom(exType) }?.value
        }) as? ExceptionHandle<E>
    }

    companion object ExceptionClassCompanion {
        private val runtimeExceptionClass = RuntimeException::class.java
        private val exceptionClass = Exception::class.java
    }

}


/**
 * [ExceptionProcessorBuilder] 的core默认实现，可用于构建一个 [ExceptionProcessor] 实例。
 */
public class CoreExceptionProcessorBuilder : ExceptionProcessorBuilder {
    private val handleList = mutableListOf<ExceptionHandle<*>>()

    /**
     * 追加注册一个或多个异常处理器。
     */
    override fun register(vararg handle: ExceptionHandle<*>): ExceptionProcessorBuilder {
        handleList.addAll(handle)
        return this
    }

    /**
     * 构建 [CoreExceptionProcessor] 实例并返回。
     */
    override fun build(): ExceptionProcessor = CoreExceptionProcessor(handleList.toTypedArray())

}
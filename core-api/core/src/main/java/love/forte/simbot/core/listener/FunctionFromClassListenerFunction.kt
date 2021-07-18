/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */

package love.forte.simbot.core.listener

import love.forte.common.ioc.DependBeanFactory
import love.forte.common.ioc.annotation.Depend
import love.forte.common.utils.annotation.AnnotationUtil
import love.forte.common.utils.convert.ConverterManager
import love.forte.simbot.LogAble
import love.forte.simbot.annotation.*
import love.forte.simbot.api.SimbotExperimentalApi
import love.forte.simbot.api.SimbotInternalApi
import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.filter.FilterManager
import love.forte.simbot.filter.ListenerFilter
import love.forte.simbot.listener.*
import love.forte.simbot.utils.MD5
import love.forte.simbot.utils.getAnnotation
import love.forte.simbot.utils.isStringOrPrimitive
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.reflect.InvocationTargetException
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KTypeParameter
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.isSuperclassOf
import kotlin.reflect.jvm.javaMethod


/**
 *
 * 基于反射获取的 [KFunction] 实现的 [ListenerFunction].
 *
 * 需要注意 [function] 要能够表达为[JavaMethod][KFunction.javaMethod]. 否则你可以参考 [FunctionListenerFunction].
 *
 * @author ForteScarlet
 */
@OptIn(SimbotInternalApi::class, SimbotExperimentalApi::class)
public class FunctionFromClassListenerFunction constructor(
    private val function: KFunction<*>,
    private val instanceName: String?,
    override val type: Class<*>,
    private val dependBeanFactory: DependBeanFactory,
    private val filterManager: FilterManager,
    private val converterManager: ConverterManager,
    private val listenerResultFactory: ListenerResultFactory,
    listenerGroupManager: ListenerGroupManager,
    strict: Boolean,
) : ListenerFunction, LogAble {
    override val log: Logger = LoggerFactory.getLogger(function.toString() + "." + function.name)

    private val functionCaller: suspend (instance: Any?, params: Array<*>) -> Any? =
        if (function.isSuspend) {
            { i, p -> function.callSuspend(i, *p) }
        } else {
            { i, p -> function.call(i, *p) }
        }

    /**
     * 此监听函数的唯一编号，用于防止出现重复冲突。
     * 在判断重复性前，会优先判断此id，再使用equals。
     * 如果id相同，则认为其相同，则不再使用equals判断。
     */
    override val id: String

    /**
     * 监听函数的名称。
     */
    override val name: String

    /**
     * 此监听函数上的 [Listens] 注解。
     */
    private val listensAnnotation: Listens

    /**
     * 此监听函数上的 [Filters] 注解。如果有的话。
     */
    private val filtersAnnotation: Filters?

    /**
     * 此监听函数上的 [ListenBreak] 注解。如果有的话。
     */
    private val listenBreakAnnotation: ListenBreak?

    /**
     * 此函数是否属于一个 **备用函数**.
     *
     * @see love.forte.simbot.annotation.SpareListen
     */
    override val spare: Boolean

    /**
     * 此监听函数的优先级。
     */
    override val priority: Int

    /**
     * 监听的类型们。
     */
    override val listenTypes: Set<Class<out MsgGet>>

    /**
     * 此监听函数对应的监听过滤器。
     */
    override val filter: ListenerFilter?

    /**
     * [groups] 应当是一个不可变列表。
     */
    override val groups: List<ListenerGroup>


    /**
     * 此监听函数的实例获取函数。
     */
    private val listenerInstanceGetter: () -> Any?

    /**
     * 此方法的参数获取函数。
     */
    private val methodParamsGetter: (ListenerFunctionInvokeData) -> Array<*>

    private companion object Types {
        private inline val priorityType get() = Priority::class.java
        private inline val ListensType get() = Listens::class.java
        private inline val FiltersType get() = Filters::class.java
        private inline val ListenBreakType get() = ListenBreak::class.java
        private inline val SpareListenType get() = SpareListen::class.java
        private val LOG = LoggerFactory.getLogger(FunctionFromClassListenerFunction::class.java)
        private val NO_STRICT_WARN: Int by lazy(LazyThreadSafetyMode.NONE) {
            LOG.warn("Strict mode was disabled for core function listener. ")
            0
        }

    }


    private val fAnnotationElement = function.javaMethod
        ?: throw IllegalStateException("KFunction $function cannot represented by a Java method.")


    init {
        val methodAnnotation = AnnotationUtil.getAnnotation(fAnnotationElement, ListensType)
        val typeAnnotation = AnnotationUtil.getAnnotation(type, ListensType)

        // 只有函数上有
        when {
            methodAnnotation != null && typeAnnotation == null -> {
                listensAnnotation = methodAnnotation
                listenTypes = listensAnnotation.value.mapTo(mutableSetOf()) { it.value.java }
            }
            typeAnnotation != null && methodAnnotation == null -> {
                // 只有类上有
                listensAnnotation = typeAnnotation
                listenTypes = listensAnnotation.value.mapTo(mutableSetOf()) { it.value.java }

            }
            typeAnnotation == null && methodAnnotation == null -> {
                // 都没有
                throw IllegalStateException("cannot found annotation '@Listens' in function $function")
            }
            else -> {
                // 都有, 注解优先为方法上的
                listensAnnotation = methodAnnotation
                // 但是如果方法上没有监听的类型，则监听类型为类上的
                val typeValues = typeAnnotation.value
                val methodValues = methodAnnotation.value
                listenTypes = if (methodValues.isNotEmpty()) {
                    // 只要方法上的有类型，就使用方法上的
                    methodAnnotation.value.mapTo(mutableSetOf()) { it.value.java }

                } else {
                    // 方法上没有类型，使用类上的
                    typeValues.mapTo(mutableSetOf()) { it.value.java }
                }
            }
        }

        // 监听注解
        // listensAnnotation = AnnotationUtil.getAnnotation(fAnnotationElement, ListensType)
        //     ?: type.let { declaringClass ->
        //         AnnotationUtil.getAnnotation(declaringClass, ListensType)
        //     } ?: throw IllegalStateException("cannot found annotation '@Listens' in function $function")

        // 优先级值
        priority = AnnotationUtil.getAnnotation(fAnnotationElement, priorityType)?.value ?: listensAnnotation.priority

        // 过滤注解
        filtersAnnotation = AnnotationUtil.getAnnotation(fAnnotationElement, FiltersType)

        // 判断是否存在@Spare注解
        spare = AnnotationUtil.containsAnnotation(fAnnotationElement, SpareListenType)

        listenBreakAnnotation = AnnotationUtil.getAnnotation(fAnnotationElement, ListenBreakType)

        id = function.toListenerId(type, listensAnnotation)
        name = function.toListenerName(listensAnnotation)

        // 分组
        val parentGroupAnnotation =
            AnnotationUtil.getAnnotation(type, ListenGroup::class.java)?.takeIf { a -> a.value.isNotEmpty() }
        val methodGroupAnnotation =
            AnnotationUtil.getAnnotation(fAnnotationElement, ListenGroup::class.java)
                ?.takeIf { a -> a.value.isNotEmpty() }

        val groupNames = when {
            parentGroupAnnotation != null && methodGroupAnnotation != null ->
                if (methodGroupAnnotation.append) parentGroupAnnotation.value + methodGroupAnnotation.value
                else methodGroupAnnotation.value
            methodGroupAnnotation != null -> methodGroupAnnotation.value
            parentGroupAnnotation != null -> parentGroupAnnotation.value
            else -> emptyArray()
        }

        groups = listenerGroupManager.assignGroup(this, *groupNames)

        listenerInstanceGetter = { dependBeanFactory[instanceName] }


        // filter
        filter = filtersAnnotation?.let {
            filterManager.getFilter(it)
        }

        val parameterGetters: List<(ListenerFunctionInvokeData) -> Any?> = function.getParameterGetters(strict)

        // 方法参数获取函数
        methodParamsGetter = { d -> Array(parameterGetters.size) { i -> parameterGetters[i](d) } }

    }

    private fun KFunction<*>.getParameterGetters(strict: Boolean): List<(ListenerFunctionInvokeData) -> Any?> =
        parameters.mapIndexedNotNull { i, p ->
            if (p.kind == KParameter.Kind.INSTANCE) null else p.toParameterGetter(i,
                strict)
        }


    @OptIn(SimbotExperimentalApi::class)
    private fun KParameter.toParameterGetter(i: Int, strict: Boolean): (ListenerFunctionInvokeData) -> Any? {


        val contextValue: ContextValue? = this.getAnnotation()
        val filterValue: FilterValue? = this.getAnnotation()
        val dependAnnotation: Depend? = this.getAnnotation()

        val orNull: Boolean = dependAnnotation?.orIgnore ?: this.type.isMarkedNullable

        val parameterClassifier =
            this.type.classifier ?: throw IllegalStateException("Parameter $this is not denotable in Kotlin ")
        if (parameterClassifier !is KClass<*>) {
            if (parameterClassifier is KTypeParameter) {
                throw IllegalStateException("TypeParameter $this not support yet.")
            } else {
                throw IllegalStateException("Unknown parameter type for $this")
            }
        }
        val parameterType: KClass<*> = parameterClassifier


        // if this parameter type is MsgGet, check and warn if need.
        if (!orNull && MsgGet::class.isSuperclassOf(parameterType)) {
            // 如果你所监听的类型中没有你填入参数的类型，且orIgnore=false
            val none = listenTypes.none { listenType ->
                parameterType.isSuperclassOf(listenType.kotlin)
            }

            if (none) {
                val (logInfo, logExInfo) = if (!orNull) {
                    ListenerParameterTypeMismatchWarn("Listener function ($name) parameter($i) type mismatch. Listened: $listenTypes, but: $parameterType. This is likely to cause an exception.",
                        "Listener function ($name)'s parameter($i) not being listened and will not be ignored. You Listen: '$listenTypes', but: '$parameterType'. This is likely to cause an exception.")
                } else {
                    ListenerParameterTypeMismatchWarn("Listener function ($name) parameter($i) type mismatch. Listened: $listenTypes, but: $parameterType. It will always be null.",
                        "Listener function ($name)'s parameter($i) not being listened. You Listen: '$listenTypes', but: '$parameterType'. Since Parameter($i) can be omitted, it will always be null.")
                }
                if (log.isDebugEnabled) {
                    log.warn(logInfo)
                    log.debug("", ListenerParameterTypeMismatchException(logExInfo))
                } else {
                    log.warn(logInfo, ListenerParameterTypeMismatchException(logExInfo))
                }

            }
        }

        return when {
            // 从过滤值中拿参数
            filterValue != null -> {
                val filterValueName = filterValue.value.ifBlank { this.name } ?: kotlin.run {
                    val def = "arg$i"
                    // Unable to determine the name of the filter value in parameter($i) $this.
                    // log.warn("Cannot get parameter({})'s name, use default: {}", this.toString(), def)
                    log.warn("Unable to determine the name of the filter value name in parameter({}) {}",
                        i,
                        this.toString())
                    def
                }

                // val parameterType = parameterType

                if (orNull) {
                    // null able
                    f@{ d ->
                        val findValue: String? = filter?.let { f ->
                            d.msgGet.text?.let { text ->
                                f.getFilterValue(filterValueName, text)
                            }
                        }
                        // val text: String = d.msgGet.text ?: return@f null
                        // val findValue: String? = filter?.getFilterValue(filterValueName, text)
                        if (findValue == null) {
                            null
                        } else {
                            converterManager.convert(parameterType.java, findValue)
                        }
                    }
                } else {
                    // non null.
                    f@{ d ->
                        val text: String = d.msgGet.text
                            ?: throw IllegalStateException("Msg ${d.msgGet} unable to get msg.")
                        val findValue = filter?.getFilterValue(filterValueName, text)
                        if (findValue == null) {
                            throw IllegalStateException("Unable to extract filter value '$filterValueName' in parameter($i) $this.")
                        } else {
                            converterManager.convert(parameterType.java, findValue)
                        }
                    }
                }
            }

            // 从上下文中获取
            contextValue != null -> {
                val findKey = contextValue.value
                val scopes = contextValue.scopes
                val orNull = contextValue.orNull

                if (scopes.isEmpty()) {
                    if (!orNull) {
                        // empty and non-null, throw.
                        throw IllegalStateException("$this ContextValue's scope is empty, but non-null.")
                    } else {
                        // empty.
                        log.warn("{} ContextValue's scope is empty, and nullable, always null.", this)
                        return f@{ null }
                    }

                } else {

                    f@{ d ->
                        d.context.let { context ->
                            for (scope in scopes) {
                                val v = context[scope][findKey]
                                if (v != null) return@let v
                            }
                            if (orNull) return@let null

                            throw ContextValueNotFoundException("Cannot found '$findKey' from ${
                                scopes.joinToString(",",
                                    "[",
                                    "]")
                            }")
                        }
                    }
                }


            }

            // 有@Depend
            dependAnnotation != null -> {
                val dependName: String? = dependAnnotation.value.ifBlank { this.name }

                if (orNull) {
                    // or null
                    if (dependName != null) {
                        // by name
                        { dependBeanFactory.getOrNull(dependName) }
                    } else {
                        // by type
                        val type = dependAnnotation.type.takeIf { dt -> dt != Void::class } ?: parameterType
                        { d ->
                            // 如果当前的动态参数msgGet的类型正好是此参数的类型的子类，直接使用
                            val msgGet: MsgGet = d.msgGet
                            when {
                                type.isSuperclassOf(msgGet::class) -> msgGet
                                type.isSuperclassOf(this@FunctionFromClassListenerFunction::class) -> this@FunctionFromClassListenerFunction
                                else -> d[type.java] ?: dependBeanFactory.getOrNull(type.java)
                            }
                        }
                    }
                } else {
                    // non null
                    // by type
                    val type = dependAnnotation.type.takeIf { dt -> dt != Void::class } ?: parameterType
                    { d ->
                        // 如果获取到的msgGet的类型正好是此参数的类型的子类，直接使用
                        val msgGet: MsgGet = d.msgGet
                        when {
                            type.isSuperclassOf(msgGet::class) -> msgGet
                            type.isSuperclassOf(this@FunctionFromClassListenerFunction::class) -> this@FunctionFromClassListenerFunction
                            else -> d[type.java] ?: dependBeanFactory[type.java]
                        }
                    }
                }
            }

            // 什么注解也没有, by type.
            else -> {
                if (strict) {
                    // strict
                    // by type
                    val type = dependAnnotation?.type?.takeIf { dt -> dt != Void::class } ?: parameterType
                    if (type.isStringOrPrimitive()) {
                        log.warn("{} Parameter(name={}, index={})'s type is String or primitive types. In strict mode, String or primitive types do not support injection by depend manager. This has a high probability of causing an exception, and you may need to mark it as @FilterValue or @ContextValue, or turn off strict mode.",
                            this@FunctionFromClassListenerFunction.name,
                            this.name,
                            this.index)
                        return if (orNull) {
                            { d -> d[type.java] ?: dependBeanFactory.getOrNull(type.java) }
                        } else {
                            { d -> d[type.java] ?: dependBeanFactory[type.java] }
                        }
                    }

                    if (orNull) {
                        { d ->
                            // 如果当前的动态参数msgGet的类型正好是此参数的类型的子类，直接使用
                            val msgGet: MsgGet = d.msgGet
                            when {
                                type.isSuperclassOf(msgGet::class) -> msgGet
                                type.isSuperclassOf(this@FunctionFromClassListenerFunction::class) -> this@FunctionFromClassListenerFunction
                                else -> d[type.java] ?: dependBeanFactory.getOrNull(type.java)
                            }
                        }
                    } else {
                        // not null
                        { d ->
                            // 如果当前的动态参数msgGet的类型正好是此参数的类型的子类，直接使用
                            val msgGet: MsgGet = d.msgGet
                            when {
                                type.isSuperclassOf(msgGet::class) -> msgGet
                                type.isSuperclassOf(this@FunctionFromClassListenerFunction::class) -> this@FunctionFromClassListenerFunction
                                else -> d[type.java] ?: dependBeanFactory[type.java]
                            }
                        }
                    }

                } else {
                    // not strict
                    NO_STRICT_WARN
                    val type = dependAnnotation?.type?.takeIf { dt -> dt != Void::class } ?: parameterType
                    val name = this.name ?: kotlin.run {
                        val def = "arg$i"
                        // Unable to determine the name of the filter value in parameter($i) $this.
                        // log.warn("Cannot get parameter({})'s name, use default: {}", this.toString(), def)
                        log.warn("Unable to determine the name of the filter value name in parameter({}) {}",
                            i,
                            this.toString())
                        def
                    }

                    // 在全局寻找, type最后
                    fun findInAll(d: ListenerFunctionInvokeData): Any? {
                        return filter?.let { f ->
                            d.msgGet.text?.runCatching {
                                f.getFilterValue(name, this)
                            }?.getOrNull()
                        }?.let { fv -> converterManager.convert(type.java, fv) }
                            ?: run {
                                for (scope in ListenerContext.Scope.values()) {
                                    d.context.let { context ->
                                        val v = context[scope][name]
                                        if (v != null) return@run v
                                    }
                                }
                                null
                            } ?: d[type.java] ?: dependBeanFactory.getOrNull(type.java)
                    }

                    // 在全局寻找, type最后
                    fun findInAllTypeFirst(d: ListenerFunctionInvokeData): Any? {
                        return d[type.java]
                            ?: dependBeanFactory.getOrNull(type.java)
                            ?: filter?.let { f ->
                                d.msgGet.text?.runCatching {
                                    f.getFilterValue(name, this)
                                }?.getOrNull()
                            }?.let { fv -> converterManager.convert(type.java, fv) }
                            ?: run {
                                for (scope in ListenerContext.Scope.values()) {
                                    d.context.let { context ->
                                        val v = context[scope][name]
                                        if (v != null) return@run v
                                    }
                                }
                                null
                            }
                    }

                    fun findInAllTypeFirstOrGet(d: ListenerFunctionInvokeData): Any? {
                        val msgGet: MsgGet = d.msgGet
                        return when {
                            type.isSuperclassOf(msgGet::class) -> msgGet
                            type.isSuperclassOf(this@FunctionFromClassListenerFunction::class) -> this@FunctionFromClassListenerFunction
                            else -> findInAllTypeFirst(d)
                        }
                    }




                    if (type.isStringOrPrimitive()) {
                        log.warn("{} Parameter(name={}, index={})'s type is String or primitive types. In non-strict mode, the filter value will be tried first, followed by the context value, and finally by the dependency.",
                            this@FunctionFromClassListenerFunction.name, name, this.index)

                        return if (orNull) {
                            ::findInAll
                        } else {
                            { d ->
                                findInAll(d)
                                    ?: throw IllegalArgumentException("The value of $name cannot be found in all places including FilterValue、ContextValue with all scopes and Dependencies.")
                            }
                        }
                    }

                    if (orNull) {
                        ::findInAllTypeFirstOrGet
                    } else {
                        { d ->
                            findInAllTypeFirstOrGet(d)
                                ?: throw IllegalArgumentException("The value of $name cannot be found in all places including FilterValue、ContextValue with all scopes and Dependencies.")
                        }
                    }
                }


                // // not filterValue
                // val dependName: String? = dependAnnotation?.value?.ifBlank { this.name }
                //
                // if (orNull) {
                // } else {
                //     // non null
                //     if (dependName != null) {
                //         // by name
                //         { dependBeanFactory[dependName] }
                //     } else {
                //         // by type
                //         val type = dependAnnotation?.type?.takeIf { dt -> dt != Void::class } ?: parameterType
                //         { d ->
                //             // 如果获取到的msgGet的类型正好是此参数的类型的子类，直接使用
                //             val msgGet: MsgGet = d.msgGet
                //             when {
                //                 type.isSuperclassOf(msgGet::class) -> msgGet
                //                 type.isSuperclassOf(this@FunctionFromClassListenerFunction::class) -> this
                //                 else -> d[type.java] ?: dependBeanFactory[type.java]
                //             }
                //         }
                //     }
                // }
            }

        }
    }

    /**
     * 获取此监听函数上可以得到的注解。
     */
    @Suppress("UNCHECKED_CAST")
    override fun <A : Annotation> getAnnotation(type: Class<out A>): A? = when (type) {
        ListensType -> listensAnnotation as A
        FiltersType -> filtersAnnotation as? A
        ListenBreakType -> listenBreakAnnotation as? A
        else -> AnnotationUtil.getAnnotation(fAnnotationElement, type)
    }

    private val invoker: suspend (data: ListenerFunctionInvokeData) -> ListenResult<*> = {
        it.let { data ->
            // 获取实例
            val instance: Any? = runCatching {
                listenerInstanceGetter()
            }.getOrElse { e ->
                return@let listenerResultFactory.getResult(null, this, e)
            }

            // 获取方法参数
            val params: Array<*> = runCatching {
                methodParamsGetter(data)
            }.getOrElse { e ->
                return@let listenerResultFactory.getResult(null, this, e)
            }

            // 执行方法
            val invokeResult: Any? = runCatching {
                functionCaller(instance, params)
                // method(instance, *params)
            }.getOrElse { e ->
                val cause = if (e is InvocationTargetException) {
                    e.targetException
                } else e
                return@let listenerResultFactory.getResult(null, this, cause)
            }

            // set result
            // resultBuilder.result = invokeResult

            // build listen result.
            return@let listenerResultFactory.getResult(invokeResult, this)
        }
    }


    @Volatile
    private var realInvoker: suspend (data: ListenerFunctionInvokeData) -> ListenResult<*> = invoker


    override suspend fun invoke(data: ListenerFunctionInvokeData): ListenResult<*> = realInvoker(data)

    //     // 获取实例
    //     val instance: Any? = runCatching {
    //         listenerInstanceGetter()
    //     }.getOrElse {
    //         return listenerResultFactory.getResult(null, this, it)
    //     }
    //
    //     // 获取方法参数
    //     val params: Array<*> = runCatching {
    //         methodParamsGetter(data)
    //     }.getOrElse {
    //         return listenerResultFactory.getResult(null, this, it)
    //     }
    //
    //     // 执行方法
    //     val invokeResult: Any? = runCatching {
    //         functionCaller(instance, params)
    //         // method(instance, *params)
    //     }.getOrElse {
    //         val cause = if (it is InvocationTargetException) {
    //             it.targetException
    //         } else it
    //         return listenerResultFactory.getResult(null, this, cause)
    //     }
    //
    //     // set result
    //     // resultBuilder.result = invokeResult
    //
    //     // build listen result.
    //     return listenerResultFactory.getResult(invokeResult, this)
    // }

    @OptIn(SimbotExperimentalApi::class)
    override val switch: ListenerFunction.Switch = Switch()


    @OptIn(SimbotExperimentalApi::class)
    private inner class Switch : ListenerFunction.Switch {
        override fun enable() {
            realInvoker = invoker
        }

        override fun disable() {
            realInvoker = ListenerFunction.Switch.DISABLE_FUNCTION_INVOKER
        }

        override val isEnable: Boolean
            get() = realInvoker === invoker
    }

}


/**
 * method 取得唯一ID。直接获取完整路径。
 */
internal fun KFunction<*>.toListenerId(declaringClass: Class<*>, listens: Listens): String {
    return listens.id.ifBlank {
        val methodName = this@toListenerId.name
        val methodParameters = this@toListenerId.parameters
        val methodReturnType = this@toListenerId.returnType

        val wholeName =
            "${declaringClass.name} $methodReturnType $methodName(${methodParameters.joinToString(", ") { "${it.type} ${it.name}" }})"

        val wholeNameMD5: String = MD5[wholeName]

        "${declaringClass.typeName}.$methodName#$wholeNameMD5"
    }

}

/**
 * method 取得展示name
 */
internal fun KFunction<*>.toListenerName(listens: Listens): String = with(listens.name) {
    ifBlank { this@toListenerName.name }
}

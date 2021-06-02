/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
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
@file:JvmName("MethodListenerFunctions")

package love.forte.simbot.core.listener

import love.forte.common.ioc.DependBeanFactory
import love.forte.common.ioc.annotation.Depend
import love.forte.common.utils.annotation.AnnotationUtil
import love.forte.common.utils.convert.ConverterManager
import love.forte.simbot.LogAble
import love.forte.simbot.SimbotIllegalArgumentException
import love.forte.simbot.annotation.*
import love.forte.simbot.api.SimbotExperimentalApi
import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.filter.AtDetection
import love.forte.simbot.filter.FilterData
import love.forte.simbot.filter.FilterManager
import love.forte.simbot.filter.ListenerFilter
import love.forte.simbot.listener.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.security.MessageDigest
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.kotlinFunction

/**
 *
 * 以Method实例为执行载体的 [ListenerFunction] 实例。
 *
 * @property method method实例。
 * @property dependBeanFactory 依赖工厂。需要其可以获取除了 [MsgGet] 以外的动态参数，
 * 例如 [love.forte.simbot.api.sender.Sender]
 * 或 [love.forte.simbot.filter.AtDetection]。
 * @property filterManager 过滤器工厂。
 * @property
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
@OptIn(SimbotExperimentalApi::class)
@Suppress("JoinDeclarationAndAssignment")
public class MethodListenerFunction(
    private val method: Method,
    private val instanceName: String?,
    declClass: Class<*>,
    private val dependBeanFactory: DependBeanFactory,
    private val filterManager: FilterManager,
    private val converterManager: ConverterManager,
    private val listenerResultFactory: ListenerResultFactory,

    ) : ListenerFunction, LogAble {
    override val log: Logger = LoggerFactory.getLogger(method.declaringClass.typeName + "." + method.name)

    private val isStatic: Boolean = Modifier.isStatic(method.modifiers)

    /**
     * 此监听函数上的 [Listens] 注解。
     */
    private val listensAnnotation: Listens

    /**
     * 此监听函数上的 [Filters] 注解。如果有的话。
     */
    private val filtersAnnotation: Filters?

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
     * 此监听函数上的 [ListenBreak] 注解。如果有的话。
     */
    private val listenBreakAnnotation: ListenBreak?

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
     * 监听的类型们。
     */
    override val listenTypes: Set<Class<out MsgGet>>


    /**
     * [Filters] 注解对应的监听函数。
     */
    private val listenAnnotationFilter: ListenerFilter?


    /**
     * 此监听函数对应的监听器列表。
     */
    override val filters: List<ListenerFilter>
        get() = (listenAnnotationFilter?.let { listOf(it) } ?: emptyList())


    /**
     * 执行过滤。
     */
    private fun doFilter(
        msgGet: MsgGet,
        atDetection: AtDetection,
        listenerContext: ListenerContext,
    ): Boolean = listenAnnotationFilter?.let { annotationFilter ->
        val data = FilterData(msgGet, atDetection, listenerContext, this)
        return annotationFilter.test(data)
    } ?: true


    /**
     * 当前监听函数的载体。例如一个 Class。
     * 一般如果是个method，那么此即为[Class]。
     */
    override val type: Class<*> =
        if (isStatic) {
            declClass
        } else {
            method.declaringClass
        }


    /**
     * 此监听函数的实例获取函数。
     */
    private val listenerInstanceGetter: () -> Any?

    /**
     * 此方法的参数获取函数。
     */
    private val methodParamsGetter: (ListenerFunctionInvokeData) -> Array<*>

    private companion object Types {
        // private val msgSenderType = MsgSender::class.java
        // private val senderType = Sender::class.java
        // private val getterType = Getter::class.java
        // private val setterType = Setter::class.java
        // private val atDetectionType = AtDetection::class.java
        // private val listenerContextType = ListenerContext::class.java
        // private val botType = Bot::class.java

        private inline val priorityType get() = Priority::class.java
        private inline val ListensType get() = Listens::class.java
        private inline val FiltersType get() = Filters::class.java
        private inline val ListenBreakType get() = ListenBreak::class.java
        private inline val SpareListenType get() = SpareListen::class.java
        // private val MsgGetType = MsgGet::class.java
    }

    init {
        // 监听注解
        listensAnnotation = AnnotationUtil.getAnnotation(method, ListensType)
            ?: method.declaringClass?.let { declaringClass ->
                AnnotationUtil.getAnnotation(declaringClass,
                    ListensType)
            }
                    ?: throw IllegalStateException("cannot found annotation '@Listens' in method $method")

        // 优先级值
        priority = AnnotationUtil.getAnnotation(method, priorityType)?.value ?: listensAnnotation.priority

        // 过滤注解
        filtersAnnotation = AnnotationUtil.getAnnotation(method, FiltersType)

        // 判断是否存在@Spare注解
        spare = AnnotationUtil.containsAnnotation(method, SpareListenType)


        listenBreakAnnotation = AnnotationUtil.getAnnotation(method, ListenBreakType)

        id = method.toListenerId(listensAnnotation)
        name = method.toListenerName(listensAnnotation)


        listenerInstanceGetter = if (isStatic) {
            ::nullInstanceGetter
        } else {
            { dependBeanFactory[instanceName] }
        }

        // 监听类型列表
        listenTypes = listensAnnotation.value.mapTo(mutableSetOf()) { it.value.java }

        listenAnnotationFilter = filtersAnnotation?.let {
            filterManager.getFilter(it)
        }

        val kFunction: KFunction<*>? = method.kotlinFunction
        val ktParameters = kFunction?.parameters

        val firstInstance = ktParameters?.firstOrNull()?.kind == KParameter.Kind.INSTANCE


        // init method args getter.
        val parameterGetters: List<(ListenerFunctionInvokeData) -> Any?> =
            method.parameters.mapIndexed { i, methodParameter ->
                val contextValue: ContextValue? =
                    AnnotationUtil.getAnnotation(methodParameter, ContextValue::class.java)
                val filterValue: FilterValue? = AnnotationUtil.getAnnotation(methodParameter, FilterValue::class.java)
                val dependAnnotation: Depend? = AnnotationUtil.getAnnotation(methodParameter, Depend::class.java)


                val orIgnore: Boolean = dependAnnotation?.orIgnore ?: kotlin.runCatching {
                    // try as kt param
                    // val kParameter: KParameter? = method.kotlinFunction?.parameters?.get(i)
                    val parameter = ktParameters?.get(if (firstInstance) i + 1 else i)
                    parameter?.type?.isMarkedNullable ?: false
                }.getOrDefault(false)

                val parameterType = methodParameter.type

                // if this parameter type is MsgGet, check and warn if need.
                if (!orIgnore && MsgGet::class.java.isAssignableFrom(parameterType)) {
                    // 如果你所监听的类型中没有你填入参数的类型，且orIgnore=false
                    val none = listenTypes.none { listenType ->
                        parameterType.isAssignableFrom(listenType)
                    }



                    if (none) {
                        val (logInfo, logExInfo) = if (!orIgnore) {
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


                when {
                    // 从过滤值中拿参数
                    filterValue != null -> {
                        val filterValueName = kotlin.runCatching {
                            filterValue.value.ifBlank {
                                method.kotlinFunction?.parameters?.get(i)?.name ?: methodParameter.name
                            }
                        }.getOrElse { e ->
                            throw IllegalStateException(
                                "Unable to determine the name of the filter value in method $method($i).",
                                e
                            )
                        }

                        // val parameterType = parameterType

                        if (orIgnore) {
                            f@{ d ->
                                val text: String = d.msgGet.text ?: return@f null
                                val findValue: String? = listenAnnotationFilter?.getFilterValue(filterValueName, text)
                                if (findValue == null) {
                                    null
                                } else {
                                    converterManager.convert(parameterType, findValue)
                                }
                            }
                        } else {
                            f@{ d ->
                                val text: String = d.msgGet.text
                                    ?: throw IllegalStateException("Msg ${d.msgGet} unable to get msg.")
                                val findValue = listenAnnotationFilter?.getFilterValue(filterValueName, text)
                                if (findValue == null) {
                                    throw IllegalStateException("Unable to extract filter value '$filterValueName' in method $method.")
                                } else {
                                    converterManager.convert(parameterType, findValue)
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
                                throw IllegalStateException("Your")
                            }

                            // empty.
                            f@{ null }

                        } else {

                            f@{ d ->
                                d.context.let { context ->
                                    for (scope in scopes) {
                                        val v = context[scope][findKey]
                                        if (v != null) return@let v
                                    }
                                    if (orNull) return@let null

                                    throw ContextValueNotFoundException("Cannot found '$findKey' from ${scopes.joinToString(",", "[", "]")}")
                                }
                            }
                        }


                    }


                    // 什么注解也没有
                    else -> {
                        // not filterValue
                        val dependName: String? = dependAnnotation?.value?.ifBlank { null }
                        if (orIgnore) {
                            if (dependName != null) {
                                { dependBeanFactory.getOrNull(dependName) }
                            } else {
                                val type =
                                    dependAnnotation?.type?.takeIf { dt -> dt.java != Void::class.java }?.java
                                        ?: parameterType
                                { d ->
                                    // 如果当前的动态参数msgGet的类型正好是此参数的类型的子类，直接使用
                                    val msgGet: MsgGet = d.msgGet
                                    if (type.isAssignableFrom(msgGet.javaClass)) msgGet
                                    else d[type] ?: dependBeanFactory.getOrNull(type)
                                }
                            }
                        } else {
                            if (dependName != null) {
                                { dependBeanFactory[dependName] }
                            } else {
                                val type =
                                    dependAnnotation?.type?.java?.takeIf { dt -> dt != Void::class.java }
                                        ?: parameterType
                                { d ->
                                    // 如果获取到的msgGet的类型正好是此参数的类型的子类，直接使用
                                    val msgGet: MsgGet = d.msgGet
                                    if (type.isAssignableFrom(msgGet.javaClass)) msgGet
                                    else d[type] ?: dependBeanFactory[type]
                                }
                            }
                        }

                    }

                }
            }


        // 方法参数获取函数
        methodParamsGetter = { d -> Array(parameterGetters.size) { i -> parameterGetters[i](d) } }
    }


    /**
     * 获取此监听函数上可以得到的注解。
     */
    @Suppress("UNCHECKED_CAST")
    override fun <A : Annotation> getAnnotation(type: Class<out A>): A? = when (type) {
        ListensType -> listensAnnotation as A
        FiltersType -> filtersAnnotation as? A
        ListenBreakType -> listenBreakAnnotation as? A
        else -> AnnotationUtil.getAnnotation(method, type)
    }


    /**
     * 执行监听函数并返回一个执行后的响应结果。
     */
    override fun invoke(data: ListenerFunctionInvokeData): ListenResult<*> {
        // do filter
        val filter: Boolean = doFilter(data.msgGet, data.atDetection, data.context)
        if (!filter || data.listenerInterceptorChain.intercept().prevent) {
            // 没有通过检测, 返回ListenResult默认的无效化实现。
            return ListenResult
        }
        // // 如果被拦截
        // if (data.listenerInterceptorChain.intercept().isPrevent) {
        //     return EmptyFailedNoBreakResult
        // }

        // val resultBuilder = ListenResultBuilder()
        // 获取实例
        val instance: Any? = runCatching {
            listenerInstanceGetter()
        }.getOrElse {
            return listenerResultFactory.getResult(null, this, it)
        }

        // 获取方法参数
        val params: Array<*> = runCatching {
            methodParamsGetter(data)
        }.getOrElse {
            return listenerResultFactory.getResult(null, this, it)
        }

        // 执行方法
        val invokeResult: Any? = runCatching {
            method(instance, *params)
        }.getOrElse {
            val cause = if (it is InvocationTargetException) {
                it.targetException
            } else it
            return listenerResultFactory.getResult(null, this, cause)
        }

        // set result
        // resultBuilder.result = invokeResult

        // build listen result.
        return listenerResultFactory.getResult(invokeResult, this)
    }
}


/**
 * method 取得唯一ID。直接获取完整路径。
 */
internal fun Method.toListenerId(listens: Listens): String {
    return with(listens.name) {
        if (isBlank()) {
            val methodIn = this@toListenerId.declaringClass
            val methodName = this@toListenerId.name
            val methodParameters = this@toListenerId.parameters
            val methodReturnType = this@toListenerId.returnType

            val wholeName =
                "${methodIn.name} ${methodReturnType.name} $methodName(${methodParameters.joinToString(", ") { "${it.type.name} ${it.name}" }})"

            val wholeNameMD5: String = wholeName.toMD5()

            "${methodIn.typeName}.$methodName#$wholeNameMD5"
        } else this
    }
}

/**
 * method 取得展示name
 */
internal fun Method.toListenerName(listens: Listens): String = with(listens.name) {
    if (isBlank()) this@toListenerName.name else this
}


/**
 * 取MD5。
 */
internal fun String.toMD5(): String {
    return runCatching {
        //获取md5加密对象
        val instance: MessageDigest = MessageDigest.getInstance("MD5")
        //对字符串加密，返回字节数组
        val digest: ByteArray = instance.digest(toByteArray())
        val sb = StringBuffer()
        for (b in digest) {
            //获取低八位有效值
            val i: Int = b.toInt() and 0xff
            //将整数转化为16进制
            val hexString = Integer.toHexString(i)
            if (hexString.length < 2) {
                //如果是一位的话，补0
                sb.append('0')
                // hexString = "0" + hexString
            }
            sb.append(hexString)
        }
        sb.toString()
    }.getOrDefault("")
}


internal data class ListenerParameterTypeMismatchWarn(val log: String, val exLog: String)


/**
 * 监听参数未被监听异常。
 */
@Suppress("unused")
public class ListenerParameterTypeMismatchException : SimbotIllegalArgumentException {
    constructor() : super()
    constructor(s: String?) : super(s)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}


internal fun nullInstanceGetter(): Any? = null




/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     MethodListenerFunction.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.listener

import love.forte.common.ioc.DependBeanFactory
import love.forte.common.ioc.annotation.Depend
import love.forte.common.utils.annotation.AnnotationUtil
import love.forte.common.utils.convert.ConverterManager
import love.forte.simbot.annotation.FilterValue
import love.forte.simbot.annotation.Filters
import love.forte.simbot.annotation.Listens
import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.api.sender.Getter
import love.forte.simbot.api.sender.MsgSender
import love.forte.simbot.api.sender.Sender
import love.forte.simbot.api.sender.Setter
import love.forte.simbot.bot.Bot
import love.forte.simbot.filter.AtDetection
import love.forte.simbot.filter.FilterManager
import love.forte.simbot.filter.ListenerFilter
import love.forte.simbot.filter.NoSuchFilterException
import love.forte.simbot.listener.ListenResult
import love.forte.simbot.listener.ListenerContext
import love.forte.simbot.listener.ListenerFunction
import love.forte.simbot.listener.ListenerFunctionInvokeData
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Type
import java.security.MessageDigest
import kotlin.reflect.jvm.kotlinFunction

/**
 *
 * 以Method实例为执行载体的 [ListenerFunction] 实例。
 *
 * @property method method实例。
 * @property dependBeanFactory 依赖工厂。需要其可以获取除了 [MsgGet] 以外的动态参数，
 * 例如 [love.forte.simbot.core.api.sender.Sender]
 * 或 [love.forte.simbot.core.filter.AtDetection]。
 * @property filterManager 过滤器工厂。
 * @property
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
@Suppress("JoinDeclarationAndAssignment")
public class MethodListenerFunction(
    private val method: Method,
    private val dependBeanFactory: DependBeanFactory,
    private val filterManager: FilterManager,
    private val converterManager: ConverterManager
) : ListenerFunction {

    /**
     * 此监听函数上的 [Listens] 注解。
     */
    private val listensAnnotation: Listens

    /**
     * 此监听函数上的 [Filters] 注解。如果有的话。
     */
    private val filtersAnnotation: Filters?

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
     * 自定义注解列表。
     */
    private val customFilter: List<ListenerFilter>

    /**
     * 此监听函数对应的监听器列表。
     */
    override val filters: List<ListenerFilter>
        get() = (listenAnnotationFilter?.let { mutableListOf(it) } ?: mutableListOf()).apply {
            if (customFilter.isNotEmpty()) {
                addAll(customFilter)
            }
        }


    /**
     * 当前监听函数的载体。例如一个 Class。
     * 一般如果是个method，那么此即为[Class]。
     */
    override val type: Type

    /**
     * 此监听函数的实例获取函数。
     */
    private val listenerInstanceGetter: () -> Any

    /**
     * 此方法的参数获取函数。
     */
    private val methodParamsGetter: (ListenerFunctionInvokeData) -> Array<*>

    private companion object Types {
        private val msgSenderType = MsgSender::class.java
        private val senderType = Sender::class.java
        private val getterType = Getter::class.java
        private val setterType = Setter::class.java
        private val atDetectionType = AtDetection::class.java
        private val listenerContextType = ListenerContext::class.java
        private val botType = Bot::class.java

        private val ListensType = Listens::class.java
        private val FiltersType = Filters::class.java
        private val MsgGetType = MsgGet::class.java
    }

    init {
        // 监听注解
        listensAnnotation = AnnotationUtil.getAnnotation(method, Listens::class.java)
            ?: throw IllegalStateException("cannot found annotation '@Listens' in method $method")

        // 过滤注解
        filtersAnnotation = AnnotationUtil.getAnnotation(method, Filters::class.java)

        id = method.toListenerId(listensAnnotation)
        name = method.toListenerName(listensAnnotation)

        type = method.declaringClass

        listenerInstanceGetter = { dependBeanFactory[type] }

        // 监听类型列表
        listenTypes = listensAnnotation.value.mapTo(mutableSetOf()) { it.value.java }

        if (filtersAnnotation != null) {
            // 注解过滤器对应的监听函数
            listenAnnotationFilter = filtersAnnotation.let {
                filterManager.getFilter(it)
            }

            // 自定义过滤器列表
            customFilter = filtersAnnotation.customFilter.map {
                filterManager.getFilter(it) ?: throw NoSuchFilterException(it)
            }.sortedBy { it.priority }
        } else {
            listenAnnotationFilter = null
            customFilter = emptyList()

        }


        // init method args getter.
        val parameterGetters: List<(ListenerFunctionInvokeData) -> Any?> =
            method.parameters.mapIndexed { i, it ->
                val filterValue: FilterValue? = AnnotationUtil.getAnnotation(it, FilterValue::class.java)
                val dependAnnotation: Depend? = AnnotationUtil.getAnnotation(it, Depend::class.java)
                val orIgnore: Boolean = dependAnnotation?.orIgnore ?: false

                val parameterType = it.type

                if (filterValue == null) {
                    // not filterValue
                    val dependName: String? = dependAnnotation?.value?.ifBlank { null }

                    // when {
                    //     // 参数是送信器、at detection等相关类型
                    //     msgSenderType.isAssignableFrom(parameterType) -> return@mapIndexed { d -> d.msgSender }
                    //     senderType.isAssignableFrom(parameterType) -> return@mapIndexed { d -> d.msgSender.SENDER }
                    //     getterType.isAssignableFrom(parameterType) -> return@mapIndexed { d -> d.msgSender.GETTER }
                    //     setterType.isAssignableFrom(parameterType) -> return@mapIndexed { d -> d.msgSender.SETTER }
                    //     atDetectionType.isAssignableFrom(parameterType) -> return@mapIndexed { d -> d.atDetection }
                    //     listenerContextType.isAssignableFrom(parameterType) -> return@mapIndexed { d -> d.context }
                    //     botType.isAssignableFrom(parameterType) -> return@mapIndexed { d -> d.bot }
                    // }

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
                                dependAnnotation?.type?.takeIf { dt -> dt.java != Void::class.java }?.java
                                    ?: parameterType
                            { d ->
                                // 如果获取到的msgGet的类型正好是此参数的类型的子类，直接使用
                                val msgGet: MsgGet = d.msgGet
                                if (type.isAssignableFrom(msgGet.javaClass)) msgGet
                                else d[type] ?: dependBeanFactory[type]
                            }
                        }
                    }

                } else {
                    val filterValueName = kotlin.runCatching {
                        filterValue.value.ifBlank {
                            method.kotlinFunction?.parameters?.get(i)?.name ?: it.name
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
                            val msg: String = d.msgGet.msg ?: return@f null
                            val findValue: String? = listenAnnotationFilter?.getFilterValue(filterValueName, msg)
                            if (findValue == null) {
                                null
                            } else {
                                converterManager.convert(parameterType, findValue)
                            }
                        }
                    } else {
                        f@{ d ->
                            val msg: String = d.msgGet.msg
                                ?: throw IllegalStateException("Msg ${d.msgGet} unable to get msg.")
                            val findValue = listenAnnotationFilter?.getFilterValue(filterValueName, msg)
                            if (findValue == null) {
                                throw IllegalStateException("Unable to extract filter value $filterValueName in method $method.")
                            } else {
                                converterManager.convert(parameterType, findValue)
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
    override fun <A : Annotation> getAnnotation(type: Class<out A>): A? = when (type) {
        ListensType -> listensAnnotation as A
        FiltersType -> filtersAnnotation as? A
        else -> AnnotationUtil.getAnnotation(method, type)
    }


    /**
     * 执行监听函数并返回一个执行后的响应结果。
     */
    override fun invoke(data: ListenerFunctionInvokeData): ListenResult<*> {
        val resultBuilder = ListenResultBuilder()

        // 获取实例
        val instance: Any = runCatching {
            listenerInstanceGetter()
        }.getOrElse {
            resultBuilder.success = false
            resultBuilder.throwable = it
            return resultBuilder.build()
        }

        // 获取方法参数
        val params: Array<*> = runCatching {
            methodParamsGetter(data)
        }.getOrElse {
            resultBuilder.success = false
            resultBuilder.throwable = it
            return resultBuilder.build()
        }

        // 执行方法
        val invokeResult: Any? = runCatching {
            method(instance, *params).apply {
                resultBuilder.success = this != null
            }
        }.getOrElse {
            // invoke err.
            resultBuilder.throwable = if (it is InvocationTargetException) {
                it.targetException
            } else it
            resultBuilder.success = false
            return resultBuilder.build()
        }

        // set result
        resultBuilder.result = invokeResult

        // build listen result.
        return resultBuilder.build()
    }
}


/**
 * method 取得唯一ID。
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

            "$methodName#$wholeNameMD5"
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












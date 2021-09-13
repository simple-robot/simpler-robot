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

@file:Suppress("unused")

package love.forte.simbot.core.listener

import love.forte.simbot.annotation.Listens
import love.forte.simbot.api.SimbotExperimentalApi
import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.api.sender.MsgSender
import love.forte.simbot.constant.PriorityConstant
import love.forte.simbot.filter.*
import love.forte.simbot.listener.ListenResult
import love.forte.simbot.listener.ListenerFunction
import love.forte.simbot.listener.ListenerFunctionInvokeData
import love.forte.simbot.listener.ListenerGroupManager
import java.lang.reflect.Type
import kotlin.reflect.KClass

/*
    监听函数构造实例
 */





/**
 * 构建一个 [ListenerFunction].
 *
 */
@ListenerFunctionBuilderDSL
public inline fun <T : MsgGet> buildListenerFunction(
    id: String,
    vararg listenTypes: KClass<out T>,
    block: ListenerFunctionBuilder<T>.() -> Unit,
): ListenerFunction = buildListenerFunction(id = id, name = id, *listenTypes, block = block)


/**
 * 构建一个 [ListenerFunction].
 *
 */
@ListenerFunctionBuilderDSL
public inline fun <T : MsgGet> buildListenerFunction(
    id: String,
    name: String,
    vararg listenTypes: KClass<out T>, block: ListenerFunctionBuilder<T>.() -> Unit,
): ListenerFunction = ListenerFunctionBuilder(id, name, *listenTypes.map { kc -> kc.java }.toTypedArray()).apply(block).build()

/**
 * 构建一个 [ListenerFunction].
 *
 */
@ListenerFunctionBuilderDSL
public fun <T : MsgGet> listenerFunction(
    id: String,
    vararg listenTypes: KClass<out T>,
    invoker: suspend (data: ListenerFunctionInvokeData) -> ListenResult<*>,
): ListenerFunction = buildListenerFunction(id = id, name = id, *listenTypes) {
    invoker(invoker)
}


/**
 * 构建一个 [ListenerFunction].
 *
 */
@ListenerFunctionBuilderDSL
public fun <T : MsgGet> listenerFunction(
    id: String,
    name: String,
    vararg listenTypes: KClass<out T>,
    invoker: suspend (data: ListenerFunctionInvokeData) -> ListenResult<*>,
): ListenerFunction = buildListenerFunction(id = id, name = name, *listenTypes) {
    invoker(invoker)
}

/**
 * 构建一个 [ListenerFunction].
 *
 */
@ListenerFunctionBuilderDSL
public fun <T : MsgGet> listenerFunction(
    id: String,
    vararg listenTypes: KClass<out T>,
    invoker: suspend (msg: T, sender: MsgSender, atDetection: AtDetection) -> ListenResult<*>,
): ListenerFunction = buildListenerFunction(id = id, name = id, *listenTypes) {
    invoker(invoker)
}


/**
 * 构建一个 [ListenerFunction].
 *
 */
@ListenerFunctionBuilderDSL
public fun <T : MsgGet> listenerFunction(
    id: String,
    name: String,
    vararg listenTypes: KClass<out T>,
    invoker: suspend (msg: T, sender: MsgSender, atDetection: AtDetection) -> ListenResult<*>,
): ListenerFunction = buildListenerFunction(id = id, name = name, *listenTypes) {
    invoker(invoker)
}


@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
@DslMarker
annotation class ListenerFunctionBuilderDSL


/**
 * 构建一个 [ListenerFunction] 实例.
 *
 *
 */
public class ListenerFunctionBuilder<T : MsgGet> @JvmOverloads constructor(
    val id: String,
    val name: String = id,
    listenTypes: Collection<Class<out T>>,
) {

    init {
        check(listenTypes.isNotEmpty()) { "Listen types cannot be empty." }
    }

    @JvmOverloads
    constructor(id: String,
                name: String = id,
                vararg listenTypes: Class<out T>): this(id, name, listenTypes.toSet())
    @JvmOverloads
    constructor(id: String,
                name: String = id,
                listenType: Class<out T>): this(id, name, setOf(listenType))


    private val listenTypesSet = listenTypes.toSet()

    private lateinit var invokeFunction: suspend (data: ListenerFunctionInvokeData) -> ListenResult<*>

    /////////// Common   //////////////

    /**
     * 是否为 [备用函数][ListenerFunction.spare]
     */
    @ListenerFunctionBuilderDSL
    var isSpare = false

    /**
     * 是否为 [异步函数][love.forte.simbot.annotation.Async]
     */
    @ListenerFunctionBuilderDSL
    var isAsync = false

    /**
     * 监听函数的优先级。
     */
    @ListenerFunctionBuilderDSL
    var priority = Listens.DEFAULT_PRIORITY

    /**
     * 所属载体。
     */
    @ListenerFunctionBuilderDSL
    var type: Type = Any::class.java


    @OptIn(SimbotExperimentalApi::class)
    private var groups: List<String> = emptyList()


    @OptIn(SimbotExperimentalApi::class)
    private var groupManager: ListenerGroupManager? = null

    /**
     * 为当前监听函数分配监听分组。
     * 进行分组需要提供 [ListenerGroupManager] 实例。
     *
     */
    @OptIn(SimbotExperimentalApi::class)
    fun listenerGroup(groupManager: ListenerGroupManager, vararg groups: String): ListenerFunctionBuilder<T> {
        this.groupManager = groupManager
        this.groups = groups.toList()
        return this
    }


    /**
     * 注解获取器。默认无法获取任何注解。
     *
     */
    private var annotationGetter: (type: Class<out Annotation>) -> Annotation? = { null }


    /**
     * 设置一个注解获取器。
     */
    @ListenerFunctionBuilderDSL
    fun annotationGetter(block: (type: Class<out Annotation>) -> Annotation?): ListenerFunctionBuilder<T> {
        this.annotationGetter = block
        return this
    }

    /**
     * 提供可以获取的注解列表。
     *
     */
    @ListenerFunctionBuilderDSL
    fun annotations(vararg annotations: Annotation): ListenerFunctionBuilder<T> {
        when {
            annotations.isEmpty() -> {
                annotationGetter = { null }
                return this
            }

            annotations.size == 1 -> {
                val single = annotations[0]
                val type = single.annotationClass.java
                return annotationGetter {
                    if (it == type) single else null
                }
            }

            else -> {
                val map = mutableMapOf<Class<out Annotation>, Annotation>()
                for (annotation in annotations) {
                    map[annotation.annotationClass.java] = annotation
                }
                return annotationGetter { map[it] }
            }

        }
    }

    /**
     * 此监听函数的过滤器。
     */
    private var filter: ListenerFilter? = null

    @JvmOverloads
    @ListenerFunctionBuilderDSL
    fun filter(priority: Int = PriorityConstant.LAST, block: (data: FilterData) -> Boolean): ListenerFunctionBuilder<T> {
        this.filter = buildListenerFilter {
            this.priority = priority
            filter(block)
        }
        return this
    }


    ///////////  For kt  //////////////

    @JvmSynthetic
    @ListenerFunctionBuilderDSL
    fun buildFilter(filter: ListenerFilterBuilder.() -> Unit): ListenerFunctionBuilder<T> {
        this.filter = buildListenerFilter(filter)
        return this
    }


    /**
     * 监听函数实际的执行函数. 提供完整的事件参数。
     *
     */
    @ListenerFunctionBuilderDSL
    @JvmSynthetic
    fun invoker(block: suspend (data: ListenerFunctionInvokeData) -> ListenResult<*>): ListenerFunctionBuilder<T> {
        invokeFunction = block
        return this
    }

    /**
     * 监听函数实际的执行函数, 仅提供 [当前事件][T] 与 [送信器][MsgSender] 实例.
     *
     */
    @ListenerFunctionBuilderDSL
    @Suppress("UNCHECKED_CAST")
    @JvmSynthetic
    fun invoker(block: suspend (msg: T, sender: MsgSender, atDetection: AtDetection) -> ListenResult<*>): ListenerFunctionBuilder<T> {
        invokeFunction = { data ->
            val msg = data.msgGet as T
            val sender = data.msgSender
            val atDetection = data.atDetection
            block(msg, sender, atDetection)
        }
        return this
    }


    ///////////  For Java  //////////////

    @Suppress("FunctionName")
    @ListenerFunctionBuilderDSL
    @JvmName("listenerFilter")
    fun _listenerFilter(filter: BuildListenerFilter): ListenerFunctionBuilder<T> {
        this.filter = buildListenerFilter(filter)
        return this
    }

    /**
     * 监听函数实际的执行函数. 提供完整的事件参数。
     */
    @Suppress("FunctionName")
    @JvmName("listenerFunction")
    fun _listenerFunction4j(block: (data: ListenerFunctionInvokeData) -> ListenResult<*>): ListenerFunctionBuilder<T> {
        return invoker { d -> block(d) }
    }

    /**
     * 监听函数实际的执行函数, 仅提供 [当前事件][T] 与 [送信器][MsgSender] 实例.
     */
    @Suppress("FunctionName")
    @JvmName("listenerFunction")
    fun _listenerFunction4j(block: (msg: T, sender: MsgSender, atDetection: AtDetection) -> ListenResult<*>): ListenerFunctionBuilder<T> {
        return invoker { msg, sender, atDetection -> block(msg, sender, atDetection) }
    }


    @OptIn(SimbotExperimentalApi::class)
    fun build(): ListenerFunction = FunctionListenerFunction(
        id = id,
        name = name,
        spare = isSpare,
        priority = priority,
        isAsync = isAsync,
        type = type,
        listenTypes = listenTypesSet,
        groupNames = groups,
        groupManager = groupManager,
        annotationGetter = annotationGetter,
        func = invokeFunction,
        filter = filter
    )
}


public fun interface BuildListenerFilter : (ListenerFilterBuilder) -> Unit {
    override operator fun invoke(builder: ListenerFilterBuilder)
}




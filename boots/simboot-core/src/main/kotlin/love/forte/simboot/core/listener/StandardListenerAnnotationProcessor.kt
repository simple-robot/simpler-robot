/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *
 */

package love.forte.simboot.core.listener

import love.forte.annotationtool.core.KAnnotationTool
import love.forte.annotationtool.core.nonConverters
import love.forte.di.annotation.Beans
import love.forte.simboot.annotation.Binder
import love.forte.simboot.annotation.Filter
import love.forte.simboot.annotation.Filters
import love.forte.simboot.annotation.toData
import love.forte.simboot.core.filter.BootFiltersAnnotationProcessor
import love.forte.simboot.filter.EventFilterRegistrar
import love.forte.simboot.filter.FiltersAnnotationProcessor
import love.forte.simboot.filter.FiltersData
import love.forte.simboot.filter.filtersAnnotationProcessContext
import love.forte.simboot.interceptor.AnnotatedEventListenerInterceptor
import love.forte.simboot.listener.*
import love.forte.simbot.*
import love.forte.simbot.core.event.plus
import love.forte.simbot.event.*
import org.slf4j.Logger
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Named
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KVisibility
import kotlin.reflect.full.*
import kotlin.reflect.jvm.jvmName
import kotlin.reflect.jvm.kotlinFunction
import love.forte.simboot.annotation.Interceptor as InterceptorAnnotation

/**
 *
 * [ListenerAnnotationProcessor] 基础实现类, 用于通过 [ListenerData] 解析并注册 [love.forte.simbot.event.EventListener].
 *
 * [StandardListenerAnnotationProcessor] 只会处理 非抽象类下的 **公共** 函数。
 *
 * @author ForteScarlet
 */
@Beans
public class StandardListenerAnnotationProcessor : ListenerAnnotationProcessor {
    private val tool = KAnnotationTool()
    private val logger = LoggerFactory.getLogger(StandardListenerAnnotationProcessor::class)
    private val instanceCache = ConcurrentHashMap<KClass<*>, Any>()


    override fun process(context: ListenerAnnotationProcessorContext): Boolean {
        val function = context.function
        val from = context.from
        if (from != null && (from.isAbstract || function.visibility != KVisibility.PUBLIC)) {
            return true
        }

        val listenerLogger = context.from?.let { f -> LoggerFactory.getLogger(f) }
            ?: LoggerFactory.getLogger(function.toString())


        val id = function.resolveId(context)
        val targets = function.resolveTargets(context)

        // if (targets.isEmpty()) {
        //     throw SimbotIllegalStateException("Listener(id=$id) process target missing. Maybe you need to provide some @Listen(...) or a parameter type of [Event].")
        // }

        // all binders.
        val binders: List<ParameterBinder> = function.resolveBinders(context)

        val listenerAttributeMap = AttributeMutableMap(ConcurrentHashMap())

        val listener = AnnotationFunctionalEventListener(
            id = id,
            isAsync = context.listenerData.async,
            targets = targets, // If empty, listen all event.
            caller = function,
            logger = listenerLogger,
            binders = binders.toTypedArray(),
            attributeMap = listenerAttributeMap
        )

        logger.info("Resolve listener: id={}, targets={}", listener.id, targets.map { t -> t.id }.ifEmpty { "<ALL>" })

        // filters
        val filters = function.resolveFilters(listener, listenerAttributeMap, context)
        logger.debug("Size of resolved listener filters: {}", filters.size)
        logger.debug("Resolved listener filters: {}", filters)

        // 所有拦截器
        val interceptors = function.resolveInterceptors(context)
        logger.debug("Size of resolved listener interceptors: {}", interceptors.size)
        logger.debug("Resolved listener interceptors: {}", interceptors)

        // 合并
        val finalListener = listener + filters + interceptors


        // 注册listener
        context.listenerRegistrar.register(finalListener)

        return true
    }

    /**
     * 解析获取所有的专属拦截器。
     */
    private fun KFunction<*>.resolveInterceptors(context: ListenerAnnotationProcessorContext): List<EventListenerInterceptor> {
        val annotations = tool.getAnnotations(this, InterceptorAnnotation::class)
        if (annotations.isEmpty()) return emptyList()

        // map to interceptors
        return annotations.map { it.toInterceptor(context) }.sortedBy { it.priority }
    }

    private fun InterceptorAnnotation.toInterceptor(context: ListenerAnnotationProcessorContext): EventListenerInterceptor {
        return when {
            value.isNotEmpty() -> {
                context.beanContainer[value, AnnotatedEventListenerInterceptor::class]
            }
            type != AnnotatedEventListenerInterceptor::class -> {
                val obj = type.objectInstance
                if (obj != null) return obj

                val beanContainer = context.beanContainer
                val allNamed = beanContainer.getAll(type)
                if (allNamed.isNotEmpty()) {
                    beanContainer[type]
                } else {
                    // try instance.
                    kotlin.runCatching {
                        instanceCache.computeIfAbsent(type) { type.createInstance() } as AnnotatedEventListenerInterceptor
                    }.getOrElse {
                        throw SimbotIllegalStateException(
                            "Cannot get an interceptor instance of type [$type]: does not exist in the bean container and cannot be instantiated directly: ${it.localizedMessage}",
                            it
                        )
                    }
                }
            }
            else ->
                throw SimbotIllegalStateException("@Interceptor needs to specify [value] or [type], and [value] cannot be empty or type cannot be equal to [AnnotatedEventListenerInterceptor.class] type self. But now the value is empty and the type is [AnnotatedEventListenerInterceptor.class].")
        }
    }

    /**
     * 解析获取注所有的解过滤器。
     */
    private fun KFunction<*>.resolveFilters(
        listener: EventListener,
        listenerAttributeMap: MutableAttributeMap,
        context: ListenerAnnotationProcessorContext
    ): List<EventFilter> {
        val filters = tool.getAnnotation(this, Filters::class)
        val filterList = tool.getAnnotations(this, Filter::class)

        if (filters == null && filterList.isEmpty()) return emptyList()

        val filterDataList = filterList.map { it.toData(source = this) }
        val filtersData =
            filters?.toData(source = this, filterDataList) ?: FiltersData(source = this, value = filterDataList)

        val filterRegistrar = ListFilterRegistrar(mutableListOf())

        val filterProcessContext = filtersAnnotationProcessContext(
            filtersData,
            listener,
            listenerAttributeMap,
            filterRegistrar,
            context.beanContainer
        )
        val filtersDataProcessorType = filtersData.processor

        val filterProcessor: FiltersAnnotationProcessor =
            if (filtersDataProcessorType == FiltersAnnotationProcessor::class) {
                BootFiltersAnnotationProcessor
            } else {
                // is object?
                filtersDataProcessorType.objectInstance
                    ?: kotlin.run {
                        kotlin.runCatching { context.beanContainer.getOrNull(filtersDataProcessorType) }.getOrElse {
                            throw SimbotIllegalStateException(
                                "Unable to get filter processor's instance by bean container: ${it.localizedMessage}",
                                it
                            )
                        }
                            ?: kotlin.runCatching {
                                instanceCache.computeIfAbsent(filtersDataProcessorType) { filtersDataProcessorType.createInstance() } as FiltersAnnotationProcessor
                            }.getOrElse { e ->
                                throw SimbotIllegalStateException(
                                    "Filter processor cannot be instantiated directly: ${e.localizedMessage}",
                                    e
                                )
                            }

                    }
            }


        // process
        filterProcessor.process(filterProcessContext)

        return filterRegistrar.list.apply { sortBy { it.priority } }
    }

    /**
     * 解析得到监听事件类型。
     */
    private fun KFunction<*>.resolveTargets(context: ListenerAnnotationProcessorContext): Set<Event.Key<*>> {
        val listens = context.listenerData.listens
        if (listens != null) {
            return listens.value.map { data ->
                data.value.getEventKeyOr { v ->
                    throw SimbotIllegalStateException("Event type [$v] cannot be listen: there is no companion object of type Event.Key<T>")
                }
            }.toSet()
        } else {
            // find all parameters.
            val receiver = extensionReceiverParameter
            if (receiver != null) {
                val classifier = receiver.type.classifier
                if ((classifier as? KClass<*>)?.isSubclassOf(Event::class) == true) {
                    @Suppress("UNCHECKED_CAST")
                    return setOf(
                        (classifier as KClass<out Event>).getEventKeyOr { v ->
                            throw SimbotIllegalStateException("Listener function's event receiver type [$v] cannot be listen: there is no companion object of type Event.Key<T>")
                        }
                    )
                }
            }

            // no receiver or not event type
            return valueParameters.mapNotNull { parameter ->
                val classifier = parameter.type.classifier
                if ((classifier as? KClass<*>)?.isSubclassOf(Event::class) == true) {
                    @Suppress("UNCHECKED_CAST")
                    (classifier as KClass<out Event>).getEventKeyOrElse { v ->
                        logger.warn(
                            "Listener function's event parameter type [{}] cannot be listen: there is no companion object of type Event.Key<T>. it may always be null.",
                            v
                        )
                        null
                    }
                } else null
            }.toSet()


        }


    }

    private inline fun KClass<out Event>.getEventKeyOr(or: (KClass<out Event>) -> Nothing): Event.Key<*> {
        return try {
            getKey()
        } catch (noDefine: NoSuchEventKeyDefineException) {
            // only cache NoSuchEventKeyDefineException.
            or(this)
        }
        // return companionObjectInstance?.takeIf { it is Event.Key<*> } as? Event.Key<*> ?: or(this)

    }

    private inline fun KClass<out Event>.getEventKeyOrElse(or: (KClass<*>) -> Event.Key<*>?): Event.Key<*>? {
        return try {
            getKey()
        } catch (noDefine: NoSuchEventKeyDefineException) {
            // only cache NoSuchEventKeyDefineException.
            or(this)
        }
        // return companionObjectInstance?.takeIf { it is Event.Key<*> } as? Event.Key<*> ?: or(this)
    }

    /**
     * 解析得到ID。
     */
    private fun KFunction<*>.resolveId(context: ListenerAnnotationProcessorContext): ID {

        // 解析参数
        val fromInstance = instanceParameter?.type?.classifier as? KClass<*>

        // id in data
        val dataId = context.listenerData.id.takeIf { it.isNotEmpty() }?.ID

        val id = dataId ?: fromInstance?.let { f ->
            "${f.qualifiedName ?: f.jvmName}.$name".ID
        } ?: name.ID

        return id

    }

    /**
     * 通过指定监听函数解析得到所有binder factory
     */
    private fun KFunction<*>.resolveBinders(context: ListenerAnnotationProcessorContext): List<ParameterBinder> {
        val binders = mutableSetOf<ParameterBinderFactory>()
        val binderFactoryContainer = context.binderFactoryContainer
        val fromId = context.from?.let { f -> tool.getAnnotation(f, Named::class) }?.value

        binders.addAll(binderFactoryContainer.getGlobals())

        // current binder with id
        val currentBinder = tool.getAnnotation(this, Binder::class)
        if (currentBinder != null) {
            if (currentBinder.scope != Binder.Scope.SPECIFY) {
                throw SimbotIllegalStateException("Listener function [$this] annotate @Binder, But the scope is not Scope.SPECIFY.")
            }

            currentBinder.id.ifEmpty {
                throw SimbotIllegalStateException("Listener function [$this] annotate @Binder, But no id is specified.")
            }.forEach { i ->
                val got = binderFactoryContainer[i]
                    ?: throw SimbotIllegalStateException("Cannot found binder by id $i annotate on listener function [$this]")
                binders.add(got)
            }
        }

        // CURRENT scope binder.
        // find from
        val from = context.from
        var err: Throwable? = null
        val memberFunctions: Set<KFunction<*>> = kotlin.runCatching {
            from?.memberFunctions?.plus(from.memberExtensionFunctions) ?: emptyList()
        }.getOrElse { e1 ->
            err = e1
            kotlin.runCatching {
                from!!.java.methods.mapNotNull { m -> m.kotlinFunction }.also {
                    err = null
                }
            }.getOrElse { e2 ->
                err!!.addSuppressed(e2)
                emptyList()
            }
        }.filter { f ->
            val binder = tool.getAnnotation(f, Binder::class)
            binder?.scope == Binder.Scope.CURRENT
        }.toSet()

        if (err != null) {
            logger.error("Cannot resolve member functions of function come from class $from.", err)
        }

        if (memberFunctions.isNotEmpty()) {
            // to binder
            val currentScopeBinders =
                memberFunctions.map { f -> binderFactoryContainer.resolveFunctionToBinderFactory(fromId, f) }
            binders.addAll(currentScopeBinders)
        }


        return resolveBinderFromFactory(context, binders.sortedBy { it.priority })
    }


    /**
     * 通过 factory 集合，将binder factory转化为指定binder。
     */
    private fun KFunction<*>.resolveBinderFromFactory(
        context: ListenerAnnotationProcessorContext,
        factories: List<ParameterBinderFactory>
    ): List<ParameterBinder> {
        val binders = parameters.map { parameter ->
            val bindContext = ParameterBinderFactoryContextImpl(
                context,
                this,
                parameter,
            )
            val bindList = mutableListOf<ParameterBinderResult.NotEmpty>()
            val bindSpareList = mutableListOf<ParameterBinderResult.NotEmpty>()
            for (factory in factories) {
                when (val result = factory.resolveToBinder(bindContext)) {
                    is ParameterBinderResult.Empty -> continue
                    is ParameterBinderResult.NotEmpty -> {
                        // not empty.
                        when (result) {
                            is ParameterBinderResult.Normal -> {
                                if (bindList.isEmpty() || (bindList.first() !is ParameterBinderResult.Only)) {
                                    bindList.add(result)
                                }
                            }
                            is ParameterBinderResult.Only -> {
                                if (bindList.isNotEmpty() && bindList.first() is ParameterBinderResult.Only) {
                                    // 上一个也是Only.
                                    bindList[0] = result
                                } else {
                                    bindList.clear() // clear all
                                    bindList.add(result)
                                }
                            }
                            is ParameterBinderResult.Spare -> {
                                bindSpareList.add(result)
                            }
                        }
                    }
                }
            }

            bindList.sortBy { it.priority }
            bindSpareList.sortBy { it.priority }
            when {
                bindList.isEmpty() && bindSpareList.isEmpty() -> {
                    // no binder.
                    EmptyBinder(parameter)
                }
                bindList.isEmpty() -> {
                    // spare as normal.
                    MergedBinder(bindSpareList.map { it.binder }, emptyList())
                }
                else -> {
                    MergedBinder(
                        bindList.map { it.binder },
                        bindSpareList.map { it.binder }.ifEmpty { emptyList() },
                    )
                }
            }
        }

        return binders
    }


}


private class ListFilterRegistrar(val list: MutableList<EventFilter>) : EventFilterRegistrar {
    override fun register(filter: EventFilter) {
        list.add(filter)
    }
}


private class ParameterBinderFactoryContextImpl(
    override val annotationProcessContext: ListenerAnnotationProcessorContext,
    override val source: KFunction<*>,
    override val parameter: KParameter
) : ParameterBinderFactory.Context


/**
 * 组合多个binder的 [ParameterBinder].
 *
 */
private class MergedBinder(
    private val binders: List<ParameterBinder>, // not be empty
    private val spare: List<ParameterBinder> // empty able
) : ParameterBinder {
    init {
        if (binders.isEmpty()) throw IllegalArgumentException("Binders cannot be empty.")
    }


    override suspend fun arg(context: EventListenerProcessingContext): Result<Any?> {
        var err: Throwable? = null

        suspend fun ParameterBinder.invoke(): Result<Any?>? {
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

            Result.failure<Any?>(BindException("Nothing binder success.", err))
        }.getOrElse { binderInvokeException ->
            err?.also {
                binderInvokeException.addSuppressed(it)
            }
            Result.failure(BindException("Binder invoke failure", binderInvokeException))
        }
    }
}

/**
 * 将会直接抛出错误的binder。
 */
private class EmptyBinder(
    private val parameter: KParameter
) : ParameterBinder {
    private val resultProvider: () -> Result<Any?> = if (parameter.type.isMarkedNullable) {
        val nullResult: Result<Any?> = Result.success(null)
        ({ nullResult })
    } else {
        {
            Result.failure(BindException("Parameter(#${parameter.index}) [$parameter] has no binder."))
        }
    }

    override suspend fun arg(context: EventListenerProcessingContext): Result<Any?> {
        return resultProvider()
    }
}


private class AnnotationFunctionalEventListener<R>(
    override val id: ID,
    override val isAsync: Boolean,
    private val targets: Set<Event.Key<*>>,
    override val caller: KFunction<R>,
    override val logger: Logger,
    override val binders: Array<ParameterBinder>,
    private val attributeMap: AttributeMutableMap
) : FunctionalBindableEventListener<R>() {

    private lateinit var targetCaches: MutableSet<Event.Key<*>> //= mutableSetOf<>()
    private lateinit var notTargetCaches: MutableSet<Event.Key<*>> //= mutableSetOf<Event.Key<*>>()

    init {
        // not empty, init it.
        if (targets.isNotEmpty()) {
            targetCaches = mutableSetOf()
            notTargetCaches = mutableSetOf()
        }
    }

    override fun isTarget(eventType: Event.Key<*>): Boolean {
        // 如果为空，视为监听全部
        if (targets.isEmpty()) return true

        if (eventType in notTargetCaches) return false
        if (eventType in targetCaches) return true
        if (eventType in targets) return true

        synchronized(targetCaches) {
            if (eventType in targetCaches) return true

            for (target in targets) {
                if (eventType.isSubFrom(target)) {
                    targetCaches.add(eventType)
                    return true
                }
            }
            notTargetCaches.add(eventType)
            return false
        }
    }

    override fun convertValue(value: Any?, parameter: KParameter): Any? {
        if (value == null) return null
        return nonConverters().convert(
            instance = value,
            to = parameter.type.classifier as KClass<*>
        )
    }


    override fun <T : Any> getAttribute(attribute: Attribute<T>): T? = attributeMap[attribute]
}



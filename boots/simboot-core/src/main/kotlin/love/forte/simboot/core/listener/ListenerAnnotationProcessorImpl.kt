/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simboot.core.listener

import love.forte.annotationtool.core.KAnnotationTool
import love.forte.annotationtool.core.nonConverters
import love.forte.di.BeanContainer
import love.forte.di.BeansException
import love.forte.simboot.annotation.Binder
import love.forte.simboot.annotation.Filter
import love.forte.simboot.annotation.Filters
import love.forte.simboot.annotation.toData
import love.forte.simboot.core.filter.BootFiltersAnnotationProcessor
import love.forte.simboot.filter.EventFilterRegistrar
import love.forte.simboot.filter.FiltersAnnotationProcessor
import love.forte.simboot.filter.FiltersData
import love.forte.simboot.filter.filtersAnnotationProcessContext
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
import kotlin.reflect.full.*
import kotlin.reflect.jvm.jvmName
import kotlin.reflect.jvm.kotlinFunction

/**
 *
 * [ListenerAnnotationProcessor] 基础实现类, 用于通过 [ListenerData] 解析并注册 [love.forte.simbot.event.EventListener].
 *
 * @author ForteScarlet
 */
internal class ListenerAnnotationProcessorImpl : ListenerAnnotationProcessor {
    private val tool = KAnnotationTool()
    private val logger = LoggerFactory.getLogger(ListenerAnnotationProcessorImpl::class)

    override fun process(context: ListenerAnnotationProcessorContext): Boolean {
        val listenerLogger = LoggerFactory.getLogger(context.from)
        val function = context.function


        val id = function.resolveId(context)
        val targets = function.resolveTargets(context)

        // all binders.
        val binders: List<ParameterBinder> = function.resolveBinders(context)

        val listenerAttributeMap = AttributeMutableMap(ConcurrentHashMap())

        val listener = AnnotationFunctionalEventListener(
            id = id,
            isAsync = context.listenerData.async,
            targets = targets,
            caller = function,
            logger = listenerLogger,
            binders = binders.toTypedArray(),
            attributeMap = listenerAttributeMap
        )
        // filters
        val filters = function.resolveFilters(listener, listenerAttributeMap, context)

        // 合并filter
        val filterMergedListener = listener + filters

        // 注册listener
        context.listenerRegistrar.register(filterMergedListener)

        return true
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

        val filterDataList = filterList.map { it.toData() }
        val filtersData = filters?.toData(filterDataList) ?: FiltersData(value = filterDataList)

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
                        // get from container?
                        try {
                            context.beanContainer[filtersDataProcessorType]
                        } catch (beansException: BeansException) {
                            kotlin.runCatching {
                                filtersDataProcessorType.createInstance()
                            }.getOrElse { e ->
                                e.addSuppressed(beansException)
                                val failure = SimbotIllegalStateException("Unable to get filter processor's instance.")
                                failure.addSuppressed(e)
                                throw failure
                            }
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
        val fromId = tool.getAnnotation(context.from, Named::class)?.value

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
            from.memberFunctions + from.memberExtensionFunctions
        }.getOrElse { e1 ->
            err = e1
            kotlin.runCatching {
                from.java.methods.mapNotNull { m -> m.kotlinFunction }.also {
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
            val currentScopeBinders = memberFunctions.map { f -> binderFactoryContainer.resolveFunctionToBinderFactory(fromId, f) }
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
            val bindContext = ParameterBinderFactoryContextImpl(this, parameter, context.beanContainer)
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
    override val source: KFunction<*>,
    override val parameter: KParameter,
    override val beanContainer: BeanContainer
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
    override suspend fun arg(context: EventListenerProcessingContext): Result<Any?> {
        return Result.failure(BindException("Parameter $parameter has no binder."))
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

    private val targetCaches = mutableSetOf<Event.Key<*>>()
    private val notTargetCaches = mutableSetOf<Event.Key<*>>()

    override fun isTarget(eventType: Event.Key<*>): Boolean {
        if (eventType in notTargetCaches) return false
        if (eventType in targets) return true
        if (eventType in targetCaches) return true
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
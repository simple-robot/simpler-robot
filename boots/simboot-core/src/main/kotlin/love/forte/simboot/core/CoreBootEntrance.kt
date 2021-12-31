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

package love.forte.simboot.core

import kotlinx.coroutines.CompletionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import love.forte.annotationtool.core.KAnnotationTool
import love.forte.di.BeanContainer
import love.forte.di.all
import love.forte.di.allInstance
import love.forte.simboot.*
import love.forte.simboot.annotation.*
import love.forte.simboot.core.filter.KeywordBinderFactory
import love.forte.simboot.core.internal.CoreBootEntranceContextImpl
import love.forte.simboot.core.internal.ResourcesScanner
import love.forte.simboot.core.internal.visitJarEntry
import love.forte.simboot.core.internal.visitPath
import love.forte.simboot.core.listener.AutoInjectBinderFactory
import love.forte.simboot.core.listener.EventParameterBinderFactory
import love.forte.simboot.core.listener.InstanceInjectBinderFactory
import love.forte.simboot.core.listener.toBinderFactory
import love.forte.simboot.factory.BeanContainerFactory
import love.forte.simboot.factory.BotRegistrarFactory
import love.forte.simboot.factory.ConfigurationFactory
import love.forte.simboot.listener.*
import love.forte.simbot.*
import love.forte.simbot.event.EventListener
import love.forte.simbot.event.EventListenerManager
import love.forte.simbot.event.EventListenerRegistrar
import love.forte.simbot.utils.asCycleIterator
import org.slf4j.Logger
import kotlin.concurrent.thread
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KVisibility
import kotlin.reflect.full.instanceParameter
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberExtensionFunctions
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.jvm.kotlinFunction
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue


public interface CoreBootEntranceContext {


    /**
     * [Configuration] 工厂.
     */
    public fun getConfigurationFactory(): ConfigurationFactory

    /**
     * [BeanContainer] 工厂.
     */
    public fun getBeanContainerFactory(): BeanContainerFactory


    /**
     * 读取所有的bot配置文件信息。
     */
    public fun getAllBotInfos(
        configuration: Configuration, beanContainer: BeanContainer
    ): List<BotVerifyInfo>


    /**
     * 通过 [BeanContainer] 最终得到一个 [EventListenerManager].
     */
    public fun getListenerManager(beanContainer: BeanContainer): EventListenerManager


    /**
     * 尝试扫描顶层函数的列表
     */
    public val topFunctionScanPackages: Set<String>


    /**
     * 启动命令参数。
     */
    public val args: Array<String>

    /**
     * 由boot所提供的日志。
     */
    public val logger: Logger
}


/**
 *
 * 由 `boot-core` 提供的基础boot入口。
 *
 * [CoreBootEntrance] 提供Java包路径扫描，并解析加载一切内容，提供依赖注入功能.
 *
 * @author ForteScarlet
 */
public class CoreBootEntrance : SimbootEntrance {
    public companion object {
        internal val annotationTool: KAnnotationTool = KAnnotationTool()
    }

    @OptIn(ExperimentalTime::class)
    override fun run(context: SimbootEntranceContext): SimbootContext {
        // banner

        val contextTimedValue = measureTimedValue {
            run0(context)
        }

        val time = contextTimedValue.duration
        context.logger.info("Simboot core entrance start finished. duration time {}", time.toString())

        return contextTimedValue.value
    }


    private fun run0(context: SimbootEntranceContext): SimbootContext {
        val bootContext: CoreBootEntranceContext = context.toCoreBootEntranceContext()
        val logger = bootContext.logger
        // 获取所有配置
        val configuration = bootContext.getConfigurationFactory()(context)

        logger.info("Resolving bean container")
        // 初始化 bean container
        val beanContainer = bootContext.getBeanContainerFactory()(configuration)
        logger.debug("Using bean container: {} ({})", beanContainer, beanContainer.javaClass)

        // 初始化 listener manager -> listener manager factory
        logger.info("Resolving listener manager")
        val listenerManager = bootContext.getListenerManager(beanContainer)
        logger.debug("Using listener manager: {} ({})", listenerManager, listenerManager.javaClass)

        // 获取所有的 BotRegistrar -> BotRegistrarFactory
        logger.info("Resolving all bot registrar factories")
        val allBotRegistrarFactories = beanContainer.allInstance<BotRegistrarFactory>()
        logger.info("Size of all bot registrar factories: {}", allBotRegistrarFactories.size)
        if (logger.isDebugEnabled) {
            allBotRegistrarFactories.forEach { f ->
                logger.debug("Bot registrar factory: {} ({})", f, f.javaClass)
            }
        }

        // all registrars and group by component name.
        val allBotRegistrars = allBotRegistrarFactories.map {
            it(listenerManager).also {
                logger.debug("Bot registrar: {} ({})", it, it.javaClass)
            }
        }.groupBy { r -> r.component.id.toString() }.mapValues { (key, values) ->
            if (values.size != 1) {
                logger.warn(
                    "There are multiple registrars under the component [{}], and they will be registered sequentially in a balanced manner.",
                    key
                )
                val component = values[0].component
                BalancedBotRegistrar(component, values.toList())
            } else values[0]
        }.values


        // 所有的base binder factory
        logger.info("Resolving all base binder factories")
        val baseBinderFactories = mutableSetOf(
            KeywordBinderFactory, EventParameterBinderFactory, // event binder
            AutoInjectBinderFactory, InstanceInjectBinderFactory
        )
        logger.info("Size of all base binder factories: {}", baseBinderFactories.size)
        if (logger.isDebugEnabled) {
            baseBinderFactories.forEach { f ->
                logger.debug("Base binder factory: {} ({})", f, f.javaClass)
            }
        }

        val binderManager = BinderManager(
            mutableMapOf(), baseBinderFactories
        )

        logger.info("Resolving all binders.")
        bootContext.allBinders(binderManager, beanContainer, annotationTool)
        logger.info("Size of normal binder: {}", binderManager.normalSize)
        logger.info("Size of global binder: {}", binderManager.globalSize)
        if (logger.isDebugEnabled) {
            binderManager.getGlobals().forEach { b ->
                logger.debug("Global binder: {} ({})", b, b.javaClass)
            }
        }

        // 所有的type，尝试解析为listener
        logger.info("Resolve all listeners.")
        val listeners = bootContext.findAllListener(
            beanContainer, binderManager
        )
        logger.info("Size of all listener: {}", listeners.size)

        listeners.forEach(listenerManager::register)

        logger.info("Resolving all bot info.")
        val botInfoList = bootContext.getAllBotInfos(configuration, beanContainer)
        logger.info("Size of all bot info: {}", botInfoList.size)
        if (logger.isDebugEnabled) {
            botInfoList.forEach { b ->
                logger.debug("Bot info: {}", b.infoName)
            }
        }

        logger.info("Register all bots.")
        // all init bots
        val allBots = botInfoList.flatMap { b ->
            val registrars = allBotRegistrars.mapNotNull { botRegistrar ->
                try {
                    botRegistrar.register(b).also {
                        logger.debug(
                            "Bot [{}] registered by registrar of component {}", b.infoName, botRegistrar.component.name
                        )
                    }
                } catch (mismatch: ComponentMismatchException) {
                    null
                } catch (exception: VerifyFailureException) {
                    if (logger.isDebugEnabled) {
                        logger.debug("Bot info [{}] verify failed. read raw value: \n{}", b.infoName, b.inputStream().use { it.bufferedReader().readText() })
                    }
                    throw exception
                }
            }


            registrars.ifEmpty {
                logger.warn("Bot info [{}] is not registered by any component", b.infoName)
                emptyList()
            }

        }
        logger.info("All bots register finished. Size of all bots: {}", allBots.size)
        logger.info("Starting all bots")
        allBots.forEach { b ->
            logger.debug("Starting bot {} of component {}", b.id, b.component)
            runBlocking { b.start() }
            logger.debug("Bot {} of component {} started. username: {}", b.id, b.component, b.username)
        }
        logger.info("All bots start finished.")



        val job = Job() // alive for join.
        Runtime.getRuntime().addShutdownHook(thread(start = false) {
            job.cancel()
        })

        return CoreSimbootContext(job)
    }

}


private class CoreSimbootContext(
    private val job: Job
) : SimbootContext {

    override suspend fun start(): Boolean = false
    override val isStarted: Boolean get() = job.isActive || job.isCompleted
    override val isActive: Boolean get() = job.isActive
    override val isCancelled: Boolean get() = job.isCancelled

    override suspend fun join() {
        job.join()
    }

    override suspend fun cancel(reason: Throwable?): Boolean {
        // close all bot manager
        OriginBotManager.cancel(reason)

        return if (job.isCancelled) {
            false
        } else {
            job.cancel()
            true
        }
    }

    override fun invokeOnCompletion(handler: CompletionHandler) {
        job.invokeOnCompletion(handler)
    }
}


private fun SimbootEntranceContext.toCoreBootEntranceContext(): CoreBootEntranceContext {
    return when (val app = application) {
        null -> throw SimbootApplicationException("CoreBootEntrance does not allow application to be null.")
        is CoreBootEntranceContext -> app
        is KClass<*> -> app.classToCoreBootEntranceContext(this)
        is Class<*> -> app.kotlin.classToCoreBootEntranceContext(this)
        is SimbootApplication -> app.annotationToCoreBootEntranceContext(this)
        else -> throw SimbootApplicationException(
            """
            CoreBootEntrance application only supports the following possible types:
            - An instance of [love.forte.simboot.core.CoreBootEntranceContext].
            - A (K)Class instance annotated @SimBootApplication(...).
            But not $app (${app::class}) you provided.
        """.trimIndent()
        )
    }
}


private fun KClass<*>.classToCoreBootEntranceContext(context: SimbootEntranceContext): CoreBootEntranceContext {
    // get annotation
    val tool = CoreBootEntrance.annotationTool
    val applicationAnnotation = tool.getAnnotation(this, SimbootApplication::class)
        ?: throw SimbootApplicationException("Application [$this] is not annotated @SimBootApplication.")

    return CoreBootEntranceContextImpl(applicationAnnotation, this, context)
}

private fun SimbootApplication.annotationToCoreBootEntranceContext(context: SimbootEntranceContext): CoreBootEntranceContext {
    return CoreBootEntranceContextImpl(this, null, context)
}

private class BalancedBotRegistrar(
    override val component: Component, registrars: List<BotRegistrar>
) : BotRegistrar {
    init {
        if (registrars.isEmpty()) {
            throw SimbotIllegalArgumentException("Registrars cannot be empty.")
        }

        registrars.forEachIndexed { i, it ->
            Simbot.require(component like it.component) { "Component of registrar $it index $i != target component $component" }
        }
    }

    private val iter = registrars.toList().asCycleIterator()

    override fun register(verifyInfo: BotVerifyInfo): Bot {
        return iter.next().register(verifyInfo)
    }
}


private fun CoreBootEntranceContext.allBinders(
    manager: BinderManager, beanContainer: BeanContainer, annotationTool: KAnnotationTool
) {

    beanContainer.all.forEach { name ->
        val type = beanContainer.getType(name)

        val isSub = kotlin.runCatching {
            type.isSubclassOf(ParameterBinderFactory::class)
        }.getOrElse { e ->
            if (e.toString()
                    .startsWith("kotlin.reflect.jvm.internal.KotlinReflectionInternalError: Unresolved class:")
            ) {
                kotlin.runCatching {
                    ParameterBinderFactory::class.java.isAssignableFrom(type.java)
                }.getOrElse {
                    logger.debug("cannot resolve type: $type")
                    false
                }
            } else throw e
        }

        if (isSub) {
            val binder = annotationTool.getAnnotation(type, Binder::class)
            if (binder != null) {
                when (binder.scope) {
                    Binder.Scope.GLOBAL -> {
                        manager.addGlobalBinder(beanContainer[name, ParameterBinderFactory::class])
                    }
                    Binder.Scope.SPECIFY -> {
                        val id = binder.id.firstOrNull()?.takeIf { it.isNotEmpty() } ?: name
                        manager.addIdBinder(id, beanContainer[name, ParameterBinderFactory::class])
                    }
                    Binder.Scope.CURRENT -> {
                        throw SimbotIllegalStateException("Class level binder's scope cannot be CURRENT.")
                    }
                }
            } else {
                manager.addIdBinder(name, beanContainer[name, ParameterBinderFactory::class])
            }
        } else {
            type.allFunctions.filter { f ->
                annotationTool.getAnnotation(f, Binder::class) != null
            }.forEach { f ->
                val binder = annotationTool.getAnnotation(f, Binder::class)!!
                when (binder.scope) {
                    Binder.Scope.GLOBAL -> {
                        manager.addGlobalBinder(f.toBinderFactory(name))
                    }
                    Binder.Scope.SPECIFY -> {
                        val id = binder.id.firstOrNull()
                            ?: throw SimbotIllegalStateException("The binder whose scope is SPECIFY must specify an id.")
                        manager.addIdBinder(id, f.toBinderFactory(name))
                    }
                    Binder.Scope.CURRENT -> {
                        // skip when scope is current.
                    }
                }
            }
        }


    }
}


private class BinderManager(
    private val idBinders: MutableMap<String, ParameterBinderFactory> = mutableMapOf(),
    private val globalBinders: MutableSet<ParameterBinderFactory> = mutableSetOf()
) : ParameterBinderFactoryContainer {

    val normalSize: Int get() = idBinders.size
    val globalSize: Int get() = globalBinders.size

    fun addIdBinder(id: String, factory: ParameterBinderFactory) {
        idBinders.merge(id, factory) { old, now ->
            throw SimbotIllegalStateException("Duplicate binder factory ID. id: $id, $old vs $now")
        }
    }

    fun addGlobalBinder(factory: ParameterBinderFactory) {
        if (!globalBinders.add(factory)) {
            throw SimbotIllegalStateException("Duplicate binder factory $factory")
        }
    }

    override fun get(id: String): ParameterBinderFactory? {
        return idBinders[id]
    }

    override fun getGlobals(): List<ParameterBinderFactory> {
        return globalBinders.toList()
    }

    override fun resolveFunctionToBinderFactory(beanId: String?, function: KFunction<*>): ParameterBinderFactory {
        return function.toBinderFactory(beanId)
    }
}


/**
 * 寻找并尝试加载所有的监听函数。
 */
private fun CoreBootEntranceContext.findAllListener(
    beanContainer: BeanContainer, baseBinderContainer: ParameterBinderFactoryContainer
): List<EventListener> {

    val processors = beanContainer.allInstance<ListenerAnnotationProcessor>().sortedBy { it.priority }

    val listeners = mutableMapOf<String, EventListener>()

    val registrar = ListListenerRegistrar { listener ->
        val id = listener.id.toString()

        if (listeners.containsKey(id)) {
            throw SimbotIllegalStateException("Duplicate listener id $id")
        }
        listeners[id] = listener
    }

    val tool = KAnnotationTool(mutableMapOf(), mutableMapOf())

    beanContainer.all.forEach { name ->
        val type = beanContainer.getType(name)
        for (func in type.allFunctions) {
            val listener = tool.getAnnotation(func, Listener::class) ?: continue
            val listens = tool.getAnnotation(func, Listens::class)
            val listenDataList = tool.getAnnotations(func, Listen::class)
            val listenerData = listener.toData(listens?.toData(listenDataList.map { it.toData() }))

            val context = ListenerAnnotationProcessorContextImpl(
                listenerData = listenerData,
                beanId = name,
                from = type,
                binderFactoryContainer = baseBinderContainer,
                function = func,
                beanContainer = beanContainer,
                listenerRegistrar = registrar
            )

            processors.forEach { processor ->
                processor.process(context)
            }
        }


    }

    logger.debug("Scan and include all top-level function listeners.")
    includeAllTopListeners(processors, tool, beanContainer, baseBinderContainer, registrar)

    return listeners.values.toList().also {
        logger.debug("All functional listeners resolved, size: {}", it.size)
    }
}

private object TopFuncFrom

private fun CoreBootEntranceContext.includeAllTopListeners(
    processors: Collection<ListenerAnnotationProcessor>,
    tool: KAnnotationTool,
    beanContainer: BeanContainer,
    baseBinderContainer: ParameterBinderFactoryContainer,
    registrar: ListListenerRegistrar
) {
    val pathReplace = Regex("[/\\\\]")
    ResourcesScanner<Class<*>>().use { scanner ->
        scanner.scan("").also {
            for (scanPkg in topFunctionScanPackages) {
                it.glob(scanPkg.replace(".", "/") + "**.class")
            }
        }.visitJarEntry { entry, _ ->
            val classname = entry.name.replace(pathReplace, ".").substringBeforeLast(".class")
            val loadClass = runCatching {
                scanner.classLoader.loadClass(classname)
            }.getOrElse { e -> throw SimbotIllegalStateException("Class load filed: $classname", e) }
            sequenceOf(loadClass)
        }.visitPath { (_, r) ->
            // '/Xxx.class'
            val classname = r.replace(pathReplace, ".").substringBeforeLast(".class").let {
                if (it.startsWith(".")) it.substring(1) else it
            }
            val loadClass = runCatching {
                scanner.classLoader.loadClass(classname)
            }.getOrElse { e -> throw SimbotIllegalStateException("Class load filed: $classname", e) }
            sequenceOf(loadClass)
        }.collectSequence(true).flatMap { c ->
            c.methods.mapNotNull { m ->
                kotlin.runCatching { m.kotlinFunction?.takeIf { f -> with(f.visibility) { this == null || this == KVisibility.PUBLIC } } }
                    .getOrDefault(null)
            }
        }.filter { f -> f.instanceParameter == null } // instance is null -> top function
            .filter { f -> tool.getAnnotation(f, Listener::class) != null }.forEach { func ->
                val listener = tool.getAnnotation(func, Listener::class)!!
                val listens = tool.getAnnotation(func, Listens::class)
                val listenDataList = tool.getAnnotations(func, Listen::class)
                val listenerData = listener.toData(listens?.toData(listenDataList.map { it.toData() }))

                val context = ListenerAnnotationProcessorContextImpl(
                    listenerData = listenerData,
                    beanId = null,
                    from = TopFuncFrom::class,
                    binderFactoryContainer = baseBinderContainer,
                    function = func,
                    beanContainer = beanContainer,
                    listenerRegistrar = registrar
                )

                processors.forEach { processor ->
                    processor.process(context)
                }
            }
    }
}


private class ListenerAnnotationProcessorContextImpl(
    override val listenerData: ListenerData,
    override val beanId: String?,
    override val from: KClass<*>,
    override val binderFactoryContainer: ParameterBinderFactoryContainer,
    override val function: KFunction<*>,
    override val beanContainer: BeanContainer,
    override val listenerRegistrar: EventListenerRegistrar
) : ListenerAnnotationProcessorContext


private class ListListenerRegistrar(private val handler: (EventListener) -> Unit) : EventListenerRegistrar {
    override fun register(listener: EventListener) {
        handler(listener)
    }

}


private val KClass<*>.allFunctions: List<KFunction<*>>
    get() = kotlin.runCatching {
        memberFunctions + memberExtensionFunctions
    }.getOrDefault(emptyList())
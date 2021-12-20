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

import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import love.forte.annotationtool.core.KAnnotationTool
import love.forte.di.BeanContainer
import love.forte.di.all
import love.forte.di.allInstance
import love.forte.simboot.*
import love.forte.simboot.annotation.*
import love.forte.simboot.core.filter.KeywordBinderFactory
import love.forte.simboot.core.internal.*
import love.forte.simboot.core.listener.AutoInjectBinderFactory
import love.forte.simboot.core.listener.EventParameterBinderFactory
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
import java.util.*
import kotlin.concurrent.thread
import kotlin.io.path.bufferedReader
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberExtensionFunctions
import kotlin.reflect.full.memberFunctions


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
        configuration: Configuration,
        beanContainer: BeanContainer
    ): Map<String, List<Map<String, String>>>


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

    override fun run(context: SimbootEntranceContext): SimbootContext {
        val bootContext: CoreBootEntranceContext = context.toCoreBootEntranceContext()
        // 获取所有配置
        val configuration = bootContext.getConfigurationFactory()(context)

        // 初始化 bean container
        val beanContainer = bootContext.getBeanContainerFactory()(configuration)

        // 初始化 listener manager -> listener manager factory
        val listenerManager = bootContext.getListenerManager(beanContainer)

        // 获取所有的 BotRegistrar -> BotRegistrarFactory
        val allBotRegistrarFactories = beanContainer.allInstance<BotRegistrarFactory>()

        // all registrars and group by component name.
        val allBotRegistrars = allBotRegistrarFactories.map { it(listenerManager) }

        // group by component.
        val allGroupedBotRegistrars: Map<String, BotRegistrar> =
            allBotRegistrars.groupBy { r -> r.component.id.toString() }.mapValues { (key, values) ->
                if (values.size != 1) {
                    context.logger.warn(
                        "There are multiple registrars under the component [{}], and they will be registered sequentially in a balanced manner.",
                        key
                    )
                    val component = values[0].component
                    BalancedBotRegistrar(component, values.toList())
                } else values[0]
            }

        // 所有的base binder factory
        val baseBinderFactories = mutableSetOf(
            KeywordBinderFactory,
            EventParameterBinderFactory, // event binder
            AutoInjectBinderFactory,
        )

        val binderManager = BinderManager(
            mutableMapOf(),
            baseBinderFactories
        )

        bootContext.allBinders(binderManager, beanContainer, annotationTool)


        // 所有的type，尝试解析为listener
        val listeners = findAllListener(
            beanContainer,
            binderManager
        )

        listeners.forEach(listenerManager::register)

        val bots = bootContext.allBots(configuration)

        // all init bots
        bots.mapNotNull { b ->
            val registrar = allGroupedBotRegistrars[b.component]
            if (registrar == null) {
                bootContext.logger.warn("Unknown bot component: {}, skip this bot.", b.component)
                null
            } else {
                registrar.register(b)
            }
        }.forEach { b ->
            bootContext.logger.debug("Starting bot {} of component {}", b.id, b.component)
            runBlocking { b.start() }
            bootContext.logger.debug("Bot {} of component {} started.", b.id, b.component)
        }


        val job = Job() // alive for join.
        Runtime.getRuntime().addShutdownHook(thread(start = false) {
            job.cancel()
        })

        return CoreSimbootContext(
            job::join,

            { reason ->
                // close all bot manager
                OriginBotManager.forEach {
                    it.cancel(reason)
                }
                job.cancel()
            },

            job::invokeOnCompletion
        )
    }
}

private class CoreSimbootContext(
    private val doJoin: suspend () -> Unit,
    private val doCancel: suspend (Throwable?) -> Unit,
    private val doInvokeOnCompletion: ((reason: Throwable?) -> Unit) -> Unit,
) : SimbootContext {
    override suspend fun join() {
        doJoin()
    }

    override suspend fun cancel(reason: Throwable?) {
        doCancel(reason)
    }

    override fun invokeOnCompletion(block: (reason: Throwable?) -> Unit) {
        doInvokeOnCompletion(block)
    }
}


private fun SimbootEntranceContext.toCoreBootEntranceContext(): CoreBootEntranceContext {
    return when (val app = application) {
        null -> throw SimbootApplicationException("CoreBootEntrance does not allow application to be null.")
        is CoreBootEntranceContext -> app
        is KClass<*> -> app.classToCoreBootEntranceContext(this)
        is Class<*> -> app.kotlin.classToCoreBootEntranceContext(this)
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

private class BalancedBotRegistrar(
    override val component: Component,
    private val registrars: List<BotRegistrar>
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
    manager: BinderManager,
    beanContainer: BeanContainer,
    annotationTool: KAnnotationTool
) {

    beanContainer.all.forEach { name ->
        val type = beanContainer.getType(name)

        if (type.isSubclassOf(ParameterBinderFactory::class)) {
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
            (type.memberFunctions + type.memberExtensionFunctions).filter { f ->
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

    internal fun addIdBinder(id: String, factory: ParameterBinderFactory) {
        idBinders.merge(id, factory) { old, now ->
            throw SimbotIllegalStateException("Duplicate binder factory ID. id: $id, $old vs $now")
        }
    }

    internal fun addGlobalBinder(factory: ParameterBinderFactory) {
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
private fun findAllListener(
    beanContainer: BeanContainer,
    baseBinderContainer: ParameterBinderFactoryContainer
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
        for (func in (type.memberFunctions + type.memberExtensionFunctions)) {
            val listener = tool.getAnnotation(func, Listener::class) ?: continue
            val listens = tool.getAnnotation(func, Listens::class)
            val listenDatas = tool.getAnnotations(func, Listen::class)
            val listenerData = listener.toData(listens?.toData(
                listenDatas.map { it.toData() }
            ))

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

    return listeners.values.toList()
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


private fun CoreBootEntranceContext.allBots(
    configuration: Configuration
): List<BotVerifyInfo> {
    val botResourceGlob = configuration.getString("simbot.core.bots.path")?.let { p ->
        if (p.endsWith(".bot")) p else "$p.bot"
    } ?: "simbot-bots/**.bot"


    // all bots verify info
    return ResourcesScanner<BotVerifyInfo>()
        .scan("")
        .glob(botResourceGlob)
        .visitJarEntry { _, url ->
            sequenceOf(
                url.openStream().bufferedReader().use { reader ->
                    Properties().also { p -> p.load(reader) }
                }.asBotVerifyInfo()
            )
        }
        .visitPath { (path, _) ->
            sequenceOf(
                path.bufferedReader().use { reader ->
                    Properties().also { p -> p.load(reader) }
                }.asBotVerifyInfo()
            )
        }
        .toList(false)


}
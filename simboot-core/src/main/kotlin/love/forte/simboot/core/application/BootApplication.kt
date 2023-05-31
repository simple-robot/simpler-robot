/*
 * Copyright (c) 2022-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simboot.core.application

import kotlinx.coroutines.CompletionHandler
import kotlinx.coroutines.Job
import kotlinx.serialization.ExperimentalSerializationApi
import love.forte.annotationtool.core.KAnnotationTool
import love.forte.annotationtool.core.getAnnotation
import love.forte.di.BeanContainer
import love.forte.di.all
import love.forte.di.core.CoreBeanManager
import love.forte.plugin.suspendtrans.annotation.JvmAsync
import love.forte.plugin.suspendtrans.annotation.JvmBlocking
import love.forte.simboot.SimbootContext
import love.forte.simboot.annotation.Binder
import love.forte.simboot.annotation.Binder.Scope.*
import love.forte.simboot.annotation.Listener
import love.forte.simboot.core.binder.*
import love.forte.simboot.core.listener.FunctionalListenerProcessContext
import love.forte.simboot.core.listener.KFunctionListenerProcessor
import love.forte.simboot.core.utils.isFinal
import love.forte.simboot.core.utils.isStatic
import love.forte.simboot.core.utils.scanResources
import love.forte.simboot.core.utils.scanTopClass
import love.forte.simboot.interceptor.AnnotatedEventListenerInterceptor
import love.forte.simboot.listener.ParameterBinderFactory
import love.forte.simbot.*
import love.forte.simbot.application.*
import love.forte.simbot.bot.BotVerifyInfoDecoder
import love.forte.simbot.bot.BotVerifyInfoDecoderFactory
import love.forte.simbot.bot.StandardBotVerifyInfoDecoderFactory
import love.forte.simbot.bot.toBotVerifyInfo
import love.forte.simbot.core.application.*
import love.forte.simbot.core.event.EventInterceptorsGenerator
import love.forte.simbot.core.event.EventListenerRegistrationDescriptionsGenerator
import love.forte.simbot.core.event.SimpleEventListenerManager
import love.forte.simbot.core.event.SimpleListenerManagerConfiguration
import love.forte.simbot.event.*
import love.forte.simbot.event.EventListenerRegistrationDescription.Companion.toRegistrationDescription
import love.forte.simbot.resources.Resource
import love.forte.simbot.utils.view
import org.slf4j.Logger
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.LongAdder
import javax.inject.Named
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KVisibility
import kotlin.reflect.full.*
import kotlin.reflect.jvm.kotlinFunction
import kotlin.time.Duration.Companion.nanoseconds

/**
 * boot-core 模块所提供的 [ApplicationFactory] 实现，基于 [SimpleApplication] 的拓展。
 *
 * 使用 [BootApplicationConfiguration] 作为配置类，使用 [BootApplicationBuilder] 作为 builder 实现。
 *
 * @see BootApplicationConfiguration
 * @see BootApplicationBuilder
 *
 */
public object Boot : ApplicationFactory<BootApplicationConfiguration, BootApplicationBuilder, BootApplication> {
    override suspend fun create(
        configurator: BootApplicationConfiguration.() -> Unit,
        builder: suspend BootApplicationBuilder.(BootApplicationConfiguration) -> Unit,
    ): BootApplication {
        // init configurator
        val config = BootApplicationConfiguration().also(configurator).also {
            it.initJob()
        }
        val logger = config.logger
        val startTime = System.nanoTime()
        val appBuilder = BootApplicationBuilderImpl().apply {
            builder(config)
        }
        return appBuilder.build(config).also {
            val duration = (System.nanoTime() - startTime).nanoseconds
            logger.info("Boot Application built in {}", duration.toString())
        }

    }
}


/**
 * [Boot] 所使用的配置类型。
 */
public open class BootApplicationConfiguration : SimpleApplicationConfiguration() {
    public companion object {
        public const val DEFAULT_BOT_VERIFY_GLOB: String = "simbot-bots/*.bot*"
    }


    /**
     * 提供额外参数，例如命令行参数。
     */
    public open var args: List<String> = emptyList()

    /**
     * 追加或设置 [BootApplicationConfiguration.args] 命令行参数。
     * 如果 [append] 为 `true`，则在 [BootApplicationConfiguration.args] 的基础上追加，否则覆盖。
     *
     * 如果 [append] 为 `false` 且 参数 [args]为空，[BootApplicationConfiguration.args] 会被覆盖为空。
     *
     */
    @JvmOverloads
    public open fun args(append: Boolean = true, vararg args: String) {
        if (append) {
            if (args.isNotEmpty()) {
                this.args = this.args + args.asList()
            }
        } else {
            this.args = args.asList()
        }
    }

    /**
     * 在通过 [classesScanPackage] 或 [topLevelListenerScanPackage] 进行包扫描的时候所使用的类加载器。
     */
    public open var classLoader: ClassLoader = BootApplicationConfiguration::class.java.classLoader

    /**
     * 需要加载的所有 `*.bot(.*)?` 文件的资源扫描glob。默认为 [DEFAULT_BOT_VERIFY_GLOB]。
     *
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public open var botConfigurationResources: List<String> = listOf(DEFAULT_BOT_VERIFY_GLOB)

    /**
     * 在 [botConfigurationResources] 之外可以提供其他独立的配置资源信息。
     * 例如一些系统文件系统中的某些指定资源作为 *.bot 验证信息文件。
     */
    public open var botConfigurations: List<Resource> = emptyList()

    /**
     * 配置用于针对 `*.bot` 配置文件的解码器列表。
     *
     * 如果此集合为空，则会尝试通过 [StandardBotVerifyInfoDecoderFactory.supportDecoderFactories]
     * 加载所有当前环境下所支持的解码器工厂并构建对应的解码器。
     */
    protected open val botVerifyInfoDecoderFactories: MutableMap<BotVerifyInfoDecoderFactory<*, *>, () -> BotVerifyInfoDecoder> =
        mutableMapOf()

    protected open val botVerifyInfoDecoderConfigurations: MutableMap<BotVerifyInfoDecoderFactory<*, *>, Any.() -> Unit> =
        mutableMapOf()

    /**
     * 使用一个 [BotVerifyInfoDecoderFactory]
     * 来配置并添加一个 [BotVerifyInfoDecoder] 到 [botVerifyInfoDecoderFactories] 中。
     *
     * 注: 目前同一个 [factory] 实例仅允许注册并配置一个。
     */
    @JvmOverloads
    public open fun <C : Any, D : BotVerifyInfoDecoder> botVerifyInfoDecoderFactory(
        factory: BotVerifyInfoDecoderFactory<C, D>,
        configurator: C.() -> Unit = {},
    ) {
        val oldConfig = botVerifyInfoDecoderConfigurations[factory] as? C.() -> Unit

        @Suppress("UNCHECKED_CAST") val newConfig: Any.() -> Unit = if (oldConfig == null) {
            {
                this as C
                configurator()
            }
        } else {
            {
                this as C
                oldConfig()
                configurator()
            }
        }

        botVerifyInfoDecoderConfigurations[factory] = newConfig

        val createFactory: () -> BotVerifyInfoDecoder = {
            factory.create(newConfig)
        }

        botVerifyInfoDecoderFactories.merge(factory, createFactory) { _, curr ->
            curr
        }
    }

    internal data class BotVerifyInfoDecoderFactoryWithConfiguration(
        val factory: BotVerifyInfoDecoderFactory<*, *>,
        val creator: () -> BotVerifyInfoDecoder,
    ) {
        fun create(): BotVerifyInfoDecoder = creator()
    }

    @OptIn(ExperimentalSimbotApi::class, ExperimentalSerializationApi::class)
    internal fun botVerifyDecodersOrDefaultStandards(): List<BotVerifyInfoDecoderFactoryWithConfiguration> {
        if (botVerifyInfoDecoderFactories.isNotEmpty()) {
            return botVerifyInfoDecoderFactories.map { (k, v) ->
                BotVerifyInfoDecoderFactoryWithConfiguration(k, v)
            }
        }

        return StandardBotVerifyInfoDecoderFactory.supportDecoderFactories(logger, classLoader)
            .map { BotVerifyInfoDecoderFactoryWithConfiguration(it) { it.create() } }
    }

    /**
     * 需要进行依赖扫描的所有包路径。
     *
     */
    public open var classesScanPackage: List<String> = emptyList()

    /**
     * 需要进行顶层监听函数扫描的包路径。
     */
    public open var topLevelListenerScanPackage: List<String> = emptyList()


    /**
     * 需要进行顶层Binder函数扫描的包路径。
     */
    public open var topLevelBinderScanPackage: List<String> = emptyList()


    /**
     * 是否在bot注册后，在 application 构建完毕的时候自动执行 `Bot.start`。
     *
     * 这一行为会注册到 [StandardApplicationBuilder.onCompletion] 中.
     *
     * 如果设置为 `true`，效果类似于：
     * ```kotlin
     * simbotApplication(...) {
     *     bots {
     *         register(...)?.also { bot ->
     *             onCompletion {
     *                 bot.start()
     *             }
     *         }
     *     }
     * }
     * ```
     *
     * 默认为`true`。
     *
     */
    public open var isAutoStartBots: Boolean = true

    /**
     * 当（通过扫描bot配置文件）注册多个bot时，其中某个bot注册失败时（出现异常时）的处理策略。
     *
     * 默认情况下会直接抛出出现异常。
     *
     * @see BotRegistrationFailurePolicy
     *
     * @since 3.1.0
     */
    public var botAutoRegistrationFailurePolicy: BotRegistrationFailurePolicy = BotRegistrationFailurePolicy.ERROR


    // BOT注册失败策略

}

/**
 * 当自动扫描的bot注册失败时的处理策略。
 *
 * @see BootApplicationConfiguration.botAutoRegistrationFailurePolicy
 *
 * @since 3.1.0
 */
public enum class BotRegistrationFailurePolicy {

    /**
     * 当bot注册过程中出现异常以及bot最终无法被注册 (没有匹配的 component) 时都会抛出异常。
     *
     * 是默认的选项。
     */
    ERROR,

    /**
     * 当bot注册过程中出现异常以及bot最终无法被注册 (没有匹配的 component) 时会输出带有异常信息的警告日志。
     */
    WARN,

    /**
     * 当bot注册过程中出现异常以及bot最终无法被注册 (没有匹配的 component) 时仅会输出 debug 调试日志。
     */
    IGNORE;
}


/**
 * 用于构建 [BootApplication] 的构建器。
 */
public interface BootApplicationBuilder : StandardApplicationBuilder<BootApplication> {

    /**
     * 事件处理器。
     */
    @ApplicationBuilderDsl
    override fun eventProcessor(configurator: SimpleListenerManagerConfiguration.(environment: Application.Environment) -> Unit)


    /**
     *
     * 配置当前环境中所使用的依赖管理器。
     * 在进行构建的时候，依赖管理中所有增加的内容都可能会被扫描并被处理，
     * 例如会自动扫描所有加载的bean中标记了 [Listener] 注解的函数并将它们解析为监听函数，
     * 以及会扫描所有实现了 [ParameterBinderFactory] 的类或者标记了 [@Binder][Binder] 的函数并追加至 [binders]
     * 中等。
     *
     * e.g.
     * ```kotlin
     *  beans {
     *      bean(name, instance)
     *      beanBy(name) { Foo() }
     *      bean(name, Foo::class) { Foo() }
     *      scan("com.example1", "com.example2")
     *  }
     * ```
     *
     */
    @ApplicationBuilderDsl
    public fun beans(beanContainerBuilder: BeanContainerBuilder.() -> Unit)


    /**
     * 在 [beans] 之外额外配置binder信息。
     */
    @ApplicationBuilderDsl
    public fun binders(parameterBinderBuilder: ParameterBinderBuilder.() -> Unit)

}


/**
 * [Boot] 所得到的最终的 [Application] 实现, 基于 [SimpleApplication].
 *
 * @see Boot
 */
public interface BootApplication : SimpleApplication, SimbootContext {

    /**
     * 当前环境中的 [Bean容器][BeanContainer].
     */
    public val beanContainer: BeanContainer

    /**
     * [BootApplication] 不需要执行 [start], 将会始终返回 `true`。
     */
    override suspend fun start(): Boolean = true

    /**
     * [BootApplication] 从一开始就是启用状态。
     */
    override val isStarted: Boolean
        get() = true


    @JvmBlocking
    @JvmAsync(baseName = "asFuture", suffix = "")
    override suspend fun join()

}


/**
 * [BootApplication] 实现。
 */
private class BootApplicationImpl(
    override val configuration: ApplicationConfiguration,
    override val environment: BootEnvironment,
    override val eventListenerManager: SimpleEventListenerManager,
    override val beanContainer: BeanContainer,
    providerList: List<EventProvider>,
) : BootApplication, BaseApplication() {
    override val providers: List<EventProvider> = providerList.view()

    override val coroutineContext = environment.coroutineContext
    override val logger = environment.logger

    private val job: Job? get() = coroutineContext[Job]

    override val isActive: Boolean
        get() = job?.isActive ?: true

    override val isCancelled: Boolean
        get() = job?.isCancelled ?: false


    override suspend fun cancel(reason: Throwable?): Boolean {
        shutdown(reason)
        return true
    }

    override fun invokeOnCompletion(handler: CompletionHandler) {
        job?.invokeOnCompletion(handler) ?: handler.invoke(null)
    }
}


/**
 * [BootApplicationBuilder] 的实现。
 */
private class BootApplicationBuilderImpl : BootApplicationBuilder, BaseStandardApplicationBuilder<BootApplication>() {
    private var beanContainerBuilderConfig: (BeanContainerBuilder.() -> Unit) = {}
    override fun beans(beanContainerBuilder: BeanContainerBuilder.() -> Unit) {
        beanContainerBuilderConfig.also { old ->
            beanContainerBuilderConfig = {
                old()
                beanContainerBuilder()
            }
        }
    }

    private var binderBuilderConfig: (ParameterBinderBuilder.() -> Unit) = {}
    override fun binders(parameterBinderBuilder: ParameterBinderBuilder.() -> Unit) {
        binderBuilderConfig.also { old ->
            binderBuilderConfig = {
                old()
                parameterBinderBuilder()
            }
        }
    }


    @OptIn(ExperimentalSimbotApi::class)
    @Suppress("DuplicatedCode")
    suspend fun build(configuration: BootApplicationConfiguration): BootApplication {
        val logger = configuration.logger
        val classLoader = configuration.classLoader
        val tool = KAnnotationTool(ConcurrentHashMap(), ConcurrentHashMap())

        // region build components
        logger.debug("Building components...")
        if (componentFactoriesSize() <= 0) {
            logger.debug("There are no installed components. Try to find and install all component factories in current environment.")
            installAllComponents(classLoader)
        }
        val components = buildComponents()
        logger.debug("Components are built: {}", components)
        logger.info("The size of components built is {}", components.size)
        // endregion


        logger.debug("Creating boot environment...")
        val environment = BootEnvironment(
            components, logger, configuration.coroutineContext
        )
        logger.debug("Boot environment created: {}", environment)


        // region build bean container
        val beanContainerBuilder = BeanContainerBuilderImpl(tool, classLoader, configuration)
        val scanPackage = configuration.classesScanPackage
        if (scanPackage.isNotEmpty()) {
            logger.debug("Initialize the bean container builder")
            logger.debug("Scan packages {} for bean", scanPackage)
            beanContainerBuilder.scan(classLoader, scanPackage)
        }

        logger.debug("Building bean container by builder: {}", beanContainerBuilder)
        beanContainerBuilder.beanContainerBuilderConfig()

        val beanContainer: CoreBeanManager = beanContainerBuilder.build()
        if (logger.isDebugEnabled) {
            logger.debug("Bean container is built: {}, The size of beans: {}", beanContainer, beanContainer.all.size)
        }

        // endregion

        // region build binders
        val binderBuilder = ParameterBinderBuilderImpl().also(binderBuilderConfig)


        // Binder containers.
        resolvedBinderContainerFromBeanContainer(binderBuilder, beanContainer, tool)
        resolvedBinderContainerFromScanTopLevelFunctions(
            binderBuilder, classLoader, configuration.topLevelBinderScanPackage, tool
        )

        // include default global binders
        binderBuilder.includeDefaults()

        val binderManager = binderBuilder.build()
        // endregion

        // region build listeners
        val processor = KFunctionListenerProcessor()

        eventProcessor {
            interceptors {
                autoConfigInterceptors(beanContainer)
            }
        }

        // auto scan listeners.
        listeners {
            autoConfigFromBeanContainer(logger, binderManager, beanContainer, processor, tool)
            autoScanTopFunction(
                classLoader,
                logger,
                binderManager,
                beanContainer,
                processor,
                tool,
                configuration.topLevelListenerScanPackage
            )
        }


        logger.debug("Building listener manager...")
        val listenerManager = buildListenerManager(configuration, environment)
        logger.debug("Listener manager is built: {}", listenerManager)
        // endregion

        // region build components
        // endregion

        // region build providers
        logger.debug("Building providers...")
        if (eventProviderFactoriesSize() <= 0) {
            logger.debug("There are no installed event providers. Try to find and install all event provider factories in current environment.")
            installAllEventProviders(classLoader)
        }
        val providers = buildProviders(listenerManager, components, configuration)
        logger.info("The size of providers built is {}", providers.size)
        if (providers.isNotEmpty()) {
            logger.debug("The built providers: {}", providers)
        }

        // endregion

        // region scan bots verify info and decoders
        logger.debug("Resolving bot verify infos and bot verify decoders...")
        // scan and auto register bot

        val botVerifyDecoderFactories = configuration.botVerifyDecodersOrDefaultStandards()
        if (logger.isDebugEnabled) {
            logger.debug("Using bot verify info decoder factories: {}", botVerifyDecoderFactories.map { it.factory })
        }
        if (botVerifyDecoderFactories.isEmpty()) {
            // 没有可用的bot verify info decoder, 无法自动扫描bot信息。
            logger.error("There is no bot verify info decoder available, and the bot information cannot be automatically scanned.")
        } else {
            bots {
                autoRegisterBots(
                    classLoader,
                    logger,
                    configuration.botConfigurationResources,
                    botVerifyDecoderFactories,
                    configuration.botConfigurations,
                    configuration
                )
            }
        }
        // endregion

        // create application
        val application = BootApplicationImpl(configuration, environment, listenerManager, beanContainer, providers)
        // set application attribute
        listenerManager.globalScopeContext[ApplicationAttributes.Application] = application

        // complete.
        complete(application)

        // region register bots
        // after complete.
        logger.debug("Registering bots...")
        val bots = registerBots(providers)
        logger.info("Bots all registered. The size of bots: {}", bots.size)
        if (bots.isNotEmpty()) {
            logger.debug("The all registered bots: {}", bots)
        }
        val isAutoStartBots = configuration.isAutoStartBots
        logger.debug("Auto start bots: {}", isAutoStartBots)
        if (isAutoStartBots && bots.isNotEmpty()) {
            bots.forEach { bot ->
                logger.info("Starting bot {}", bot)
                val started = bot.start()
                logger.info("Bot [{}] started: {}", bot, started)
            }
        }
        if (isAutoStartBots && bots.isEmpty()) {
            logger.debug("But the registered bots are empty.")
        }
        // endregion


        return application
    }


}


private fun resolvedBinderContainerFromBeanContainer(
    parameterBinderBuilder: ParameterBinderBuilder,
    beanContainer: BeanContainer,
    tool: KAnnotationTool,
) {
    beanContainer.all.forEach { name ->
        // type, and functions
        val type = beanContainer.getType(name)
        if (type.isSubclassOf(ParameterBinderFactory::class)) {
            val binder = tool.getAnnotation<Binder>(type)

            fun globalBinderFactory() {
                val instance = beanContainer[name, type] as ParameterBinderFactory
                parameterBinderBuilder.binder(binderFactory = instance)
            }

            fun specifyBinderFactory(id: String) {
                val instance = beanContainer[name, type] as ParameterBinderFactory
                parameterBinderBuilder.binder(id = id, binderFactory = instance)
            }

            if (binder == null) {
                globalBinderFactory()
            } else {
                val id = binder.value.firstOrNull()
                when (binder.scope) {
                    GLOBAL -> globalBinderFactory()
                    SPECIFY -> {
                        if (id == null) {
                            throw SimbotIllegalStateException("The Binder named [$name] of type [$type] annotate [$binder] with scope [SPECIFY], but the required property id (@Binder.value) is empty.")
                        }
                        specifyBinderFactory(id)
                    }

                    DEFAULT -> {
                        if (id == null) {
                            globalBinderFactory()
                        } else {
                            specifyBinderFactory(id)
                        }
                    }

                    else -> {
                        // 通过类实现的ParameterBinderFactory不允许使用Binder.Scope.CURRENT
                        throw SimbotIllegalStateException("Parameter Binder Factory implemented by class does not allow Binder.Scope.CURRENT, but $binder")
                    }
                }

            }
        } else {
            fun globalBinderFactory(func: KFunction<*>) {
                parameterBinderBuilder.binder(function = func) { context ->
                    context.beanContainer[name, type]
                }
            }

            fun specifyBinderFactory(id: String, func: KFunction<*>) {
                parameterBinderBuilder.binder(id, func) { context ->
                    context.beanContainer[name, type]
                }
            }

            runCatching {
                type.allFunctions.forEach a@{ func ->
                    // must be public
                    if (func.visibility != KVisibility.PUBLIC) return@a

                    val binder = tool.getAnnotation<Binder>(func) ?: return@a
                    val id = binder.value.firstOrNull()
                    val scope = binder.scope

                    // if current, skip.
                    if (scope == CURRENT) return@a

                    // if listener, skip.
                    if (tool.getAnnotation<Listener>(func) != null) return@a

                    when (scope) {
                        GLOBAL -> globalBinderFactory(func)
                        SPECIFY -> {
                            if (id == null) {
                                throw SimbotIllegalStateException("The Binder function [$func] of type [$type] named [$name] annotate [$binder] with scope [SPECIFY], but the required property id (@Binder.value) is empty.")
                            }
                            specifyBinderFactory(id, func)
                        }

                        else -> {
                            // is default, as global
                            globalBinderFactory(func)
                        }
                    }
                }
            }
        }
    }
}


@OptIn(InternalSimbotApi::class)
private fun resolvedBinderContainerFromScanTopLevelFunctions(
    parameterBinderBuilder: ParameterBinderBuilder,
    classLoader: ClassLoader,
    packages: List<String>,
    tool: KAnnotationTool,
) {

    fun globalBinderFactory(func: KFunction<*>) {
        parameterBinderBuilder.binder(function = func) { null }
    }

    fun specifyBinderFactory(id: String, func: KFunction<*>) {
        parameterBinderBuilder.binder(id, func) { null }
    }

    if (packages.isNotEmpty()) {
        scanTopClass(classLoader, packages, { _, _ ->
            null
        }) {
            forEach { c ->
                kotlin.runCatching {
                    c.methods.forEach a@{ m ->
                        val func = m.kotlinFunction ?: return@a
                        val binder = tool.getAnnotation<Binder>(func) ?: return@a
                        val scope = binder.scope
                        val id = binder.value.firstOrNull()

                        if (scope == Binder.Scope.CURRENT) {
                            // 顶层函数不能使用 CURRENT 作用域。
                            throw SimbotIllegalStateException("Top-level binder function cannot use the CURRENT scope. but the binder of function [$func] is $binder")
                        }

                        when (scope) {
                            Binder.Scope.GLOBAL -> globalBinderFactory(func)
                            Binder.Scope.SPECIFY -> {
                                if (id == null) {
                                    throw SimbotIllegalStateException("The top-level Binder function [$func] of class [$c] annotate [$binder] with scope [SPECIFY], but the required property id (@Binder.value) is empty.")
                                }
                                specifyBinderFactory(id, func)
                            }

                            else -> {
                                // default
                                if (id == null) {
                                    globalBinderFactory(func)
                                } else {
                                    specifyBinderFactory(id, func)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}


private fun ParameterBinderBuilder.includeDefaults() {
    binder(binderFactory = InstanceInjectBinderFactory)
    binder(binderFactory = EventParameterBinderFactory)
    binder(binderFactory = KeywordBinderFactory)
    binder(binderFactory = AutoInjectBinderFactory)
}


/**
 * 根据bean容器寻找并自动注册拦截器实例。
 */
private fun EventInterceptorsGenerator.autoConfigInterceptors(beanContainer: BeanContainer) {
    autoConfigProcessingInterceptors(beanContainer)
    autoConfigListenerInterceptors(beanContainer)
}


private fun EventInterceptorsGenerator.autoConfigProcessingInterceptors(beanContainer: BeanContainer) {
    beanContainer.all<EventProcessingInterceptor>().forEach { name ->
        val type = beanContainer.getType(name)
        if (!type.isSubclassOf(EventProcessingInterceptor::class)) {
            return@forEach
        }

        val instance = beanContainer[name, type] as EventProcessingInterceptor
        processingIntercept(name, instance)
    }
}

private fun EventInterceptorsGenerator.autoConfigListenerInterceptors(beanContainer: BeanContainer) {
    beanContainer.all<EventListenerInterceptor>().forEach { name ->
        val type = beanContainer.getType(name)
        if (!type.isSubclassOf(EventListenerInterceptor::class)) {
            return@forEach
        }

        if (type.isSubclassOf(AnnotatedEventListenerInterceptor::class)) {
            // 不使用 AnnotatedEventListenerInterceptor
            return@forEach
        }

        val instance = beanContainer[name, type] as EventListenerInterceptor
        listenerIntercept(name, instance)
    }
}


private fun EventListenerRegistrationDescriptionsGenerator.autoConfigFromBeanContainer(
    logger: Logger,
    binderManager: BinderManager,
    beanContainer: BeanContainer,
    listenerProcessor: KFunctionListenerProcessor,
    tool: KAnnotationTool,
) {
    val count = LongAdder()
    beanContainer.all.forEach { name ->
        val type = beanContainer.getType(name)
        // if is listener
        when {
            type.isSubclassOf(EventListener::class) -> {
                val listener = beanContainer[name, type] as EventListener
                logger.debug("Load event listener instance [{}] by type [{}] named [{}]", listener, type, name)
                listener(listener)
                count.increment()
            }

            type.isSubclassOf(EventListenerBuilder::class) -> {
                val builder = beanContainer[name, type] as EventListenerBuilder
                logger.debug("Load event listener builder instance [{}] by type [{}] named [{}]", builder, type, name)
                listener(builder.build())
                count.increment()
            }

            else -> {
                for (function in type.allFunctions) {
                    val listener = tool.getAnnotation(function, Listener::class) ?: continue
                    val returnType = function.returnType.classifier as? KClass<*>?
                    if (returnType?.isSubclassOf(EventListenerBuilder::class) == true) {
                        resolveEventListenerOrBuilderFunction(
                            type, function, logger, beanContainer, listener, count
                        )
                    } else {
                        resolveEventListenerFunction(
                            type, function, logger, binderManager, beanContainer, listenerProcessor, listener, count
                        )
                    }
                }
            }
        }
    }
    logger.info("The size of resolved event listeners is {}", count.sum())
}

private fun EventListenerRegistrationDescriptionsGenerator.resolveEventListenerFunction(
    type: KClass<*>?,
    function: KFunction<*>,
    logger: Logger,
    binderManager: BinderManager,
    beanContainer: BeanContainer,
    listenerProcessor: KFunctionListenerProcessor,
    listener: Listener,
    count: LongAdder,
) {
    if (type == null) {
        logger.debug("Resolving top-level listener function [{}] from top", function)
    } else {
        logger.debug("Resolving listener function [{}] from type [{}]", function, type)
    }

    val resolvedListener = listenerProcessor.process(
        FunctionalListenerProcessContext(
            function = function,
            binderManager = binderManager,
            beanContainer = beanContainer,
        )
    )

    val description = resolvedListener.toRegistrationDescription(priority = listener.priority, isAsync = listener.async)

    if (type == null) {
        logger.debug("Resolved top-level listener description: [{}] by processor [{}]", description, listenerProcessor)
    } else {
        logger.debug("Resolved listener description: [{}] by processor [{}]", description, listenerProcessor)
    }

    // include listener.
    count.increment()
    listener(description)
}

private fun EventListenerRegistrationDescriptionsGenerator.resolveEventListenerOrBuilderFunction(
    type: KClass<*>?,
    function: KFunction<*>,
    logger: Logger,
    beanContainer: BeanContainer,
    listener: Listener,
    count: LongAdder,
) {
    val kParameters = function.parameters
    if (kParameters.any { it.kind != KParameter.Kind.INSTANCE }) {
        logger.warn(
            "It is not recommended to have parameters in the function that registers the Event Listener Builder, but function: {}",
            function
        )
    }
    val parameters = kParameters.associateWith { beanContainer.getByKParameter(it) }

    when (val result = function.callBy(parameters)) {
        is EventListenerRegistrationDescriptionBuilder -> {
            val buildDescription = result.buildDescription()
            listener(buildDescription)
            if (type == null) {
                logger.debug("Resolved top-level listener: [{}] by description builder [{}]", buildDescription, result)
            } else {
                logger.debug(
                    "Resolved listener: [{}] from [{}] by description builder [{}]",
                    buildDescription,
                    type,
                    result
                )
            }
        }

        is EventListenerBuilder -> {
            val built = result.build()
            val description = built.toRegistrationDescription(priority = listener.priority, isAsync = listener.async)
            listener(description)
            if (type == null) {
                logger.debug(
                    "Resolved top-level listener built [{}] with description: [{}] by builder [{}]",
                    built,
                    description,
                    result
                )
            } else {
                logger.debug(
                    "Resolved listener built [{}] with description: [{}] from [{}] by builder [{}]",
                    built,
                    description,
                    type,
                    result
                )
            }
        }

        is EventListener -> {
            val description = result.toRegistrationDescription(priority = listener.priority, isAsync = listener.async)
            listener(description)
            if (type == null) {
                logger.debug("Resolved top-level listener [{}] with description: [{}]", result, description)
            } else {
                logger.debug("Resolved listener [{}] with description: [{}] from [{}]", result, description, type)
            }
        }

        else -> return
    }




    count.increment()
}


private var topLevelEventListenerBuilderWarn: Any? = Any()
private inline fun doTopLevelEventListenerBuilderWarn(block: () -> Unit) {
    topLevelEventListenerBuilderWarn?.also { mark ->
        synchronized(mark) {
            if (topLevelEventListenerBuilderWarn != null) {
                block()
                topLevelEventListenerBuilderWarn = null
            }
        }
    }
}

@OptIn(InternalSimbotApi::class)
private fun EventListenerRegistrationDescriptionsGenerator.autoScanTopFunction(
    classLoader: ClassLoader,
    logger: Logger,
    binderManager: BinderManager,
    beanContainer: BeanContainer,
    listenerProcessor: KFunctionListenerProcessor,
    tool: KAnnotationTool,
    targets: List<String>,
) {
    val count = LongAdder()
    // 扫描所有的 final 且没有标记named注解的类。
    if (targets.isNotEmpty()) {
        scanTopClass(classLoader, targets, { e, className ->
            logger.warn("Class [{}] failed to load and will be skipped.", className)
            if (logger.isDebugEnabled) {
                logger.debug("Reason for failure: $e", e)
            }
            null
        }) {
            forEach { c ->
                c.methods.mapNotNull { m ->
                    kotlin.runCatching {
                        if (!m.isFinal || !m.isStatic) {
                            return@mapNotNull null
                        }

                        val kf = m.kotlinFunction ?: return@mapNotNull null
                        if (kf.instanceParameter != null) {
                            return@mapNotNull null
                        }

                        kf
                    }.getOrElse { e ->
                        if (logger.isDebugEnabled) {
                            logger.debug(
                                "The method [$m] of class [$c] cannot be resolved to KFunction. Skip for now.", e
                            )
                        }
                        null
                    }
                }.forEach { function ->
                    kotlin.runCatching {
                        val listener = tool.getAnnotation<Listener>(function) ?: return@runCatching

                        // 没有参数、且返回值为 EventListenerBuilder, 则使用EventListenerBuilder返回值。
                        val returnType = function.returnType.classifier as? KClass<*>?
                        if (returnType?.isSubclassOf(EventListenerBuilder::class) == true) {
                            resolveEventListenerOrBuilderFunction(
                                null, function, logger, beanContainer, listener, count
                            )
                            doTopLevelEventListenerBuilderWarn {
                                logger.warn("Using the top-level function to register the Event Listener Builder is still experimental.")
                            }
                        } else {
                            resolveEventListenerFunction(
                                null, function, logger, binderManager, beanContainer, listenerProcessor, listener, count
                            )
                        }
                    }
                }
            }
        }
    }

    logger.info("The size of resolved Top-Level event listeners is {}", count.sum())
}


@OptIn(InternalSimbotApi::class)
private fun BotRegistrar.autoRegisterBots(
    classLoader: ClassLoader,
    logger: Logger,
    scanResources: List<String>,
    decoderFactories: List<BootApplicationConfiguration.BotVerifyInfoDecoderFactoryWithConfiguration>,
    botResources: List<Resource>,
    configuration: BootApplicationConfiguration,
) {
    val botVerifyInfoList = scanResources(classLoader, scanResources) {
        plus(botResources).mapNotNull { r ->
            val decoder = decoderFactories.find { it.factory.match(r.name) }?.create()
            if (decoder == null) {
                logger.warn("No decoder factories match resource [{}] named [{}], skip.", r, r.name)
                return@mapNotNull null
            }

            r.toBotVerifyInfo(decoder)
        }
    }.toList()

    logger.info("The size of resolved bot verify infos is {}", botVerifyInfoList.size)

    val failurePolicy = configuration.botAutoRegistrationFailurePolicy

    botVerifyInfoList.forEach { botInfo ->
        val bot = try {
            register(botInfo)
        } catch (e: Throwable) {
            when (failurePolicy) {
                BotRegistrationFailurePolicy.ERROR -> {
                    logger.error("Bot verify info [{}] registration failed (with {})", botInfo, this)
                    throw e
                }

                BotRegistrationFailurePolicy.IGNORE -> {
                    logger.debug("Bot verify info [{}] registration failed (with {})", botInfo, this, e)
                    null
                }

                BotRegistrationFailurePolicy.WARN -> {
                    // 忽略它并输出此警告
                    logger.warn("Bot verify info [{}] registration failed (with {})", botInfo, this, e)
                    null
                }
            }
        }

        if (bot == null) {
            @Suppress("DuplicatedCode")
            when (failurePolicy) {
                BotRegistrationFailurePolicy.ERROR -> {
                    val err = BotAutoRegistrationFailureException("Bot($botInfo)")
                    logger.error("Bot verify info [{}] is not matched by any manager.", botInfo, err)
                    throw err
                }

                BotRegistrationFailurePolicy.WARN -> {
                    val warn = BotAutoRegistrationFailureException("Bot($botInfo)") // For log only.
                    logger.warn("Bot verify info [{}] is not matched by any manager.", botInfo, warn)
                }

                else -> {
                    // do nothing.
                }
            }
        }
    }
}

/**
 * 通过自动扫描注册bot时bot无法注册时出现的异常。
 *
 */
public class BotAutoRegistrationFailureException @InternalSimbotApi constructor(message: String?) : IllegalStateException(message)


private val KClass<*>.allFunctions: List<KFunction<*>>
    get() = kotlin.runCatching {
        memberFunctions + memberExtensionFunctions
    }.getOrDefault(emptyList())


@Suppress("DuplicatedCode")
private fun BeanContainer.getByKParameter(parameter: KParameter): Any {
    val name = parameter.findAnnotation<Named>()?.value?.let { n ->
        n.ifEmpty {
            kotlin.runCatching { parameter.name }.getOrNull()
        }
    }
    val type = parameter.type.classifier as? KClass<*>?
    val value = when {
        name == null && type == null -> {
            throw IllegalStateException("The name and type of parameter [$parameter] are both null")
        }

        name == null && type != null -> {
            // only type
            get(type)
        }

        type == null && name != null -> {
            // only name
            get(name)
        }

        else -> {
            // both not null
            get(name!!, type!!)
        }
    }

    return value
}

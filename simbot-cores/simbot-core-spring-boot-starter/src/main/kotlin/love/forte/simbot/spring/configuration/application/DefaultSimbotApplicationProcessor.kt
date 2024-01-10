/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package love.forte.simbot.spring.configuration.application

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import love.forte.simbot.bot.*
import love.forte.simbot.component.Component
import love.forte.simbot.logger.Logger
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.logger.logger
import love.forte.simbot.spring.common.BotAutoStartOnFailureException
import love.forte.simbot.spring.common.BotConfigResourceLoadOnFailureException
import love.forte.simbot.spring.common.MismatchConfigurableBotManagerException
import love.forte.simbot.spring.common.application.*
import love.forte.simbot.spring.configuration.listener.SimbotEventListenerResolver
import love.forte.simbot.suspendrunner.runInNoScopeBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import java.io.FileNotFoundException

/**
 * [SimbotApplicationProcessor] 的默认实现
 * [DefaultSimbotApplicationProcessor]
 * 的配置器。
 *
 *
 */
@Configuration(proxyBeanMethods = false)
public open class DefaultSimbotSpringApplicationProcessorConfiguration {
    @Bean(DEFAULT_SIMBOT_APPLICATION_PROCESSOR_BEAN_NAME)
    @ConditionalOnMissingBean(SimbotApplicationProcessor::class)
    public open fun defaultSimbotApplicationProcessor(
        properties: SpringApplicationConfigurationProperties,
        @Autowired(required = false) eventListenerResolvers: List<SimbotEventListenerResolver>? = null,
        @Autowired(required = false) preConfigurer: List<SimbotApplicationPreConfigurer>? = null,
        @Autowired(required = false) postConfigurer: List<SimbotApplicationPostConfigurer>? = null
    ): DefaultSimbotApplicationProcessor =
        DefaultSimbotApplicationProcessor(
            properties = properties,
            eventListenerResolvers = eventListenerResolvers ?: emptyList(),
            preConfigurer = preConfigurer ?: emptyList(),
            postConfigurer = postConfigurer ?: emptyList(),
        )

    public companion object {
        public const val DEFAULT_SIMBOT_APPLICATION_PROCESSOR_BEAN_NAME: String =
            "love.forte.simbot.spring.configuration.application.defaultSimbotApplicationProcessor"
    }
}


/**
 * [SimbotApplicationProcessor] 的默认实现，
 * 默认行为中会进行如下内容：
 * - 扫描、加载事件处理器（[SimbotEventListenerResolver]）并注册
 * - 扫描、加载所有的可注册 bot
 *
 * @author ForteScarlet
 */
public class DefaultSimbotApplicationProcessor(
    private val properties: SpringApplicationConfigurationProperties,
    private val eventListenerResolvers: List<SimbotEventListenerResolver>,
    private val preConfigurer: List<SimbotApplicationPreConfigurer>,
    private val postConfigurer: List<SimbotApplicationPostConfigurer>
) : SimbotApplicationProcessor {
    override fun process(application: SpringApplication) {
        preConfigurer.forEach {
            it.configure(application)
            logger.debug("Configured application {} by pre configurer {}", application, it)
        }

        eventListenerResolvers.forEach {
            it.resolve(application)
            logger.debug("Resolved event listener with application {} by listener resolver {}", application, it)
        }

        process0(application)

        postConfigurer.forEach {
            it.configure(application)
            logger.debug("Configured application {} by post configurer {}", application, it)
        }
    }


    private fun process0(application: SpringApplication) {
        loadBots(application)

    }

    private fun loadBots(application: SpringApplication) {
        BotAutoLoader(properties.bots, application).load()
    }


    public companion object {
        private val logger = LoggerFactory.logger<DefaultSimbotApplicationProcessor>()
    }
}


private class BotAutoLoader(
    val properties: SpringApplicationConfigurationProperties.BotProperties,
    val application: SpringApplication
) {
    @OptIn(ExperimentalSerializationApi::class)
    val json = Json {
        isLenient = true
        classDiscriminator = Component.CLASS_DISCRIMINATOR
        ignoreUnknownKeys = true
        allowTrailingComma = true
        decodeEnumsCaseInsensitive = true
        allowSpecialFloatingPointValues = true
        prettyPrint = false
        serializersModule = application.components.serializersModule
    }
    val botManagers = application.botManagers

    fun load() {
        val resolver = PathMatchingResourcePatternResolver()

        val botList =
            properties.configurationJsonResources
                .asSequence()
                .flatMap {
                    try {
                        resolver.getResources(it).asSequence()
                    } catch (fne: FileNotFoundException) {
                        logger.warn(
                            "Bot configuration resource path [{}] could not be resolved because of FileNotFoundException(message={}), will be skip.",
                            it,
                            fne.localizedMessage,
                        )
                        logger.debug(
                            "Bot configuration resource path [{}] could not be resolved because of FileNotFoundException(message={}), will be skip.",
                            it,
                            fne.localizedMessage,
                            fne
                        )

                        emptySequence()
                    }
                }
                .distinct()
                .mapNotNull { resource ->
                    logger.debug("Resolving bot auto-register configuration resource: {}", resource)
                    if (resource.filename == null) {
                        logger.warn("Filename of resource [{}] is null", resource)
                    }

                    val content = loadResourceContent(properties.autoRegistrationResourceLoadFailurePolicy, resource)
                        ?: return@mapNotNull null

                    val configuration = try {
                        json.decodeFromString(SerializableBotConfiguration.serializer(), content)
                    } catch (se: Throwable) {
                        processDecodeFailure(resource, se, properties.autoRegistrationResourceLoadFailurePolicy)
                        return@mapNotNull null
                    }

                    var bot: Bot? = null

                    for (manager in botManagers) {
                        try {
                            if (manager.configurable(configuration)) {
                                bot = manager.register(configuration)
                            }
                        } catch (e: Throwable) {
                            processRegisterBotFailed(
                                properties.autoRegistrationFailurePolicy,
                                e,
                                resource,
                                configuration,
                                manager
                            )
                        }
                    }

                    if (bot == null) {
                        mismatchConfigurableManager(
                            properties.autoRegistrationMismatchConfigurableBotManagerPolicy,
                            resource,
                            configuration,
                            botManagers
                        )
                    }

                    bot
                }
                .onEach { bot ->
                    logger.debug("Registered bot: {}", bot)
                }
                .toList()

        logger.info("The number of registered bots is {}", botList.size)

        processedBotWithPolicy(botList)
    }

    /**
     * 加载资源文本或根据策略处理异常
     */
    private fun loadResourceContent(policy: BotConfigResourceLoadFailurePolicy, resource: Resource): String? {
        var content: String? = null
        val failures = mutableListOf<Throwable>()
        if (resource.isFile) {
            kotlin.runCatching {
                content = resource.file.readText()
            }.getOrElse(failures::add)
        }

        if (content == null) {
            kotlin.runCatching {
                content = resource.url.readText()
            }.getOrElse(failures::add)
        }

        return content ?: run {
            val err = BotConfigResourceLoadOnFailureException("Cannot load text content for resource $resource").apply {
                failures.forEach { addSuppressed(it) }
            }
            when (policy) {
                BotConfigResourceLoadFailurePolicy.ERROR -> throw err
                BotConfigResourceLoadFailurePolicy.ERROR_LOG -> {
                    logger.error(err.message, err)
                    null
                }

                BotConfigResourceLoadFailurePolicy.WARN -> {
                    logger.warn(err.message, err)
                    null
                }

                BotConfigResourceLoadFailurePolicy.IGNORE -> {
                    logger.debug(err.message, err)
                    null
                }
            }
        }

    }

    /**
     * 根据策略处理无法解析 content 内容的异常
     */
    private fun processDecodeFailure(
        resource: Resource,
        se: Throwable,
        policy: BotConfigResourceLoadFailurePolicy
    ) {
        val message = se.localizedMessage
        val errMsg = if (se is SerializationException && message.contains("Polymorphic", ignoreCase = true)) {
            "JSON resource [$resource] fails to deserialize, " +
                    "and this may be because a component does not support polymorphic configurations, " +
                    "e.g. the configuration class does not implement ${SerializableBotConfiguration::class}, " +
                    "the component does not provide polymorphic serialisation information, etc. " +
                    "The information: $message"
        } else {
            "JSON resource [$resource] fails to deserialize, The information: $message"
        }

        val ex = BotConfigResourceLoadOnFailureException(errMsg, se)

        when (policy) {
            BotConfigResourceLoadFailurePolicy.ERROR -> throw ex
            BotConfigResourceLoadFailurePolicy.ERROR_LOG -> logger.error(errMsg, ex)
            BotConfigResourceLoadFailurePolicy.WARN -> logger.warn(errMsg, ex)
            BotConfigResourceLoadFailurePolicy.IGNORE -> logger.debug(errMsg, ex)
        }
    }

    /**
     * 根据策略处理注册bot时出现异常的情况。
     */
    private fun processRegisterBotFailed(
        policy: BotRegistrationFailurePolicy,
        e: Throwable,
        resource: Resource,
        configuration: SerializableBotConfiguration,
        botManager: BotManager
    ) {
        val message =
            "Failed to register bot " +
                    "from resource [$resource] " +
                    "to manager [$botManager] " +
                    "via configuration [$configuration]. " +
                    "The information: ${e.localizedMessage}"

        val ex = BotRegisterFailureException(message, e)
        when (policy) {
            BotRegistrationFailurePolicy.ERROR -> throw ex
            BotRegistrationFailurePolicy.ERROR_LOG -> logger.error(message, ex)
            BotRegistrationFailurePolicy.WARN -> logger.warn(message, ex)
            BotRegistrationFailurePolicy.IGNORE -> logger.debug(message, ex)
        }
    }

    /**
     * 根据策略处理无法找到匹配的可配置 [BotManager] 的情况
     */
    private fun mismatchConfigurableManager(
        policy: MismatchConfigurableBotManagerPolicy,
        resource: Resource,
        configuration: SerializableBotConfiguration,
        botManagers: BotManagers
    ) {
        val message = "No registrable BotManager " +
                "matching configuration [$configuration] (type: ${configuration::class}) " +
                "from resource [$resource] was found in $botManagers"

        val ex = MismatchConfigurableBotManagerException(message)

        when (policy) {
            MismatchConfigurableBotManagerPolicy.ERROR -> throw ex
            MismatchConfigurableBotManagerPolicy.ERROR_LOG -> logger.error(message, ex)
            MismatchConfigurableBotManagerPolicy.WARN -> logger.warn(message, ex)
            MismatchConfigurableBotManagerPolicy.IGNORE -> logger.debug(message, ex)
        }

    }

    private fun processedBotWithPolicy(botList: List<Bot>) {
        val autoStartBots = properties.autoStartBots
        val policy = properties.autoRegistrationFailurePolicy
        val autoStartMode = properties.autoStartMode

        logger.debug(
            "Auto start bots is {} with onFailure policy {} and start mode {}",
            autoStartBots,
            policy,
            autoStartMode
        )

        if (autoStartBots) {
            when (autoStartMode) {
                BotAutoStartMode.BLOCK -> startBotsInBlocking(policy, botList)
                BotAutoStartMode.ASYNC -> startBotsInAsync(policy, botList)
            }
        }
    }

    private fun startBotsInAsync(policy: BotRegistrationFailurePolicy, botList: List<Bot>) {
        when (policy) {
            BotRegistrationFailurePolicy.ERROR -> {
                // 异常通过日志输出并关闭 application: 异步中直接抛异常没什么效果
                application.launch {
                    // 任意失败全盘皆输
                    try {
                        coroutineScope {
                            botList.forEach { bot ->
                                launch { bot.start() }
                                logger.debug("Launched to start bot {}", bot)
                            }
                        }
                    } catch (e: Throwable) {
                        val err = BotAutoStartOnFailureException(e)
                        logger.error(
                            "There are certain bots that have exceptions in asynchronous startups, application will be cancelled",
                            err
                        )
                        application.cancel(err)
                    }
                }
            }

            BotRegistrationFailurePolicy.ERROR_LOG -> startBotsInAsyncOnFutureWithLog(botList) { error(it.message, it) }
            BotRegistrationFailurePolicy.WARN -> startBotsInAsyncOnFutureWithLog(botList) { warn(it.message, it) }
            BotRegistrationFailurePolicy.IGNORE -> startBotsInAsyncOnFutureWithLog(botList) { debug(it.message, it) }
        }
    }

    private inline fun startBotsInAsyncOnFutureWithLog(
        botList: List<Bot>,
        crossinline onFailure: Logger.(e: Throwable) -> Unit
    ) {
        application.launch {
            supervisorScope {
                botList.forEach { bot ->
                    launch {
                        kotlin.runCatching { bot.start() }
                            .getOrElse { e -> logger.onFailure(BotAutoStartOnFailureException(e)) }
                    }
                    logger.debug("Launched to start bot {}", bot)
                }
            }
        }
    }

    /**
     * 阻塞地依次启动 bot。
     */
    private fun startBotsInBlocking(policy: BotRegistrationFailurePolicy, botList: List<Bot>) {
        runInNoScopeBlocking {
            for (bot in botList) {
                logger.debug("Starting bot {}", bot)
                try {
                    bot.start()
                    logger.debug("Bot {} started successfully", bot)
                } catch (e: Throwable) {
                    val message = "Bot $bot auto start on failure: ${e.localizedMessage}"
                    val ex = BotAutoStartOnFailureException(message, e)
                    when (policy) {
                        BotRegistrationFailurePolicy.ERROR -> throw ex
                        BotRegistrationFailurePolicy.ERROR_LOG -> logger.error(message, ex)
                        BotRegistrationFailurePolicy.WARN -> logger.warn(message, ex)
                        BotRegistrationFailurePolicy.IGNORE -> logger.debug(message, ex)
                    }
                }
            }
        }
    }


    companion object {
        private val logger = LoggerFactory.logger<BotAutoLoader>()
    }
}

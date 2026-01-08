/*
 * Copyright (c) 2021-2025. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.qguild.bot

import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import love.forte.simbot.application.ApplicationConfiguration
import love.forte.simbot.bot.BotManager
import love.forte.simbot.bot.SerializableBotConfiguration
import love.forte.simbot.bot.UnsupportedBotConfigurationException
import love.forte.simbot.common.coroutines.linkTo
import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.function.invokeBy
import love.forte.simbot.common.id.ID
import love.forte.simbot.component.NoSuchComponentException
import love.forte.simbot.component.find
import love.forte.simbot.component.qguild.QQGuildComponent
import love.forte.simbot.component.qguild.bot.config.QGBotComponentConfiguration
import love.forte.simbot.component.qguild.bot.config.QGBotFileConfiguration
import love.forte.simbot.component.qguild.internal.bot.QQGuildBotManagerImpl
import love.forte.simbot.event.EventDispatcher
import love.forte.simbot.plugin.PluginConfigureContext
import love.forte.simbot.plugin.PluginFactory
import love.forte.simbot.plugin.PluginFactoryConfigurerProvider
import love.forte.simbot.plugin.PluginFactoryProvider
import love.forte.simbot.qguild.stdlib.Bot
import love.forte.simbot.qguild.stdlib.ConfigurableBotConfiguration
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * QQ频道BOT的bot管理器。
 *
 * [QQGuildBotManager] 不允许注册相同 `appId` 的bot。
 *
 * _Note: [QQGuildBotManager] 仅由内部继承实现使用，对外不稳定_
 *
 * @author ForteScarlet
 */
public interface QQGuildBotManager : BotManager {
    public val eventDispatcher: EventDispatcher
    public val configuration: QQGuildBotManagerConfiguration

    override fun get(id: ID): QGBot

    override fun all(): Sequence<QGBot>

    override fun all(id: ID): Sequence<QGBot>

    @OptIn(ExperimentalContracts::class)
    private fun checkConfig(configuration: SerializableBotConfiguration): Boolean {
        contract {
            returns(true) implies (configuration is QGBotFileConfiguration)
        }
        return configuration is QGBotFileConfiguration
    }

    override fun configurable(configuration: SerializableBotConfiguration): Boolean = checkConfig(configuration)

    /**
     * 注册一个Bot的信息
     */
    override fun register(configuration: SerializableBotConfiguration): QGBot {
        if (!checkConfig(configuration)) {
            throw UnsupportedBotConfigurationException("Required configuration type: ${QGBotFileConfiguration::class}, but $configuration (${configuration::class})")
        }

        val c: QGBotFileConfiguration = configuration

        // no config
        val (appId, secret, token) = c.ticket.toTicket()
        return register(appId, secret, token) {
            c.includeConfig(this)
        }
    }

    /**
     * 通过所需信息注册一个bot。
     *
     * 注意，在配置 [ConfigurableBotConfiguration] 时，
     * 如果 [coroutineContext][ConfigurableBotConfiguration.coroutineContext] 中
     * 存在自定义的 [Job][kotlinx.coroutines.Job]，那么 `Application` 中的Job
     * 则会作为一个 `Root Job` 而不是 `Parent Job` 使用。
     *
     * `Root Job` 仅会在其自身完成或关闭的时候**通知**相关联的子类使它们关闭，
     * 但不会有硬性关联，这种通知是通过 [Job.invokeOnCompletion][kotlinx.coroutines.Job.invokeOnCompletion] 实现的，
     * 参考 [Job.linkTo]。
     *
     * 如果 [coroutineContext][ConfigurableBotConfiguration.coroutineContext]
     * 中不存在自定义的Job，则会直接使用 [QQGuildBotManager][love.forte.simbot.component.qguild.bot.QQGuildBotManager]
     * 内的 [Job] 作为 parent Job 。
     *
     */
    public fun register(
        appId: String,
        secret: String,
        token: String,
        block: ConfigurerFunction<QGBotComponentConfiguration>?,
    ): QGBot

    /**
     * 通过所需信息注册一个bot。
     *
     * 注意，在配置 [ConfigurableBotConfiguration] 时，
     * 如果 [coroutineContext][ConfigurableBotConfiguration.coroutineContext] 中
     * 存在自定义的 [Job][kotlinx.coroutines.Job]，那么 `Application` 中的Job
     * 则会作为一个 `Root Job` 而不是 `Parent Job` 使用。
     *
     * `Root Job` 仅会在其自身完成或关闭的时候**通知**相关联的子类使它们关闭，
     * 但不会有硬性关联，这种通知是通过 [Job.invokeOnCompletion][kotlinx.coroutines.Job.invokeOnCompletion] 实现的，
     * 参考 [Job.linkTo]。
     *
     * 如果 [coroutineContext][ConfigurableBotConfiguration.coroutineContext]
     * 中不存在自定义的Job，则会直接使用
     * [QQGuildBotManager][love.forte.simbot.component.qguild.bot.QQGuildBotManager]
     * 内的 [Job] 作为 parent Job 。
     *
     */
    public fun register(
        appId: String,
        secret: String,
        token: String
    ): QGBot = register(appId, secret, token, block = null)

    /**
     * 通过所需信息注册一个bot。
     *
     * 注意，在配置 [ConfigurableBotConfiguration] 时，
     * 如果 [coroutineContext][ConfigurableBotConfiguration.coroutineContext] 中
     * 存在自定义的 [Job][kotlinx.coroutines.Job]，那么 `Application` 中的Job
     * 则会作为一个 `Root Job` 而不是 `Parent Job` 使用。
     *
     * `Root Job` 仅会在其自身完成或关闭的时候**通知**相关联的子类使它们关闭，
     * 但不会有硬性关联，这种通知是通过 [Job.invokeOnCompletion][kotlinx.coroutines.Job.invokeOnCompletion] 实现的，
     * 参考 [Job.linkTo]。
     *
     * 如果 [coroutineContext][ConfigurableBotConfiguration.coroutineContext]
     * 中不存在自定义的Job，则会直接使用
     * [QQGuildBotManager][love.forte.simbot.component.qguild.bot.QQGuildBotManager]
     * 内的 [Job] 作为 parent Job 。
     *
     */
    public fun register(
        ticket: Bot.Ticket,
        block: ConfigurerFunction<QGBotComponentConfiguration>?,
    ): QGBot

    /**
     * 通过所需信息注册一个bot。
     *
     * 注意，在配置 [ConfigurableBotConfiguration] 时，
     * 如果 [coroutineContext][ConfigurableBotConfiguration.coroutineContext] 中
     * 存在自定义的 [Job][kotlinx.coroutines.Job]，那么 `Application` 中的Job
     * 则会作为一个 `Root Job` 而不是 `Parent Job` 使用。
     *
     * `Root Job` 仅会在其自身完成或关闭的时候**通知**相关联的子类使它们关闭，
     * 但不会有硬性关联，这种通知是通过 [Job.invokeOnCompletion][kotlinx.coroutines.Job.invokeOnCompletion] 实现的，
     * 参考 [Job.linkTo]。
     *
     * 如果 [coroutineContext][ConfigurableBotConfiguration.coroutineContext]
     * 中不存在自定义的Job，则会直接使用
     * [QQGuildBotManager][love.forte.simbot.component.qguild.bot.QQGuildBotManager]
     * 内的 [Job] 作为 parent Job 。
     *
     */
    public fun register(ticket: Bot.Ticket): QGBot = register(ticket, block = null)

    /**
     * [QQGuildBotManager] 的注册工厂。
     */
    public companion object Factory : PluginFactory<QQGuildBotManager, QQGuildBotManagerConfiguration> {
        override val key: PluginFactory.Key = object : PluginFactory.Key {}

        override fun create(
            context: PluginConfigureContext,
            configurer: ConfigurerFunction<QQGuildBotManagerConfiguration>
        ): QQGuildBotManager {
            val component = context.components.find<QQGuildComponent>()
                ?: throw NoSuchComponentException("typeof ${QQGuildComponent::class}")

            val appContext = context.applicationConfiguration.coroutineContext
            val configuration = QQGuildBotManagerConfiguration()
            // init context
            configuration.coroutineContext = appContext.minusKey(Job)
            configuration.invokeBy(configurer)
            // config Job
            val appJob = appContext[Job]
            var job = configuration.coroutineContext[Job]
            if (job == null) {
                job = SupervisorJob(appJob)
                configuration.coroutineContext += job
            } else if (appJob != null) {
                job.linkTo(appJob)
            }

            return QQGuildBotManagerImpl(
                eventDispatcher = context.eventDispatcher,
                configuration = configuration,
                component = component,
                job = job,
                coroutineContext = configuration.coroutineContext.minusKey(Job)
            )
        }
    }
}

/**
 * @suppress
 */
@DslMarker
@Retention(AnnotationRetention.BINARY)
public annotation class QGBotManagerConfigurationDsl


/**
 * [QQGuildBotManager] 使用的配置类描述。
 */
public class QQGuildBotManagerConfiguration {
    /**
     * 当前 botManager 使用的协程上下文。
     * 初始值为 [ApplicationConfiguration] 所提供的上下文。
     *
     * 此上下文会用作每一个构建出来的 [QGBot] 的基础上下文。
     * 如果其中包括 [Job]，则会使用此 [Job] 作为 parent Job.
     * 如果 [ApplicationConfiguration] 也提供了 Job，
     * 则会将产生的 Job [链接][Job.linkTo] 到 application 的 Job 上。
     *
     */
    public var coroutineContext: CoroutineContext = EmptyCoroutineContext

    /**
     * 对所有bot的配置信息进行统一处理的函数。
     * 如果直接对 [botConfigure] 进行赋值，则会覆盖之前的配置。
     *
     * 如果想要以 _追加_ 的形式进行配置，考虑使用同名的函数 [botConfigure].
     *
     * e.g.
     * ```kotlin
     * botConfigure = { appId, secret, token -> /* ... */ }
     * ```
     */
    public var botConfigure: ConfigurableBotConfiguration.(appId: String, secret: String, token: String) -> Unit =
        { _, _, _ -> }

    /**
     * 对所有bot的配置信息进行统一处理的函数。
     * 使用当前函数会保留之前的配置。
     *
     * 如果想直接覆盖配置并抛弃之前的配置，考虑使用同名的属性 [botConfigure].
     *
     * e.g.
     * ```kotlin
     * botConfigure { appId, secret, token -> /* ... */ }
     * ```
     */
    @QGBotManagerConfigurationDsl
    public fun botConfigure(configure: ConfigurableBotConfiguration.(appId: String, secret: String, token: String) -> Unit) {
        botConfigure.also { old ->
            botConfigure = { appId, secret, token ->
                old(appId, secret, token)
                configure(appId, secret, token)
            }
        }
    }
}

/**
 * Provide [QQGuildBotManager.Factory] to SPI
 */
public class QQGuildBotManagerProvider : PluginFactoryProvider<QQGuildBotManagerConfiguration> {
    override fun loadConfigures(): Sequence<QQGuildBotManagerFactoryConfigurerProvider>? =
        loadQQGuildBotManagerConfigurers()

    override fun provide(): PluginFactory<*, QQGuildBotManagerConfiguration> = QQGuildBotManager
}

/**
 * 应用于 [QQGuildBotManagerProvider.configurersLoader] 的额外配置器。
 *
 */
public fun interface QQGuildBotManagerFactoryConfigurerProvider :
    PluginFactoryConfigurerProvider<QQGuildBotManagerConfiguration>


internal expect fun loadQQGuildBotManagerConfigurers(): Sequence<QQGuildBotManagerFactoryConfigurerProvider>?

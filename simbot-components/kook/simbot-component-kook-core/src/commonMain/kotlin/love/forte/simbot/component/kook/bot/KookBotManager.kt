/*
 *     Copyright (c) 2023-2026. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
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

package love.forte.simbot.component.kook.bot

import kotlinx.coroutines.Job
import love.forte.simbot.bot.*
import love.forte.simbot.common.coroutines.mergeWith
import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.function.invokeWith
import love.forte.simbot.common.services.Services
import love.forte.simbot.component.Component
import love.forte.simbot.component.NoSuchComponentException
import love.forte.simbot.component.kook.KookComponent
import love.forte.simbot.component.kook.bot.internal.KookBotManagerImpl
import love.forte.simbot.kook.stdlib.BotConfiguration
import love.forte.simbot.kook.stdlib.Ticket
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.logger.logger
import love.forte.simbot.plugin.PluginConfigureContext
import love.forte.simbot.plugin.PluginFactory
import love.forte.simbot.plugin.PluginFactoryConfigurerProvider
import love.forte.simbot.plugin.PluginFactoryProvider
import kotlin.coroutines.CoroutineContext

/**
 * [KookBot] 注册器。主要由 [KookBotManager] 实现。
 *
 * @author ForteScarlet
 */
public interface KookBotRegistrar {

    /**
     * 通过 [ticket] 和 [configuration] 注册bot。
     *
     * @throws ConflictBotException 已经存在注册的相同 `clientId` 的 bot 时
     */
    public fun register(
        ticket: Ticket,
        configuration: KookBotConfiguration,
    ): KookBot

    /**
     * 通过 [clientId]、 [token] 和 [configuration] 注册 ws bot。
     *
     * @throws ConflictBotException 已经存在注册的相同 `clientId` 的 bot 时
     */
    public fun registerWs(
        clientId: String,
        token: String,
        configuration: KookBotConfiguration,
    ): KookBot = register(Ticket.botWsTicket(clientId, token), configuration)

    /**
     * 通过 [ticket] 和 [block] 注册bot。
     *
     * @throws ConflictBotException 已经存在注册的相同 `clientId` 的 bot 时
     */
    public fun register(
        ticket: Ticket,
        block: KookBotConfiguration.() -> Unit = {},
    ): KookBot

    /**
     * 通过 [clientId]、 [token] 和 [block] 注册 ws bot。
     *
     * @throws ConflictBotException 已经存在注册的相同 `clientId` 的 bot 时
     */
    public fun registerWs(
        clientId: String,
        token: String,
        block: KookBotConfiguration.() -> Unit = {},
    ): KookBot = register(Ticket.botWsTicket(clientId, token), block)
}

/**
 * [KookBot] 的 [BotManager] 实现。
 *
 * @author ForteScarlet
 */
public abstract class KookBotManager : JobBasedBotManager(), KookBotRegistrar {
    protected abstract val component: Component
    protected abstract val coroutineContext: CoroutineContext
    public abstract val configuration: KookBotManagerConfiguration

    override fun configurable(configuration: SerializableBotConfiguration): Boolean =
        configuration is KookBotVerifyInfoConfiguration

    override fun register(configuration: SerializableBotConfiguration): KookBot {
        val config = configuration as? KookBotVerifyInfoConfiguration ?: throw UnsupportedBotConfigurationException(
            configuration::class.toString()
        )

        return register(config.ticket, config::includeConfig)
    }


    override fun register(ticket: Ticket, block: KookBotConfiguration.() -> Unit): KookBot =
        register(ticket, KookBotConfiguration(BotConfiguration()).also(block))


    public companion object Factory : PluginFactory<KookBotManager, KookBotManagerConfiguration> {
        override val key: PluginFactory.Key = object : PluginFactory.Key {}
        private val logger = LoggerFactory.logger<KookBotManager>()

        /**
         * 通过各属性构建 [KookBotManager] 实例。
         */
        override fun create(
            context: PluginConfigureContext,
            configurer: ConfigurerFunction<KookBotManagerConfiguration>
        ): KookBotManager {
            val component =
                context.components.find { it.id == KookComponent.ID_VALUE && it is KookComponent } as? KookComponent
                    ?: throw NoSuchComponentException("Component(id=${KookComponent.ID_VALUE}) type of KookComponent")

            val config = KookBotManagerConfiguration().also {
                it.coroutineContext = context.applicationConfiguration.coroutineContext.minusKey(Job)
                configurer.invokeWith(it)
            }
            // merge config
            config.coroutineContext =
                config.coroutineContext.mergeWith(context.applicationConfiguration.coroutineContext)

            return KookBotManagerImpl(context.eventDispatcher, config, component)
        }
    }
}


/**
 * 实现 [PluginFactoryProvider] 并通过 `Java SPI`
 * 支持 [KookBotManager] 的自动安装。
 */
public class KookBotManagerFactoryProvider :
    PluginFactoryProvider<KookBotManagerConfiguration> {
    override fun loadConfigures(): Sequence<PluginFactoryConfigurerProvider<KookBotManagerConfiguration>> {
        return Services.loadProviders<KookBotManagerFactoryConfigurerProvider>()
            .map { it() } + loadJvmConfigurerProviders()
    }

    override fun provide(): PluginFactory<*, KookBotManagerConfiguration> = KookBotManager
}

/**
 * 用于 [KookBotManagerFactoryProvider.loadConfigures] 中的可自动装配 configuration provider
 */
public fun interface KookBotManagerFactoryConfigurerProvider :
    PluginFactoryConfigurerProvider<KookBotManagerConfiguration>


internal expect fun KookBotManagerFactoryProvider.loadJvmConfigurerProviders(): Sequence<KookBotManagerFactoryConfigurerProvider>

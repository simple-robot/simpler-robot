/*
 *     Copyright (c) 2024-2026. ForteScarlet.
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

package love.forte.simbot.component.onebot.v11.core.bot

import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import love.forte.simbot.bot.*
import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.function.invokeBy
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.services.Services
import love.forte.simbot.common.services.addProviderExceptJvm
import love.forte.simbot.component.NoSuchComponentException
import love.forte.simbot.component.find
import love.forte.simbot.component.onebot.common.annotations.OneBotInternalImplementationsOnly
import love.forte.simbot.component.onebot.v11.core.bot.internal.OneBotBotManagerImpl
import love.forte.simbot.component.onebot.v11.core.component.OneBot11Component
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.plugin.PluginConfigureContext
import love.forte.simbot.plugin.PluginFactory
import love.forte.simbot.plugin.PluginFactoryConfigurerProvider
import love.forte.simbot.plugin.PluginFactoryProvider


/**
 * 一个用于管理 [OneBotBot] 的 [BotManager]。
 *
 * @author ForteScarlet
 */
@OneBotInternalImplementationsOnly
public abstract class OneBotBotManager : BotManager, JobBasedBotManager() {
    abstract override fun all(): Sequence<OneBotBot>
    abstract override fun get(id: ID): OneBotBot
    abstract override fun find(id: ID): OneBotBot?

    /**
     * 检测 [configuration] 的类型是否为 [OneBotBotSerializableConfiguration]。
     */
    override fun configurable(configuration: SerializableBotConfiguration): Boolean =
        configuration is OneBotBotSerializableConfiguration

    /**
     * 使用 [configuration] 构建一个 [OneBotBot]。
     * @throws UnsupportedBotConfigurationException [configuration] 类型不符合预期 [OneBotBotSerializableConfiguration]
     * @throws BotRegisterFailureException 注册过程出现异常
     */
    override fun register(configuration: SerializableBotConfiguration): OneBotBot {
        val obConfig = configuration as? OneBotBotSerializableConfiguration
            ?: throw UnsupportedBotConfigurationException(
                "`configuration` is expected to be an instance of `OneBotBotConfiguration`, " +
                    "but is $configuration (${configuration::class})"
            )

        return register(obConfig.toConfiguration())
    }

    /**
     * 使用 [OneBotBotConfiguration] 构建 [OneBotBot]。
     *
     * @throws BotRegisterFailureException 注册过程出现异常，
     * 比如出现了冲突的 [OneBotBotConfiguration.botUniqueId]
     */
    public abstract fun register(configuration: OneBotBotConfiguration): OneBotBot

    public companion object Factory : BotManagerFactory<OneBotBotManager, OneBotBotManagerConfiguration> {
        init {
            Services.addProviderExceptJvm(PluginFactoryProvider::class) {
                OneBotBotManagerFactoryProvider()
            }
        }

        internal val logger = LoggerFactory.getLogger(
            "love.forte.simbot.component.onebot.v11.core.bot.OneBotBotManager"
        )

        override val key: PluginFactory.Key = object : PluginFactory.Key {}

        override fun create(
            context: PluginConfigureContext,
            configurer: ConfigurerFunction<OneBotBotManagerConfiguration>
        ): OneBotBotManager {
            OneBotBotManagerConfiguration().invokeBy(configurer)

            val component = context.components.find<OneBot11Component>()
                ?: throw NoSuchComponentException("OneBot11Component(id=${OneBot11Component.ID_VALUE})")

            val appContext = context.applicationConfiguration.coroutineContext
            val job = appContext[Job]?.let { SupervisorJob(it) } ?: SupervisorJob()
            val managerContext = appContext.minusKey(Job) + job

            return OneBotBotManagerImpl(
                job,
                managerContext,
                component,
                context.eventDispatcher,
                context.components.serializersModule
            )
        }
    }
}

/**
 * [OneBotBotManager] 的工厂配置类。
 */
public class OneBotBotManagerConfiguration

/**
 * 用于通过 SPI 自动加载 [OneBot11Component] 的 provider。
 */
public class OneBotBotManagerFactoryProvider : PluginFactoryProvider<OneBotBotManagerConfiguration> {
    override fun loadConfigures(): Sequence<OneBotBotManagerFactoryConfigurerProvider> =
        loadOneBotBotManagerConfigures()

    override fun provide(): PluginFactory<*, OneBotBotManagerConfiguration> = OneBotBotManager
}

/**
 * 用于提供额外的 [OneBotBotManagerFactoryProvider] 配置器的 provider，
 * 会在 [OneBotBotManagerFactoryProvider.loadConfigures] 中被加载。
 */
public interface OneBotBotManagerFactoryConfigurerProvider :
    PluginFactoryConfigurerProvider<OneBotBotManagerConfiguration>

internal expect fun loadOneBotBotManagerConfigures(): Sequence<OneBotBotManagerFactoryConfigurerProvider>


/**
 * 根据配置注册一个 [OneBotBot]。
 *
 * @see OneBotBotManager.register
 * @throws BotRegisterFailureException 注册过程出现异常，
 * 比如出现了冲突的 [OneBotBotConfiguration.botUniqueId]
 */
public inline fun OneBotBotManager.register(block: OneBotBotConfiguration.() -> Unit): OneBotBot {
    return register(OneBotBotConfiguration().also(block))
}

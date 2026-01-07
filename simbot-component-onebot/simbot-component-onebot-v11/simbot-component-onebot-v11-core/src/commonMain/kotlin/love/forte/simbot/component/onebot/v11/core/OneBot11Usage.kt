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

package love.forte.simbot.component.onebot.v11.core

import love.forte.simbot.application.Application
import love.forte.simbot.application.ApplicationFactoryConfigurer
import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.function.invokeWith
import love.forte.simbot.component.onebot.v11.core.bot.*
import love.forte.simbot.component.onebot.v11.core.component.OneBot11Component
import love.forte.simbot.component.onebot.v11.core.component.OneBot11ComponentConfiguration
import love.forte.simbot.component.onebot.v11.core.component.useOneBot11Component
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * 同时安装使用 [OneBot11Component] 和 [OneBotBotManager].
 *
 * usage:
 * ```kotlin
 * simbotApplication(Foo) {
 *    useOneBot11()
 *    // 或
 *    useOneBot11 {
 *       component { ... }
 *       botManager { ... }
 *    }
 * }
 * ```
 *
 * 相当于：
 * ```kotlin
 * simbotApplication(Foo) {
 *    install(OneBot11Component) { ... }
 *    install(OneBotBotManager) { ... }
 * }
 * ```
 *
 */
public fun ApplicationFactoryConfigurer<*, *, *>.useOneBot11(builder: OneBot11UsageBuilder.() -> Unit = {}) {
    OneBot11UsageBuilderImpl().also(builder).build(this)
}

/**
 * 为 [OneBot11UsageBuilder] 中的函数染色。
 */
@DslMarker
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
internal annotation class OneBot11UsageBuilderDsl

/**
 * 使用在 [useOneBot11] 函数中，用于同时针对 [OneBot11Component] 和 [OneBotBotManager]
 * 进行配置。
 *
 * @see useOneBot11
 */
public interface OneBot11UsageBuilder {

    /**
     * 追加一个安装 [OneBot11Component] 时候使用的配置。
     */
    @OneBot11UsageBuilderDsl
    public fun component(configurator: ConfigurerFunction<OneBot11ComponentConfiguration>)


    /**
     * 追加一个安装 [OneBotBotManager] 时候使用的配置。
     */
    @OneBot11UsageBuilderDsl
    public fun botManager(configurator: ConfigurerFunction<OneBotBotManagerConfiguration>)

}

private class OneBot11UsageBuilderImpl : OneBot11UsageBuilder {
    private var componentConfigs = mutableListOf<ConfigurerFunction<OneBot11ComponentConfiguration>>()
    private var botManagerConfigs = mutableListOf<ConfigurerFunction<OneBotBotManagerConfiguration>>()

    override fun component(configurator: ConfigurerFunction<OneBot11ComponentConfiguration>) {
        componentConfigs.add(configurator)
    }

    override fun botManager(configurator: ConfigurerFunction<OneBotBotManagerConfiguration>) {
        botManagerConfigs.add(configurator)

    }

    fun build(configurer: ApplicationFactoryConfigurer<*, *, *>) {
        configurer.useOneBot11Component {
            componentConfigs.forEach {
                it.invokeWith(this)
            }
        }
        configurer.useOneBot11BotManager {
            botManagerConfigs.forEach {
                it.invokeWith(this)
            }
        }
    }
}

/**
 * 获取第一个 [OneBotBotManager] 并使用
 * @throws [NoSuchElementException] if no such element is found.
 */
@OptIn(ExperimentalContracts::class)
public inline fun <T> Application.oneBot11Bots(block: OneBotBotManager.() -> T): T {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    return botManagers.firstOneBotBotManager().block()
}

/**
 * 获取第一个 [OneBotBotManager] 并使用（如果有的话）
 */
@OptIn(ExperimentalContracts::class)
public inline fun Application.oneBot11BotsIfSupport(block: OneBotBotManager.() -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    botManagers.firstOneBotBotManagerOrNull()?.block()
}

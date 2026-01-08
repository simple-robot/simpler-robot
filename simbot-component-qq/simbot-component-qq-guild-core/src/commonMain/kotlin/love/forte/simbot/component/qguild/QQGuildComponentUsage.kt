/*
 * Copyright (c) 2022-2024. ForteScarlet.
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

package love.forte.simbot.component.qguild

import love.forte.simbot.application.Application
import love.forte.simbot.application.ApplicationBuilder
import love.forte.simbot.application.ApplicationFactoryConfigurer
import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.function.invokeWith
import love.forte.simbot.component.qguild.bot.QQGuildBotManager
import love.forte.simbot.component.qguild.bot.QQGuildBotManagerConfiguration


/**
 * 在 [ApplicationBuilder] 中安装使用 [QQGuildComponent]。
 *
 * usage:
 * ```kotlin
 * simbotApplication(Foo) {
 *    useQQGuildComponent()
 *    // 或
 *    useQQGuildComponent { ... }
 * }
 * ```
 *
 * 相当于：
 * ```kotlin
 * simbotApplication(Foo) {
 *    install(QQGuildComponent) { ... }
 * }
 * ```
 *
 * @see QQGuildComponent
 *
 */
public fun ApplicationFactoryConfigurer<*, *, *>.useQQGuildComponent(configurator: ConfigurerFunction<QQGuildComponentConfiguration>? = null) {
    if (configurator != null) {
        install(QQGuildComponent, configurator)
    } else {
        install(QQGuildComponent)
    }
}

/**
 * 在 [ApplicationBuilder] 中 **尝试** 安装使用 [QQGuildBotManager]。
 *
 * usage:
 * ```kotlin
 * simbotApplication(Foo) {
 *    useQQGuildBotManager()
 *    // 或
 *    useQQGuildBotManager { ... }
 * }
 * ```
 *
 * 相当于：
 * ```kotlin
 * simbotApplication(Foo) {
 *    install(QQGuildBotManager) { ... }
 * }
 * ```
 * @see QQGuildBotManager
 *
 */
public fun ApplicationFactoryConfigurer<*, *, *>.useQQGuildBotManager(configurator: ConfigurerFunction<QQGuildBotManagerConfiguration>? = null) {
    if (configurator != null) {
        install(QQGuildBotManager, configurator)
    } else {
        install(QQGuildBotManager)
    }
}

/**
 * 同时安装使用 [QQGuildComponent] 和 [QQGuildBotManager].
 *
 * usage:
 * ```kotlin
 * simbotApplication(Foo) {
 *    useQQGuild()
 *    // 或
 *    useQQGuild {
 *       component { ... }
 *       botManager { ... }
 *    }
 * }
 * ```
 *
 * 相当于：
 * ```kotlin
 * simbotApplication(Foo) {
 *    install(QQGuildComponent) { ... }
 *    install(QGBotManager) { ... }
 * }
 * ```
 *
 *
 */
public fun ApplicationFactoryConfigurer<*, *, *>.useQQGuild(builder: QQGuildUsageBuilder.() -> Unit = {}) {
    QQGuildUsageBuilderImpl().also(builder).build(this)
}

/**
 * 使用 [QQGuildBotManager]
 *
 * @throws NoSuchElementException 如果不存在
 */
public inline fun <A : Application> A.qqGuildBots(block: QQGuildBotManager.() -> Unit) {
    botManagers.firstQQGuildBotManager().also(block)
}


/**
 * 为 [QQGuildUsageBuilder] 中的函数染色。
 */
@DslMarker
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
internal annotation class QQGuildUsageBuilderDsl


/**
 * 使用在 [useQQGuild] 函数中，用于同时针对 [QQGuildComponent] 和 [QQGuildBotManager]
 * 进行配置。
 *
 * @see useQQGuild
 */
public interface QQGuildUsageBuilder {

    /**
     * 追加一个安装 [QQGuildComponent] 时候使用的配置。
     */
    @QQGuildUsageBuilderDsl
    public fun component(configurator: ConfigurerFunction<QQGuildComponentConfiguration>)


    /**
     * 追加一个安装 [QQGuildBotManager] 时候使用的配置。
     */
    @QQGuildUsageBuilderDsl
    public fun botManager(configurator: ConfigurerFunction<QQGuildBotManagerConfiguration>)

}


private class QQGuildUsageBuilderImpl : QQGuildUsageBuilder {
    private var componentConfigs = mutableListOf<ConfigurerFunction<QQGuildComponentConfiguration>>()
    private var botManagerConfigs = mutableListOf<ConfigurerFunction<QQGuildBotManagerConfiguration>>()

    override fun component(configurator: ConfigurerFunction<QQGuildComponentConfiguration>) {
        componentConfigs.add(configurator)
    }

    override fun botManager(configurator: ConfigurerFunction<QQGuildBotManagerConfiguration>) {
        botManagerConfigs.add(configurator)

    }

    fun build(configurer: ApplicationFactoryConfigurer<*, *, *>) {
        configurer.useQQGuildComponent {
            componentConfigs.forEach {
                it.invokeWith(this)
            }
        }
        configurer.useQQGuildBotManager {
            botManagerConfigs.forEach {
                it.invokeWith(this)
            }
        }
    }


}

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

package love.forte.simbot.component.kook

import love.forte.simbot.application.*
import love.forte.simbot.component.kook.bot.KookBotManager
import love.forte.simbot.component.kook.bot.KookBotManagerConfiguration


/**
 * 注册 [KOOK组件][KookComponent] 信息。
 *
 * usage:
 * ```kotlin
 * launchApplication(Foo) {
 *  useKookComponent()
 *  // 或
 *  useKookComponent { ... }
 * }
 * ```
 *
 *
 * 相当于：
 * ```kotlin
 * launchApplication(Foo) {
 *    install(KookComponent) { ... }
 * }
 * ```
 *
 * @see KookComponent
 */
@ApplicationFactoryConfigurerDSL
public inline fun ApplicationFactoryConfigurer<*, *, *>.useKookComponent(
    crossinline configurator: KookComponentConfiguration.() -> Unit = {
    }
) {
    install(KookComponent) { configurator() }
}


/**
 * 注册使用 [KookBotManager]。
 *
 * usage:
 * ```kotlin
 * launchApplication(Foo) {
 *    useKookBotManager()
 *    // 或
 *    useKookBotManager { ... }
 * }
 * ```
 *
 * 相当于:
 * ```kotlin
 * launchApplication(Foo) {
 *    install(KookBotManager) { ... }
 * }
 * ```
 *
 *
 *
 * @see KookBotManager
 */

@ApplicationFactoryConfigurerDSL
public inline fun ApplicationFactoryConfigurer<*, *, *>.useKookBotManager(
    crossinline configurator: KookBotManagerConfiguration.() -> Unit = {
    }
) {
    install(KookBotManager) { configurator() }
}

/**
 * 通过 [KookComponentUsageBuilder] 来同时配置使用
 * [KOOK组件][KookComponent] 和 [KookBotManager]。
 *
 * usage:
 * ```kotlin
 * simbotApplication(Foo) {
 *    useKook()
 *    // 或:
 *    useKook {
 *        // 配置组件
 *        component {
 *            // ...
 *        }
 *        // 配置botManager
 *        botManager {
 *            // ...
 *        }
 *    }
 * }
 * ```
 *
 *
 * 相当于:
 * ```kotlin
 * simbotApplication(Foo) {
 *    install(KookComponent) {
 *        // ...
 *    }
 *
 *    install(KookBotManager) {
 *        // ...
 *    }
 * }
 * ```
 */

public fun ApplicationFactoryConfigurer<*, *, *>.useKook(usageBuilder: KookComponentUsageBuilder.() -> Unit = {}) {
    KookComponentUsageBuilderImpl().also(usageBuilder).use(this)
}

/**
 * 同时配置 [KOOK组件][KookComponent] 和 [KookBotManager] 的构建器。
 *
 * 应用于 [useKook] 中。
 *
 * @see useKook
 *
 */
@ApplicationFactoryConfigurerDSL
public interface KookComponentUsageBuilder {
    /**
     * 提供针对 [KOOK组件][KookComponent] 的配置。
     *
     */
    @ApplicationFactoryConfigurerDSL
    public fun component(configurator: KookComponentConfiguration.() -> Unit)

    /**
     * 提供针对 [KookBotManager] 的配置。
     */
    @ApplicationFactoryConfigurerDSL
    public fun botManager(configurator: KookBotManagerConfiguration.() -> Unit)

}


private class KookComponentUsageBuilderImpl : KookComponentUsageBuilder {
    private var compConf: (KookComponentConfiguration.() -> Unit) = {}
    private var bmConf: (KookBotManagerConfiguration.() -> Unit) = {}

    override fun component(configurator: KookComponentConfiguration.() -> Unit) {
        compConf.also { old ->
            compConf = {
                old()
                configurator()
            }
        }
    }


    override fun botManager(configurator: KookBotManagerConfiguration.() -> Unit) {
        bmConf.also { old ->
            bmConf = {
                old()
                configurator()
            }
        }
    }

    fun use(b: ApplicationFactoryConfigurer<*, *, *>) {
        b.useKookComponent(compConf)
        b.useKookBotManager(bmConf)
    }
}




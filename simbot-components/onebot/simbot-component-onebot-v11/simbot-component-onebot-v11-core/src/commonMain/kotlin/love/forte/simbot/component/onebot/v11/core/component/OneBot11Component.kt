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

package love.forte.simbot.component.onebot.v11.core.component

import kotlinx.serialization.modules.SerializersModule
import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.function.invokeWith
import love.forte.simbot.common.services.Services
import love.forte.simbot.common.services.addProviderExceptJvm
import love.forte.simbot.component.ComponentConfigureContext
import love.forte.simbot.component.ComponentFactory
import love.forte.simbot.component.ComponentFactoryConfigurerProvider
import love.forte.simbot.component.ComponentFactoryProvider
import love.forte.simbot.component.onebot.common.component.OneBotComponent
import love.forte.simbot.component.onebot.v11.core.OneBot11
import kotlin.jvm.JvmField


/**
 * 一个 OneBot11 组件的组件标识。
 *
 * @author ForteScarlet
 */
public class OneBot11Component : OneBotComponent {
    override val id: String
        get() = ID_VALUE

    override val serializersModule: SerializersModule
        get() = SerializersModule

    public companion object Factory : ComponentFactory<OneBot11Component, OneBot11ComponentConfiguration> {
        init {
            // Fix https://github.com/simple-robot/simpler-robot/pull/833
            Services.addProviderExceptJvm(ComponentFactoryProvider::class) {
                OneBot11ComponentFactoryProvider()
            }
        }

        public const val ID_VALUE: String = "simbot.onebot11"

        @JvmField
        public val SerializersModule: SerializersModule = OneBot11.serializersModule

        override val key: ComponentFactory.Key = object : ComponentFactory.Key {}

        override fun create(
            context: ComponentConfigureContext,
            configurer: ConfigurerFunction<OneBot11ComponentConfiguration>
        ): OneBot11Component {
            configurer.invokeWith(OneBot11ComponentConfiguration())
            return OneBot11Component()
        }
    }
}

/**
 * [OneBot11Component.Factory] 使用的配置类。
 */
public class OneBot11ComponentConfiguration

/**
 * 用于通过 SPI 自动加载 [OneBot11Component] 的 provider。
 */
public class OneBot11ComponentFactoryProvider : ComponentFactoryProvider<OneBot11ComponentConfiguration> {
    override fun loadConfigures(): Sequence<OneBot11ComponentFactoryConfigurerProvider> =
        loadOneBot11ComponentConfigures()

    override fun provide(): ComponentFactory<*, OneBot11ComponentConfiguration> = OneBot11Component
}

/**
 * 用于提供额外的 [OneBot11ComponentFactoryProvider] 配置器的 provider，
 * 会在 [OneBot11ComponentFactoryProvider.loadConfigures] 中被加载。
 */
public interface OneBot11ComponentFactoryConfigurerProvider :
    ComponentFactoryConfigurerProvider<OneBot11ComponentConfiguration>

internal expect fun loadOneBot11ComponentConfigures(): Sequence<OneBot11ComponentFactoryConfigurerProvider>

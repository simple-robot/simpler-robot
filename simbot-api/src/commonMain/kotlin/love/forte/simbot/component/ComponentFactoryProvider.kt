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

@file:JvmName("ComponentFactoryProviders")
@file:JvmMultifileClass

package love.forte.simbot.component

import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.function.invokeWith
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlin.jvm.JvmSynthetic

/**
 * 用于支持自动加载 [ComponentFactory] 的 SPI 接口。
 *
 * @author ForteScarlet
 */
public interface ComponentFactoryProvider<CONF : Any> {
    /**
     * 得到提供的 [ComponentFactory] 实例。
     */
    public fun provide(): ComponentFactory<*, CONF>

    /**
     * 提供额外配置类的类型用于一些可自动加载的加载器的加载结果。
     * 如果返回 `null` 则代表不提供、不加载可自动加载的额外配置类型。
     *
     * 在 JVM 中使用 `ServiceLoader` 实现。
     */
    public fun loadConfigurers(): Sequence<ComponentFactoryConfigurerProvider<CONF>>?
}

/**
 * 用于在加载 [ComponentFactoryProvider] 后、构建对应的 [Component] 时，
 * 作为自动加载的额外配置类型的 SPI，
 * 在使用 `loadComponentFactoriesFromProviders` （JVM平台下） 或其他衍生函数
 * 且参数 `loadConfigurers` 为 `true` 时会被自动加载并作为构建 [Component] 的前置配置逻辑。
 *
 * @author ForteScarlet
 */
public interface ComponentFactoryConfigurerProvider<CONF : Any> {
    /**
     * 处理配置
     */
    public fun configure(config: CONF)
}


internal class ProviderComponentFactory<COM : Component, CONF : Any>(
    private val factory: ComponentFactory<COM, CONF>,
    private val configurers: List<ComponentFactoryConfigurerProvider<CONF>>
) : ComponentFactory<COM, CONF> {
    override val key: ComponentFactory.Key
        get() = factory.key

    override fun create(context: ComponentConfigureContext, configurer: ConfigurerFunction<CONF>): COM {
        return factory.create(context) {
            configurer.invokeWith(this)
            configurers.forEach {
                it.configure(this)
            }
        }
    }
}

/**
 * 添加一个用于获取 [ComponentFactoryProvider] 的函数。
 * 这是用于兼容在非 `JVM` 平台下没有 `ServiceLoader` 的方案，
 * 在 `JVM` 中应直接使用 `ServiceLoader` 加载 SPI 的方式，
 * 因此 [addProvider] 实际上对 JVM （或者说Java）隐藏。
 * 但是如果使用 Kotlin 或其他手段强行添加结果，[loadComponentProviders]
 * 也还是会得到这些结果的。
 */
@JvmSynthetic
public expect fun addProvider(providerCreator: () -> ComponentFactoryProvider<*>)

/**
 * 清理所有通过 [addProvider] 添加的 provider 构建器。
 */
@JvmSynthetic
public expect fun clearProviders()

/**
 * 尝试自动加载环境中可获取的所有 [ComponentFactoryProvider] 实例。
 * 在 `JVM` 平台下通过 `ServiceLoader` 加载 [ComponentFactoryProvider] 并得到结果，
 * 而在其他平台则会得到预先从 [addProvider] 中添加的所有函数构建出来的结果。
 *
 */
public expect fun loadComponentProviders(): Sequence<ComponentFactoryProvider<*>>

/**
 * 通过 [loadComponentProviders] 加载 [ComponentFactoryProvider] 并得到流结果。
 */
public fun loadComponentFactoriesFromProviders(loadConfigurers: Boolean): Sequence<ComponentFactory<*, *>> {
    return loadComponentProviders().map { it.loadConfigurersAndToPlugin(loadConfigurers) }
}

/**
 * 通过 [loadComponentFactoriesFromProviders] 加载并安装所有可寻得的组件。
 *
 * @param loadConfigurers 是否同时加载所有可用的前置配置
 */
public fun ComponentInstaller.findAndInstallAllComponents(loadConfigurers: Boolean) {
    loadComponentFactoriesFromProviders(loadConfigurers).forEach { factory ->
        install(factory)
    }
}


internal fun <C : Any> ComponentFactoryProvider<C>.loadConfigurersAndToPlugin(
    loadConfigurers: Boolean
): ComponentFactory<*, C> {
    val factory = provide()
    if (!loadConfigurers) {
        return factory
    }

    val loader = loadConfigurers() ?: return factory
    val configurerList = loader.toList()

    return ProviderComponentFactory(factory, configurerList)
}

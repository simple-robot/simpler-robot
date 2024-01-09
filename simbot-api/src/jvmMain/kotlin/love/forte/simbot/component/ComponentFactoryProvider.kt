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

package love.forte.simbot.component

import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.function.invokeWith
import love.forte.simbot.plugin.loadPluginFactoriesFromProviders
import java.util.*
import java.util.stream.Stream


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
     * 提供额外配置类的类型用于一些可自动加载的加载器。
     * 如果返回 `null` 则代表不提供、不加载可自动加载的额外配置类型。
     *
     * 这些加载器的类型建议由 [ComponentFactory] 的实现者提供，
     * 因此 [ServiceLoader] 也需要由实现者直接提供。
     */
    public fun configurersLoader(): ServiceLoader<out ComponentFactoryConfigurerProvider<CONF>>?
}

private class ProviderComponentFactory<COM : Component, CONF : Any>(
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
 * 用于在加载 [ComponentFactoryProvider] 后、构建对应的 [Component] 时，
 * 作为自动加载的额外配置类型的 SPI，
 * 在使用 [loadComponentFactoriesFromProviders] （或其他衍生函数）
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


/**
 * 通过 [ServiceLoader] 加载 [ComponentFactoryProvider] 并得到流结果。
 */
public fun loadComponentProviders(loader: ClassLoader): Stream<ComponentFactoryProvider<*>> {
    return ServiceLoader.load(ComponentFactoryProvider::class.java, loader)
        .stream().map { it.get() }
}

/**
 * 通过 [ServiceLoader] 加载 [ComponentFactoryProvider] 并得到流结果。
 */
public fun loadComponentProviders(): Stream<ComponentFactoryProvider<*>> {
    return ServiceLoader.load(ComponentFactoryProvider::class.java)
        .stream().map { it.get() }
}

/**
 * 通过 [ServiceLoader] 加载 [ComponentFactoryProvider] 并得到流结果。
 */
public fun loadComponentFactoriesFromProviders(
    loader: ClassLoader,
    loadConfigurers: Boolean
): Stream<ComponentFactory<*, *>> {
    return loadComponentProviders(loader).map { it.loadConfigurersAndToPlugin(loadConfigurers) }
}

/**
 * 通过 [ServiceLoader] 加载 [ComponentFactoryProvider] 并得到流结果。
 */
public fun loadComponentFactoriesFromProviders(loadConfigurers: Boolean): Stream<ComponentFactory<*, *>> {
    return loadComponentProviders().map { it.loadConfigurersAndToPlugin(loadConfigurers) }
}

/**
 * 通过 [loadPluginFactoriesFromProviders] 加载并安装所有可寻得的组件。
 *
 * @param loadConfigurers 是否同时加载所有可用的前置配置
 */
public fun ComponentInstaller.findAnyInstallAllPlugins(loadConfigurers: Boolean) {
    loadComponentFactoriesFromProviders(loadConfigurers).forEach { factory ->
        install(factory)
    }
}

private fun <C : Any> ComponentFactoryProvider<C>.loadConfigurersAndToPlugin(
    loadConfigurers: Boolean
): ComponentFactory<*, C> {
    val factory = provide()
    if (!loadConfigurers) {
        return factory
    }

    val loader = configurersLoader() ?: return factory
    val configurerList = loader.stream().map { it.get() }.toList()

    return ProviderComponentFactory(factory, configurerList)
}

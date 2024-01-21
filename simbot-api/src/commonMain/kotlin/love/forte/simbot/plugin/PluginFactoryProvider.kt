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

@file:JvmName("PluginFactoryProviders")
@file:JvmMultifileClass

package love.forte.simbot.plugin

import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.function.invokeWith
import love.forte.simbot.common.services.Services
import love.forte.simbot.component.addProvider
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

/**
 * 用于支持自动加载 [PluginFactory] 的 SPI 接口。
 *
 * @author ForteScarlet
 */
public interface PluginFactoryProvider<CONF : Any> {
    /**
     * 得到提供的 [PluginFactory] 实例。
     */
    public fun provide(): PluginFactory<*, CONF>

    /**
     * 提供额外配置类的类型用于一些可自动加载的加载器。
     * 如果返回 `null` 则代表不提供、不加载可自动加载的额外配置类型。
     */
    public fun loadConfigures(): Sequence<PluginFactoryConfigurerProvider<CONF>>? = null

    /**
     * @suppress 命名错误
     */
    @Deprecated("Use 'loadConfigures'", level = DeprecationLevel.ERROR, replaceWith = ReplaceWith("loadConfigures()"))
    public fun configurersLoader(): Sequence<PluginFactoryConfigurerProvider<CONF>>? = loadConfigures()
}

internal class ProviderPluginFactory<P : Plugin, CONF : Any>(
    private val factory: PluginFactory<P, CONF>,
    private val configurers: List<PluginFactoryConfigurerProvider<CONF>>
) : PluginFactory<P, CONF> {
    override val key: PluginFactory.Key
        get() = factory.key

    override fun create(context: PluginConfigureContext, configurer: ConfigurerFunction<CONF>): P {
        return factory.create(context) {
            configurer.invokeWith(this)
            configurers.forEach {
                it.configure(this)
            }
        }
    }
}

/**
 * 用于在加载 [PluginFactoryProvider] 后、构建对应的 [Plugin] 时，
 * 作为自动加载的额外配置类型的 SPI，
 * 在使用 [loadPluginFactoriesFromProviders] （或其他衍生函数）
 * 且参数 `loadConfigurers` 为 `true` 时会被自动加载并作为构建 [Plugin] 的前置配置逻辑。
 *
 * @author ForteScarlet
 */
public interface PluginFactoryConfigurerProvider<CONF : Any> {
    /**
     * 处理配置
     */
    public fun configure(config: CONF)
}

/**
 * 添加一个用于获取 [PluginFactoryProvider] 的函数。
 * 这是用于兼容在非 `JVM` 平台下没有 `ServiceLoader` 的方案，
 * 在 `JVM` 中应直接使用 `ServiceLoader` 加载 SPI 的方式，
 * 但是如果使用 [addProvider] 强行添加结果，[loadPluginProviders]
 * 也还是会得到这些结果的。
 */
public fun addProvider(providerCreator: () -> PluginFactoryProvider<*>) {
    Services.addProvider<PluginFactoryProvider<*>>(providerCreator)
}

/**
 * 清理所有通过 [addProvider] 添加的 provider 构建器。
 */
public fun clearProviders() {
    Services.clearProviders<PluginFactoryProvider<*>>()
}

/**
 * 尝试自动加载环境中可获取的所有 [PluginFactoryProvider] 实例。
 * 在 `JVM` 平台下通过 `ServiceLoader` 加载 [PluginFactoryProvider] 并得到结果，
 * 而在其他平台则会得到预先从 [addProvider] 中添加的所有函数构建出来的结果。
 */
public expect fun loadPluginProviders(): Sequence<PluginFactoryProvider<*>>

/**
 * 通过 [loadPluginProviders] 加载 [PluginFactoryProvider] 并得到流结果。
 */
public fun loadPluginFactoriesFromProviders(loadConfigurers: Boolean): Sequence<PluginFactory<*, *>> {
    return loadPluginProviders().map { it.loadConfigurersAndToPlugin(loadConfigurers) }
}

/**
 * 通过 [loadPluginFactoriesFromProviders] 加载并安装所有可寻得的组件。
 *
 * @param loadConfigurers 是否同时加载所有可用的前置配置
 */
public fun PluginInstaller.findAndInstallAllPlugins(loadConfigurers: Boolean) {
    loadPluginFactoriesFromProviders(loadConfigurers).forEach { factory ->
        install(factory)
    }
}

internal fun <C : Any> PluginFactoryProvider<C>.loadConfigurersAndToPlugin(
    loadConfigures: Boolean
): PluginFactory<*, C> {
    val factory = provide()
    if (!loadConfigures) {
        return factory
    }

    val loader = loadConfigures() ?: return factory
    val configurerList = loader.toList()

    return ProviderPluginFactory(factory, configurerList)
}

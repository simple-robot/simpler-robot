/*
 *     Copyright (c) 2024. ForteScarlet.
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

package love.forte.simbot.application

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import love.forte.simbot.common.async.Async
import love.forte.simbot.common.async.toAsync
import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.function.invokeWith
import love.forte.simbot.component.*
import love.forte.simbot.event.EventDispatcherConfiguration
import love.forte.simbot.plugin.*
import kotlin.jvm.JvmSynthetic


/**
 * 用于构建一个 [ApplicationFactory] 的工厂。
 *
 * [ApplicationFactory] 通常应以常量形式实现，例如一个 Kotlin object。
 *
 * @param A 此工厂最终会构建的 [Application] 实现类型
 * @param C 配置工厂信息的配置器类型
 * @param L 配置阶段结束后、启动 [Application] 的启动器。
 *
 * @see Application
 *
 * @author ForteScarlet
 */
public interface ApplicationFactory<
    out A : Application,
    C : ApplicationBuilder,
    L : ApplicationLauncher<A>,
    AER : ApplicationEventRegistrar,
    DC : EventDispatcherConfiguration
    > {

    /**
     * 通过配置逻辑 [configurer] 构建一个预处理启动器 [ApplicationLauncher]。
     *
     * ```kotlin
     * create {
     *    config {
     *      ...
     *    }
     *    install(...) {
     *      ...
     *    }
     * }
     * ```
     *
     */
    public fun create(configurer: ConfigurerFunction<ApplicationFactoryConfigurer<C, AER, DC>>?): L

    /**
     * 构建一个预处理启动器 [ApplicationLauncher]。
     */
    public fun create(): L = create(null)
}

/**
 * DSL marker for [ApplicationFactoryConfigurer]
 */
@Retention(AnnotationRetention.BINARY)
@DslMarker
public annotation class ApplicationFactoryConfigurerDSL

/**
 *
 * 构建 [Application] 配置阶段的 DSL API 配置器。
 *
 * ```kotlin
 * ApplicationFactory.create {
 *     config {
 *         ...
 *     }
 *     eventDispatcher(FACTORY?) {
 *         ...
 *     }
 *     install(Components or Plugins) {
 *         // ...
 *     }
 * }
 * ```
 *
 * @see AbstractApplicationFactoryConfigurer
 */
public interface ApplicationFactoryConfigurer<
    C : ApplicationBuilder,
    AER : ApplicationEventRegistrar,
    DC : EventDispatcherConfiguration
    > : ComponentInstaller, PluginInstaller {

    /**
     * 配置 [Application] 配置阶段的一些配置信息。
     */
    @ApplicationFactoryConfigurerDSL
    public fun config(configurer: ConfigurerFunction<C>)

    /**
     * 配置 [Application] 的阶段事件。
     */
    @ApplicationFactoryConfigurerDSL
    public fun stageEvents(configurer: ConfigurerFunction<AER>)

    /**
     * 添加一个事件调度器配置。
     */
    @ApplicationFactoryConfigurerDSL
    public fun eventDispatcher(configurer: ConfigurerFunction<DC>)

    /**
     * 注册安装一个组件类型，并为其添加对应的配置。
     */
    @ApplicationFactoryConfigurerDSL
    override fun <COM : Component, CONF : Any> install(
        componentFactory: ComponentFactory<COM, CONF>, configurer: ConfigurerFunction<CONF>
    )

    /**
     * 注册安装一个组件类型。
     */
    @ApplicationFactoryConfigurerDSL
    override fun <COM : Component, CONF : Any> install(componentFactory: ComponentFactory<COM, CONF>) {
        install(componentFactory) {}
    }

    /**
     * 注册安装一个插件 [Plugin] 类型，并为其添加一个对应的配置。
     */
    @ApplicationFactoryConfigurerDSL
    override fun <P : Plugin, CONF : Any> install(
        pluginFactory: PluginFactory<P, CONF>, configurer: ConfigurerFunction<CONF>
    )

    /**
     * 注册安装一个插件 [Plugin] 类型。
     */
    @ApplicationFactoryConfigurerDSL
    override fun <P : Plugin, CONF : Any> install(pluginFactory: PluginFactory<P, CONF>) {
        install(pluginFactory) {}
    }
}

/**
 * [ApplicationFactoryConfigurer] 的基础抽象实现，提供大部分DSL配置能力的简单实现。
 *
 * @see ApplicationFactoryConfigurer
 */
public abstract class AbstractApplicationFactoryConfigurer<
    C : ApplicationBuilder,
    AER : ApplicationEventRegistrar,
    DC : EventDispatcherConfiguration
    >(
    protected open val configConfigurers: MutableList<ConfigurerFunction<C>> = mutableListOf(),
    protected open val applicationEventRegistrarConfigurations: MutableList<ConfigurerFunction<AER>> = mutableListOf(),
    protected open val eventDispatcherConfigurers: MutableList<ConfigurerFunction<DC>> = mutableListOf(),
    protected open val componentFactoriesConfigurator: ComponentFactoriesConfigurator =
        ComponentFactoriesConfigurator(),

    protected open val pluginFactoriesConfigurator: PluginFactoriesConfigurator = PluginFactoriesConfigurator(),
) : ApplicationFactoryConfigurer<C, AER, DC> {
    override fun config(configurer: ConfigurerFunction<C>) {
        configConfigurers.add(configurer)
    }

    override fun stageEvents(configurer: ConfigurerFunction<AER>) {
        applicationEventRegistrarConfigurations.add(configurer)
    }

    override fun eventDispatcher(configurer: ConfigurerFunction<DC>) {
        eventDispatcherConfigurers.add(configurer)
    }

    override fun <COM : Component, CONF : Any> install(
        componentFactory: ComponentFactory<COM, CONF>, configurer: ConfigurerFunction<CONF>
    ) {
        componentFactoriesConfigurator.add(componentFactory, configurer)
    }

    override fun <P : Plugin, CONF : Any> install(
        pluginFactory: PluginFactory<P, CONF>, configurer: ConfigurerFunction<CONF>
    ) {
        pluginFactoriesConfigurator.add(pluginFactory, configurer)
    }

    protected inline fun <AC : ApplicationConfiguration> createConfig(
        configBuilder: C, afterConfig: (C) -> AC
    ): AC {
        configConfigurers.forEach { it.invokeWith(configBuilder) }
        return afterConfig(configBuilder)
    }

    protected open fun createAllComponents(context: ComponentConfigureContext): List<Component> {
        return componentFactoriesConfigurator.createAll(context)
    }

    protected open fun createAllPlugins(context: PluginConfigureContext): List<Plugin> {
        return pluginFactoriesConfigurator.createAll(context)
    }
}

/**
 * [Application] 的预处理启动器。
 * 当执行 [ApplicationLauncher.launch] 时会构建并启动 [Application]。
 *
 * JVM 中提供了一些额外的扩展类型来支持使用 Java 的阻塞或异步风格 API 实现 [ApplicationLauncher]:
 * - [JBlockingApplicationLauncher][love.forte.simbot.application.JBlockingApplicationLauncher]
 * - [JAsyncApplicationLauncher][love.forte.simbot.application.JAsyncApplicationLauncher]
 *
 * @see love.forte.simbot.application.JBlockingApplicationLauncher
 * @see love.forte.simbot.application.JAsyncApplicationLauncher
 */
public interface ApplicationLauncher<out A : Application> {

    /**
     * 根据已经成型的配置，构建并启动一个 [Application][A]。
     * 启动时会触发 [ApplicationLaunchStage.Launch] 事件线，调用所有的启动事件并集此启动所有的组件或插件。
     */
    @JvmSynthetic
    public suspend fun launch(): A

    /**
     * 根据已经成型的配置，构建并在异步中启动一个 [Application][A]。
     * 启动时会触发 [ApplicationLaunchStage.Launch] 事件线，调用所有的启动事件并集此启动所有的组件或插件。
     */
    public fun launchIn(scope: CoroutineScope): Async<A> = scope.toAsync { launch() }

    /**
     * 根据已经成型的配置，构建并在异步中启动一个 [Application][A]。
     * 启动时会触发 [ApplicationLaunchStage.Launch] 事件线，调用所有的启动事件并集此启动所有的组件或插件。
     * 注意：会使用 [GlobalScope] 作为作用域并不传播 [DelicateCoroutinesApi] 警告。
     * 对于此作用域的说明、限制或差异等详细描述请参考 [GlobalScope] 的文档说明。
     */
    @OptIn(DelicateCoroutinesApi::class)
    public fun launchInGlobal(): Async<A> = GlobalScope.toAsync { launch() }
}


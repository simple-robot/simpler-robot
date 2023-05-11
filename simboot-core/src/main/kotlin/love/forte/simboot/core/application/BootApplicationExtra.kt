/*
 * Copyright (c) 2022-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

@file:JvmName("BootApplications")

package love.forte.simboot.core.application

import love.forte.simbot.application.*


/**
 * 通过 [createSimbotApplication] 并使用 [Boot] 作为 Application Factory 来构建一个 [BootApplication].
 *
 * 相当于 `createSimbotApplication(Boot, configurator, builder)`。
 *
 * e.g.
 * ```kotlin
 * createBootApplication({
 *      // config...
 * }) {
 *  // build...
 * }
 *
 * ```
 * @see Boot
 * @see BootApplication
 *
 */
public suspend fun createBootApplication(
    configurator: BootApplicationConfiguration.() -> Unit = {},
    builder: BootApplicationBuilder.(BootApplicationConfiguration) -> Unit = {},
): BootApplication {
    return createSimbotApplication(Boot, configurator, builder)
}

/**
 * 通过 [simbotApplication] 并使用 [Boot] 作为 Application Factory 来构建一个 [BootApplication] 的 [ApplicationLauncher].
 *
 * 相当于 `simbotApplication(Boot, configurator, builder)`。
 *
 * e.g.
 * ```kotlin
 * bootApplication({
 *      // config...
 * }) {
 *  // build...
 * }
 *
 * ```
 * @see Boot
 * @see BootApplication
 *
 */
public fun bootApplication(
    configurator: BootApplicationConfiguration.() -> Unit = {},
    builder: BootApplicationBuilder.(BootApplicationConfiguration) -> Unit = {},
): ApplicationLauncher<BootApplication> {
    return simbotApplication(Boot, configurator, builder)
}


/**
 * 通过 [buildSimbotApplication] 来提供 `DSL` 风格的方式来配置 [BootApplication].
 *
 * 相当于使用 `simbotApplication(Boot, {...}) { ... }`。
 *
 * e.g.
 * ```kotlin
 * buildBootApplication {
 *  config { // this: BootApplicationConfiguration
 *      args = listOf("-a", "-b")
 *      args("-foo", "-bar")
 *
 *      classLoader = ...
 *
 *      // 此处配置需要自动扫描并加载的 *.bot 配置文件的glob列表。
 *      botScanResources = listOf("simbot-bots/ *.bot*")
 *
 *      // 此处可以配置一些不在资源目录中无法被扫描的额外的bot配置资源。
 *      botResources = listOf(Path("foo/myBot.bot.json").toResource())
 *
 *      // Boot 模块下支持简易的依赖管理。
 *      // 此处配置需要自动扫描bean的所有包路径。
 *      classesScanPackage = listOf("com.example.foo", "com.example.bar")
 *
 *      // 此处配置Kotlin中顶层监听函数的扫描包路径
 *      topLevelListenerScanPackage = listOf("com.example.foo", "com.example.bar")
 *
 *      // 此处配置Kotlin中顶层binder函数的扫描路径
 *      topLevelBinderScanPackage = listOf("com.example.foo", "com.example.bar")
 *
 *      // 是否在 application 启动成功后自动启动所有注册成功的bot。
 *      isAutoStartBots = true
 *  }
 *
 *  build { // this: BootApplicationBuilder, it: BootApplicationConfiguration
 *     // 加载你需要的组件或者事件提供者。
 *     // 如果你不知道应该添加什么，可以尝试使用 installXxxAll
 *     installAllComponents()
 *     installAllEventProviders()
 *
 *     // 通常情况下，各组件会提供一些简化操作的扩展函数，例如
 *     // useFoo { ... }
 *     // 这个函数很大概率等同于同时使用：
 *     // install(FooComponent) { ... }
 *     // install(FooBotManager) { ... }
 *
 *
 *     // Boot 模块下支持简易的依赖管理。
 *     // 此处可以通过 beans DSL 额外配置其他需要进行管理的。
 *     beans {
 *         // ...
 *     }
 *
 *     // 下述中，listener、interceptor、binder都支持从已注册的bean中自动扫描，
 *     // 此处只需要配置在beans之外 **额外的** 内容。
 *
 *
 *     // 额外的监听配置
 *     eventProcessor {
 *         coroutineContext
 *
 *         listeners {
 *             // ...
 *         }
 *         interceptors {
 *             // ...
 *         }
 *     }
 *     // 或者
 *     listeners {
 *         // ...
 *     }
 *
 *
 *     // 额外的binder配置
 *     binders {
 *         // ...
 *     }
 *
 *     // 额外的 bot 配置
 *     // 手动额外注册一些bot. 如果有更清晰的 botManager, 那么更建议使用明确的botManager进行注册。
 *     // 明确的注册可以参考下述的 onCompletion 中的示例。
 *     bots {
 *         //
 *     }
 *
 *     // 当 application 构建完成后执行的逻辑。
 *     // 比如在明确的botManager中注册bot。
 *     onCompletion {  application ->
 *         val botManager = application.botManagers.firstOrNull { botManager -> botManager is FooBotManager } as? FooBotManager
 *             ?: return@onCompletion
 *
 *       botManager.register(code, password) { ... }
 *     }
 *   }
 * }
 *
 *
 *
 * ```
 * @see Boot
 * @see BootApplication
 *
 */
@JvmSynthetic
@ApplicationDslBuilderDsl
public suspend fun buildBootApplication(block: ApplicationDslBuilder<BootApplicationConfiguration, BootApplicationBuilder, BootApplication>.() -> Unit = {}): BootApplication {
    return buildSimbotApplication(Boot, block)
}

/**
 * 通过 [buildSimbotApplication] 来提供 `DSL` 风格的方式来配置 [BootApplication].
 *
 * 相当于使用 `simbotApplication(Boot, {...}) { ... }`。
 *
 * @see Boot
 * @see BootApplication
 *
 */
@ApplicationDslBuilderDsl
public fun buildBootApplicationLauncher(block: ApplicationDslBuilder<BootApplicationConfiguration, BootApplicationBuilder, BootApplication>.() -> Unit = {}): ApplicationLauncher<BootApplication> {
    return buildSimbotApplicationLauncher(Boot, block)
}

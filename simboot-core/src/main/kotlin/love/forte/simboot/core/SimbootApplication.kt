/*
 * Copyright (c) 2021-2023 ForteScarlet <ForteScarlet@163.com>
 *
 * 本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x 、simbot3 等) 的一部分。
 * simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 * 发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 * 你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 * https://www.gnu.org/licenses
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 * https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 */

package love.forte.simboot.core

import love.forte.simboot.core.application.BootApplicationConfiguration
import love.forte.simboot.listener.ParameterBinderFactory

/**
 * 基础的 boot-core 所使用的启动类标记注解，提供部分参数， 主要用于在 [SimbootApp] 中使用。
 *
 * **不应** 在spring环境下使用。
 *
 * [SimbootApplication] 暂不支持注解继承等机制。在使用的时候会直接被获取。
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
public annotation class SimbootApplication(
    /**
     * 指定所需扫描包路径。 如果为空默认选取标记类所在包路径为根路径。
     *
     * @see BootApplicationConfiguration.classesScanPackage
     */
    val classesPackages: Array<String> = [],
    
    /**
     * 指定需要扫描的**顶层监听函数**路径。
     *
     * 顶层监听函数正常来讲只有kotlin下会有，例如：
     * ```kotlin
     * @Listener
     * suspend fun FriendEvent.onEvent() { ... }
     * ```
     *
     * 默认为空。为空时不会扫描。
     *
     * @see BootApplicationConfiguration.topLevelListenerScanPackage
     */
    val topListenerPackages: Array<String> = [],
    
    /**
     * 是否在 [topListenerPackages] 为空的情况下使用 [classesPackages] 的最终值。
     * 如果为 `true`, 则当 [topListenerPackages] 为空的时候（也就是默认的时候）使用 [classesPackages].
     * 同样的，如果 [classesPackages] 为空，则会使用被标记类所在包的路径。
     *
     * 默认为 `false`。
     *
     */
    val classesPackagesForTopListener: Boolean = false,
    
    /**
     * 指定需要扫描的**顶层 [BinderFactory][ParameterBinderFactory] 函数**路径。
     *
     * 正常来讲只有kotlin下会有，例如：
     * ```kotlin
     * @Binder("example-id")
     * fun myBinder(context: ParameterBinderFactory.Context): ParameterBinderResult
     * ```
     *
     * 默认为空。为空时不会扫描。
     *
     * @see BootApplicationConfiguration.topLevelBinderScanPackage
     */
    val topBinderPackages: Array<String> = [],
    
    /**
     * 是否在 [topBinderPackages] 为空的情况下使用 [classesPackages] 的最终值。
     * 如果为 `true`, 则当 [topBinderPackages] 为空的时候（也就是默认的时候）使用 [classesPackages].
     * 同样的，如果 [classesPackages] 为空，则会使用被标记类所在包的路径。
     *
     * 默认为 `false`。
     *
     */
    val classesPackagesForTopBinder: Boolean = false,
    
    /**
     * 指定具体的bot配置文件扫描路径。
     *
     * 如果为空则使用默认配置，配置中默认值为 `simbot-bots/ *.bot*` [BootApplicationConfiguration.DEFAULT_BOT_VERIFY_GLOB] 。
     *
     * @see BootApplicationConfiguration.botConfigurations
     */
    val botResources: Array<String> = [],
    
    /**
     * 是否在最终自动启动所有注册的bot。默认为true。
     *
     * @see BootApplicationConfiguration.isAutoStartBots
     */
    val autoStartBots: Boolean = true,
)

/*
 * Copyright (c) 2022-2023 ForteScarlet <ForteScarlet@163.com>
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

import love.forte.simboot.core.application.Boot
import love.forte.simboot.core.application.BootApplication
import love.forte.simboot.core.application.BootApplicationBuilder
import love.forte.simboot.core.application.BootApplicationConfiguration
import love.forte.simbot.Api4J
import love.forte.simbot.SimbotRuntimeException
import love.forte.simbot.application.ApplicationLauncher
import love.forte.simbot.application.applicationLauncher
import love.forte.simbot.application.createSimbotApplication
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

/**
 * 为使用 [Boot] 作为应用工厂而提供更加简化的预设操作，
 * 并且使得这些操作兼容Java。
 *
 * **Kotlin**
 * ```kotlin
 * val app = SimbootApp<A>(*args) {
 *     // config..?
 * }
 * ```
 * ```kotlin
 * val app = SimbootApp(A::class, *args) {
 *     // config..?
 * }
 * ```
 *
 * **Java**
 * ```java
 * final BootApplication app = SimbootApp.run(Main.class, args);
 * ```
 *
 */
public object SimbootApp {
    private val logger = LoggerFactory.getLogger("love.forte.simboot.core.SimbootApp")
    
    
    /**
     * 使用 [Boot] 作为 simbot 应用工厂来构建一个 [BootApplication].
     *
     * ## 扫描路径
     * 当 [entrance] 不为 `null` 时，会尝试直接通过 [entrance.package][Class.getPackage] 获取需要扫描的主要路径。
     * 如果 [entrance] 为 `null`，则会 **尽量尝试** 在本次调用 [run] 的调用栈中寻找最近调用run的位置来作为扫描的主要路径。
     *
     * 推荐指定一个具体的、携带 [SimbootApplication] 注解的 [entrance]，而不是使用 `null` 来忽略它。
     *
     * @throws SimbootApplicationException 在启动准备过程中出现的异常, 例如提供的 [entrance] 不存在包路径等情况。
     */
    @JvmSynthetic
    public suspend fun run(
        entrance: KClass<*>? = null,
        vararg args: String,
        configurator: BootApplicationConfiguration.() -> Unit = {},
    ): BootApplication {
        val appAnnotation = entrance?.findAnnotation<SimbootApplication>()
        
        val scanPackage = appAnnotation?.classesPackages?.ifEmpty { null }?.toList()
            ?: entrance?.let {
                runCatching {
                    val package0 = it.java.`package`
                        ?: throw SimbootApplicationException("Cannot found the Package from type $it")
                    package0.name?.let { listOf(it) }
                    
                }.getOrNull()
            }
            ?: preStack("love.forte.simboot.core.SimbootApp", "run")?.let { stack ->
                stack.className.substringBeforeLast(delimiter = ".", missingDelimiterValue = "")
                    .takeIf { it.isNotEmpty() }?.let { pkg ->
                        listOf(pkg).also {
                            logger.warn(
                                "The entrance class or @SimbootApplication is null, try get and use the base scan package: {}",
                                it
                            )
                        }
                    }
            } ?: throw SimbootApplicationException("Cannot resolve the scan package.")
        
        
        val configs = resolveToConfig(entrance?.java?.classLoader, appAnnotation, scanPackage)
        val initialConfigurator = configToInitialConfigurator(args.toList(), scanPackage, configs, configurator)
        
        
        return runApp(configurator = initialConfigurator) {
            onCompletion {
                logger.info("Boot application completion via entrance {} with {}", entrance, appAnnotation)
            }
        }
    }
    
    /**
     * 使用 [Boot] 作为 simbot 应用工厂来构建一个 [BootApplication].
     *
     * ```java
     * final BootApplication app = SimbootApp.run(MyApp.class, "arg1", "arg2");
     * // ...
     * app.joinBlocking();
     * ```
     *
     * @throws SimbootApplicationException 在启动准备过程中出现的异常, 例如提供的 [entrance] 不存在包路径等情况。
     *
     */
    @Api4J
    @JvmStatic
    @JvmOverloads
    @JvmName("run")
    public fun run4J(entrance: Class<*>? = null, vararg args: String): ApplicationLauncher<BootApplication> {
        val appAnnotation = entrance?.getAnnotation(SimbootApplication::class.java)
        val scanPackage = appAnnotation?.classesPackages?.ifEmpty { null }?.toList()
            ?: entrance?.let {
                runCatching {
                    val package0 = it.`package`
                        ?: throw SimbootApplicationException("Cannot found the Package from type $it")
                    
                    package0.name?.let { listOf(it) }
                }.getOrNull()
            }
            ?: preStack("love.forte.simboot.core.SimbootApp", "run")?.let { stack ->
                stack.className.substringBeforeLast(delimiter = ".", missingDelimiterValue = "")
                    .takeIf { it.isNotEmpty() }?.let { pkg ->
                        listOf(pkg).also {
                            logger.warn(
                                "The entrance class or @SimbootApplication is null, try get and use the base scan package: {}",
                                it
                            )
                        }
                    }
            } ?: throw SimbootApplicationException("cannot resolve the scan package.")
        
        val configs = resolveToConfig(entrance?.classLoader, appAnnotation, scanPackage)
        val initialConfigurator = configToInitialConfigurator(args.toList(), scanPackage, configs)
        
        return applicationLauncher {
            runApp(configurator = initialConfigurator) {
                onCompletion {
                    logger.info("Boot application completion via entrance {} with {}", entrance, appAnnotation)
                }
            }
        }
        
    }
    
    
    private data class Configs(
        val classLoader: ClassLoader?,
        val topListenerScanPackage: List<String>,
        val topBinderScanPackage: List<String>,
        val botScanResources: List<String>?,
    )
    
    private fun resolveToConfig(classLoader: ClassLoader?, appAnnotation: SimbootApplication?, scanPackage: List<String>): Configs {
        val topListenerScanPackage = appAnnotation?.topListenerPackages?.ifEmpty { null }?.toList()
            ?: (if (appAnnotation?.classesPackagesForTopListener == true) scanPackage else null) ?: emptyList()
        
        val topBinderScanPackage = appAnnotation?.topBinderPackages?.ifEmpty { null }?.toList()
            ?: (if (appAnnotation?.classesPackagesForTopBinder == true) scanPackage else null) ?: emptyList()
        
        val botScanResources = appAnnotation?.botResources?.ifEmpty { null }?.toList()
        
        return Configs(classLoader, topListenerScanPackage, topBinderScanPackage, botScanResources)
    }
    
    private fun configToInitialConfigurator(
        args: List<String>,
        scanPackage: List<String>,
        config: Configs,
        configurator: BootApplicationConfiguration.() -> Unit = {},
    ): BootApplicationConfiguration.() -> Unit {
        return {
            this.args = args.toList()
            this.classLoader = classLoader
            classesScanPackage = scanPackage
            topLevelListenerScanPackage = config.topListenerScanPackage
            topLevelBinderScanPackage = config.topBinderScanPackage
            if (config.botScanResources != null) {
                botConfigurationResources = config.botScanResources
            }
            configurator()
        }
    }
    

    
}

private suspend fun runApp(
    configurator: BootApplicationConfiguration.() -> Unit,
    builder: BootApplicationBuilder.(BootApplicationConfiguration) -> Unit,
): BootApplication {
    return runCatching {
        createSimbotApplication(Boot, configurator = configurator, builder = builder)
    }.getOrElse { throw SimbootApplicationException("Run boot app failure: ${it.localizedMessage}", it) }
}

private inline fun preStack(className: String, methodName: String, inlineMark: () -> Unit = {}): StackTraceElement? {
    inlineMark()
    var isCurrent = false
    for (stack in Thread.currentThread().stackTrace) {
        if (stack.className == className && stack.methodName.substringBefore("$") == methodName) {
            isCurrent = true
            continue
        }
        
        if (isCurrent) {
            return stack
        }
    }
    
    return null
}

/**
 * 使用 [Boot] 作为 simbot 应用工厂来构建一个 [BootApplication].
 *
 * ```kotlin
 * @SimbootApplication
 * class Foo
 *
 * simbootApp<Foo>(args = args) { /* ... */ }
 * ```
 *
 */
public suspend inline fun <reified T> simbootApp(
    vararg args: String,
    crossinline configurator: BootApplicationConfiguration.() -> Unit = {},
): BootApplication = SimbootApp.run(T::class, args = args) { configurator() }


/**
 * 在 [SimbootApp] 启动过程中可能出现的任何异常。
 */
public open class SimbootApplicationException : SimbotRuntimeException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}

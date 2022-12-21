/*
 * Copyright (c) 2022 ForteScarlet <ForteScarlet@163.com>
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

package love.forte.simboot.spring.autoconfigure

import love.forte.simbot.application.EventProvider
import love.forte.simbot.application.EventProviderFactory
import love.forte.simbot.application.installAllEventProviders
import love.forte.simbot.logger.logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean


/**
 * 自动配置当前可加载的所有 [EventProvider] 的配置类。
 *
 * 如果当前spring环境中存在任何额外的[EventProviderFactory], 则使用这些 factory;
 * 否则会直接使用 [installAllEventProviders] 来尝试加载环境中的所有可加载项。
 *
 * For example custom factories:
 *
 * **Kotlin**
 * ```kotlin
 * @Configuration(proxyBeanMethods = false)
 * open class MyCustomFactoryConfiguration {
 *
 *    @Bean
 *    fun fooFactory(): FooProvider.Factory {
 *       return FooProvider
 *    }
 *
 *    @Bean
 *    fun barFactory(): BarFactory {
 *       return BarFactory()
 *    }
 *
 *    // ...
 *
 * }
 * ```
 *
 * **Java**
 * ```java
 * @Configuration(proxyBeanMethods = false)
 * public class MyCustomFactoryConfiguration {
 *
 *    @Bean
 *    public FooProvider.Factory fooFactory() {
 *       return FooProvider.Factory;
 *    }
 *
 *    @Bean
 *    public BarFactory barFactory() {
 *       return new BarFactory();
 *    }
 * }
 * ```
 *
 *
 * @author ForteScarlet
 */
public open class SimbotSpringBootEventProviderAutoInstallBuildConfigure {
    @Bean
    public fun simbotSpringBootEventProviderAutoInstallBuildConfigure(
        @Autowired(required = false) factories: List<EventProviderFactory<*, *>>? = null,
    ): SimbotSpringBootApplicationBuildConfigure {

        val factories0 = factories ?: emptyList()

        return SimbotSpringBootApplicationBuildConfigure { configuration ->
            logger.info("The number of Installable event provider Factories is {}", factories0.size)
            if (factories0.isEmpty()) {
                val classLoader = configuration.classLoader
                logger.info("Install event providers by [installAllEventProviders] via classLoader {}", classLoader)
                installAllEventProviders(classLoader)
            } else {
                logger.debug("Install event providers by: {}", factories)
                factories0.forEach {
                    install(it)
                }
            }
        }
    }

    public companion object {
        private val logger =
            love.forte.simbot.logger.LoggerFactory.logger<SimbotSpringBootEventProviderAutoInstallBuildConfigure>()
    }
}

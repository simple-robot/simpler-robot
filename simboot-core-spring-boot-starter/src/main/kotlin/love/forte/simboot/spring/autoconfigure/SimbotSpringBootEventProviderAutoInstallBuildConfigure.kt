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

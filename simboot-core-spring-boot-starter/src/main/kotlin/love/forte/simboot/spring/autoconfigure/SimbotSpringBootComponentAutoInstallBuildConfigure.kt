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

import love.forte.simbot.Component
import love.forte.simbot.ComponentFactory
import love.forte.simbot.installAllComponents
import love.forte.simbot.logger.logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean


/**
 * 自动配置当前可加载的所有 [Component] 的配置类。
 *
 * 如果当前spring环境中存在任何额外的[ComponentFactory], 则使用这些 factory;
 * 否则会直接使用 [installAllComponents] 来尝试加载环境中的所有可加载项。
 *
 * For example custom factories:
 *
 * **Kotlin**
 * ```kotlin
 * @Configuration(proxyBeanMethods = false)
 * open class MyCustomFactoryConfiguration {
 *
 *    @Bean
 *    fun fooFactory(): FooComponent.Factory {
 *       return FooComponent
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
 *    public FooComponent.Factory fooFactory() {
 *       return FooComponent.Factory;
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
public open class SimbotSpringBootComponentAutoInstallBuildConfigure {

    @Bean
    public fun simbotSpringBootComponentAutoInstallBuildConfigure(
        @Autowired(required = false) factories: List<ComponentFactory<*, *>>? = null
    ): SimbotSpringBootApplicationBuildConfigure {
        val factories0 = factories ?: emptyList()
        return SimbotSpringBootApplicationBuildConfigure { configuration ->
            logger.info("The number of Installable Event Provider Factories is {}", factories0.size)
            if (factories0.isEmpty()) {
                val classLoader = configuration.classLoader
                logger.info("Install components by [installAllComponents] via classLoader {}", classLoader)
                installAllComponents(classLoader)
            } else {
                logger.debug("Install components by: {}", factories)
                factories0.forEach {
                    install(it)
                }
            }
        }
    }


    public companion object {
        private val logger =
            love.forte.simbot.logger.LoggerFactory.logger<SimbotSpringBootComponentAutoInstallBuildConfigure>()
    }
}

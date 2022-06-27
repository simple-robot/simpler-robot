/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 */

package love.forte.simboot.spring.autoconfigure

import love.forte.simboot.spring.autoconfigure.application.SpringBootApplicationBuilder
import love.forte.simboot.spring.autoconfigure.application.SpringBootApplicationConfiguration
import love.forte.simbot.Component
import love.forte.simbot.ComponentFactory
import love.forte.simbot.installAllComponents
import org.springframework.beans.factory.annotation.Autowired


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
public open class SimbotSpringBootComponentAutoInstallBuildConfigure(
    @Autowired(required = false) factories: List<ComponentFactory<*, *>>? = null,
) : SimbotSpringBootApplicationBuildConfigure {
    private val factories: List<ComponentFactory<*, *>> = factories ?: emptyList()
    
    override fun SpringBootApplicationBuilder.config(configuration: SpringBootApplicationConfiguration) {
        if (factories.isEmpty()) {
            installAllComponents(configuration.classLoader)
        } else {
            factories.forEach {
                install(it)
            }
        }
    }
}
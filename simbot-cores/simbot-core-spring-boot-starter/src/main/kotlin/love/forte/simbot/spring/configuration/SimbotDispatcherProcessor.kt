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

package love.forte.simbot.spring.configuration

import love.forte.simbot.event.EventDispatchInterceptor
import love.forte.simbot.event.EventInterceptor
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.logger.logger
import love.forte.simbot.spring.application.SpringEventDispatcherConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * 用于在 starter 中针对 [SpringEventDispatcherConfiguration] 进行处理的处理器，
 * 只能存在一个，默认情况下会加载所有的 [SimbotDispatcherConfigurer] 并应用他们的逻辑。
 *
 * 可以通过注册自定义的 [SimbotDispatcherProcessor] 来覆盖默认行为。
 *
 * @see SimbotDispatcherConfigurer
 * @see DefaultSimbotDispatcherProcessorConfiguration
 */
public interface SimbotDispatcherProcessor {
    /**
     * 处理逻辑。
     */
    public fun process(configuration: SpringEventDispatcherConfiguration)
}

/**
 * 用于在 starter 中配置 [SpringEventDispatcherConfiguration]，
 * 默认情况下注册到 spring 中生效，可注册多个。
 *
 * Kotlin example:
 *
 * ```kt
 * @Component
 * class MySimbotDispatcherConfigurer : SimbotDispatcherConfigurer {
 *     // impl...
 * }
 * ```
 *
 * ```kt
 * @Configuration
 * open class MySimbotDispatcherConfiguration {
 *     @Bean
 *     open fun myDispatcherConfigurer(): SimbotDispatcherConfigurer = ...
 * }
 * ```
 *
 * Java example:
 *
 * ```java
 * @Component
 * public class MySimbotDispatcherConfigurer implements SimbotDispatcherConfigurer {
 *      // impl...
 * }
 * ```
 *
 * ```java
 * @Configuration
 * public class MySimbotDispatcherConfiguration {
 *     @Bean
 *     public SimbotDispatcherConfigurer myDispatcherConfigurer() {
 *         return ...
 *     }
 * }
 * ```
 *
 * @author ForteScarlet
 */
public interface SimbotDispatcherConfigurer {
    /**
     * 配置 [configuration].
     */
    public fun configure(configuration: SpringEventDispatcherConfiguration)
}


/**
 * 用户注册 [SimbotDispatcherProcessor] 默认行为的配置类。
 *
 */
@Configuration(proxyBeanMethods = false)
public open class DefaultSimbotDispatcherProcessorConfiguration {
    @Bean(DEFAULT_SIMBOT_DISPATCHER_PROCESSOR_BEAN_NAME)
    @ConditionalOnMissingBean(SimbotDispatcherProcessor::class)
    public open fun defaultSimbotDispatcherProcessor(
        @Autowired(required = false) configurers: List<SimbotDispatcherConfigurer>? = null,
        @Autowired(required = false) interceptors: List<EventInterceptor>? = null,
        @Autowired(required = false) dispatcherInterceptors: List<EventDispatchInterceptor>? = null
    ): DefaultSimbotDispatcherProcessor {
        return DefaultSimbotDispatcherProcessor(
            interceptors = interceptors ?: emptyList(),
            dispatcherInterceptors = dispatcherInterceptors ?: emptyList(),
            configurers = configurers ?: emptyList()
        )
    }

    public companion object {
        public const val DEFAULT_SIMBOT_DISPATCHER_PROCESSOR_BEAN_NAME: String =
            "love.forte.simbot.spring.configuration.defaultSimbotDispatcherProcessor"
    }
}

/**
 * [SimbotDispatcherProcessor] 的默认实现，通过
 * [DefaultSimbotDispatcherProcessorConfiguration] 向 spring 中配置。
 */
public open class DefaultSimbotDispatcherProcessor(
    private val configurers: List<SimbotDispatcherConfigurer>,
    private val interceptors: List<EventInterceptor>,
    private val dispatcherInterceptors: List<EventDispatchInterceptor>
) :
    SimbotDispatcherProcessor {
    override fun process(configuration: SpringEventDispatcherConfiguration) {
        // 拦截器加载
        interceptors.forEach {
            configuration.addInterceptor(it)
            logger.debug("Added interceptor {}", it)
        }

        dispatcherInterceptors.forEach {
            configuration.addDispatchInterceptor(it)
            logger.debug("Added dispatcher interceptor {}", it)
        }

        configurers.forEach {
            it.configure(configuration)
            logger.debug("Configured {} by configurer {}", configuration, it)
        }
    }

    public companion object {
        private val logger = LoggerFactory.logger<DefaultSimbotDispatcherProcessor>()
    }
}

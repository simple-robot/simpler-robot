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

package love.forte.simbot.spring.configuration

import love.forte.simbot.interception.InterceptorRegistrar
import love.forte.simbot.logger.Logger
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.logger.logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 *
 * [InterceptorRegistrar] 的处理器。
 *
 * 负责在 [Spring Application][love.forte.simbot.spring.application.Spring] 启动阶段
 * 对 [InterceptorRegistrar] 进行处理，注册所有通过 [SimbotInterceptorRegistrar] Bean
 * 声明的拦截器。
 *
 * @since 5.0
 * @author ForteScarlet
 */
public interface SimbotInterceptorRegistrarProcessor {
    /**
     * 处理 [InterceptorRegistrar]，注册所有已声明的拦截器。
     *
     * @param registrar 拦截器注册器
     */
    public fun process(registrar: InterceptorRegistrar)
}


/**
 *
 * 在 [DefaultSimbotInterceptorRegistrarProcessor] 中对 [InterceptorRegistrar]
 * 进行自定义处理的可扩展类型。
 *
 * 实现此接口并将其声明为 Spring Bean，即可在应用启动时自动将自定义的拦截器
 * 注册到 [InterceptorRegistrar] 中。可注册多个此类 Bean。
 *
 * **示例：**
 *
 * ```kotlin
 * @Component
 * class MyInterceptorRegistrar : SimbotInterceptorRegistrar {
 *     override fun register(registrar: InterceptorRegistrar) {
 *         registrar.onMessageSend { _, context, chain ->
 *             // 在消息发送前修改消息内容
 *             val modifiedContext = context.withMessage(/* 修改后的消息 */)
 *             chain.proceed(modifiedContext)
 *         }
 *     }
 * }
 * ```
 *
 * @since 5.0
 * @author ForteScarlet
 */
public interface SimbotInterceptorRegistrar {
    /**
     * 向 [registrar] 注册自定义的拦截器。
     *
     * @param registrar 拦截器注册器
     */
    public fun register(registrar: InterceptorRegistrar)
}


/**
 * [SimbotInterceptorRegistrarProcessor] 的默认实现配置。
 *
 * @since 5.0
 * @author ForteScarlet
 */
@Configuration(proxyBeanMethods = false)
public open class DefaultSimbotInterceptorRegistrarProcessorConfiguration {

    @Bean(DEFAULT_SIMBOT_INTERCEPTOR_REGISTRAR_PROCESSOR_BEAN_NAME)
    @ConditionalOnMissingBean(SimbotInterceptorRegistrarProcessor::class)
    public open fun defaultSimbotInterceptorRegistrarProcessor(
        @Autowired(required = false) registrars: List<SimbotInterceptorRegistrar>? = null,
    ): DefaultSimbotInterceptorRegistrarProcessor =
        DefaultSimbotInterceptorRegistrarProcessor(registrars ?: emptyList())

    public companion object {
        public const val DEFAULT_SIMBOT_INTERCEPTOR_REGISTRAR_PROCESSOR_BEAN_NAME: String =
            "love.forte.simbot.spring.configuration.defaultSimbotInterceptorRegistrarProcessor"
    }
}


/**
 * [SimbotInterceptorRegistrarProcessor] 的默认实现。
 *
 * 会遍历所有已注册的 [SimbotInterceptorRegistrar] Bean 并依次调用其
 * [register][SimbotInterceptorRegistrar.register] 方法来完成拦截器注册。
 *
 * @param registrars 所有已注册的拦截器注册器
 *
 * @since 5.0
 * @author ForteScarlet
 */
public class DefaultSimbotInterceptorRegistrarProcessor(
    private val registrars: List<SimbotInterceptorRegistrar>,
) : SimbotInterceptorRegistrarProcessor {

    override fun process(registrar: InterceptorRegistrar) {
        if (registrars.isEmpty()) {
            logger.debug("No SimbotInterceptorRegistrar beans found, skipping interceptor registration")
            return
        }

        logger.debug("Processing {} SimbotInterceptorRegistrar bean(s)", registrars.size)
        registrars.forEach { itx ->
            try {
                itx.register(registrar)
                logger.debug("Registered interceptors via {}", itx)
            } catch (e: Throwable) {
                logger.error("Failed to register interceptors via $itx", e)
            }
        }
    }

    public companion object {
        private val logger: Logger = LoggerFactory.logger<DefaultSimbotInterceptorRegistrarProcessor>()
    }
}

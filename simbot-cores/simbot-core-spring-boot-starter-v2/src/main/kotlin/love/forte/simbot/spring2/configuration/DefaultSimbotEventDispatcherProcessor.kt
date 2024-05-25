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

package love.forte.simbot.spring2.configuration

import love.forte.simbot.event.EventDispatcher
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.logger.logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * 配置 [SimbotEventDispatcherProcessor] 的默认实现
 * [DefaultSimbotEventDispatcherProcessor]
 * 的配置类。
 *
 */
@Configuration(proxyBeanMethods = false)
public open class DefaultSimbotEventDispatcherProcessorConfiguration {
    @Bean(DEFAULT_SIMBOT_EVENT_DISPATCHER_PROCESSOR_BEAN_NAME)
    @ConditionalOnMissingBean(SimbotEventDispatcherProcessor::class)
    public open fun defaultSimbotEventDispatcherProcessor(
        registerProcessor: SimbotEventListenerRegistrarProcessor,
        @Autowired(required = false) postConfigurers: List<SimbotEventDispatcherPostConfigurer>? = null
    ): DefaultSimbotEventDispatcherProcessor = DefaultSimbotEventDispatcherProcessor(
        registerProcessor,
        postConfigurers ?: emptyList()
    )

    public companion object {
        public const val DEFAULT_SIMBOT_EVENT_DISPATCHER_PROCESSOR_BEAN_NAME: String =
            "love.forte.simbot.spring.configuration.defaultSimbotEventDispatcherProcessor"
    }
}

/**
 * [SimbotEventDispatcherProcessor] 的默认行为实现。
 * @author ForteScarlet
 */
public open class DefaultSimbotEventDispatcherProcessor(
    private val registerProcessor: SimbotEventListenerRegistrarProcessor,
    private val postConfigurers: List<SimbotEventDispatcherPostConfigurer>
) : SimbotEventDispatcherProcessor {
    override fun process(dispatcher: EventDispatcher) {
        registerProcessor.process(dispatcher)
        logger.debug("Processed dispatcher {} by processor {}", dispatcher, registerProcessor)

        postConfigurers.forEach {
            it.configure(dispatcher)
            logger.debug("Configured dispatcher {} by {}", dispatcher, it)
        }
    }

    public companion object {
        private val logger = LoggerFactory.logger<DefaultSimbotDispatcherProcessor>()
    }
}

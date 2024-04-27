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

package love.forte.simbot.spring2.configuration.application

import love.forte.simbot.application.ApplicationFactoryConfigurer
import love.forte.simbot.spring.common.application.SpringApplicationBuilder
import love.forte.simbot.spring.common.application.SpringApplicationEventRegistrar
import love.forte.simbot.spring.common.application.SpringApplicationLauncher
import love.forte.simbot.spring.common.application.SpringEventDispatcherConfiguration
import love.forte.simbot.spring2.application.Spring
import love.forte.simbot.spring2.configuration.SimbotComponentInstallProcessor
import love.forte.simbot.spring2.configuration.SimbotDispatcherProcessor
import love.forte.simbot.spring2.configuration.SimbotPluginInstallProcessor
import love.forte.simbot.spring2.configuration.config.SimbotApplicationConfigurationProcessor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * [SimbotApplicationLauncherFactory] 的默认逻辑实现
 * [DefaultSimbotApplicationLauncherFactory] 的配置类。
 *
 */
@Configuration(proxyBeanMethods = true)
public open class DefaultSimbotSpringApplicationLauncherFactoryConfiguration {
    @Bean(DEFAULT_SPRING_APPLICATION_LAUNCHER_PROCESSOR_BEAN_NAME)
    @ConditionalOnMissingBean(SimbotApplicationLauncherFactory::class)
    public open fun defaultSpringApplicationLauncherProcessor(
        @Autowired(required = false) builderPreConfigurers: List<SimbotApplicationLauncherPreConfigurer>? = null,
        @Autowired(required = false) builderPostConfigurers: List<SimbotApplicationLauncherPostConfigurer>? = null,
        @Autowired applicationConfigurationProcessor: SimbotApplicationConfigurationProcessor,
        @Autowired dispatcherProcessor: SimbotDispatcherProcessor,
        @Autowired componentProcessor: SimbotComponentInstallProcessor,
        @Autowired pluginProcessor: SimbotPluginInstallProcessor,
    ): DefaultSimbotApplicationLauncherFactory =
        DefaultSimbotApplicationLauncherFactory(
            builderPreConfigurers ?: emptyList(),
            builderPostConfigurers ?: emptyList(),
            applicationConfigurationProcessor,
            dispatcherProcessor,
            componentProcessor,
            pluginProcessor,
        )

    public companion object {
        public const val DEFAULT_SPRING_APPLICATION_LAUNCHER_PROCESSOR_BEAN_NAME: String =
            "love.forte.simbot.spring.configuration.defaultSpringApplicationLauncherProcessor"
    }
}


/**
 * [SimbotApplicationLauncherFactory] 的默认实现。
 *
 */
public class DefaultSimbotApplicationLauncherFactory(
    private val builderPreConfigurers: List<SimbotApplicationLauncherPreConfigurer>,
    private val builderPostConfigurers: List<SimbotApplicationLauncherPostConfigurer>,
    private val applicationConfigurationProcessor: SimbotApplicationConfigurationProcessor,
    private val dispatcherProcessor: SimbotDispatcherProcessor,
    private val componentProcessor: SimbotComponentInstallProcessor,
    private val pluginProcessor: SimbotPluginInstallProcessor,
) : SimbotApplicationLauncherFactory {
    override fun process(factory: Spring): SpringApplicationLauncher {
        return factory.create {
            // pre
            builderPreConfigurers.forEach { it.configure(this) }

            configure0()

            // post
            builderPostConfigurers.forEach { it.configure(this) }
        }
    }

    private fun ApplicationFactoryConfigurer<
        SpringApplicationBuilder,
        SpringApplicationEventRegistrar,
        SpringEventDispatcherConfiguration
        >.configure0() {
        config {
            applicationConfigurationProcessor.process(this)
        }
        // dispatchers
        eventDispatcher {
            dispatcherProcessor.process(this)
        }
        // components
        componentProcessor.process(this)
        // plugins
        pluginProcessor.process(this)
    }
}

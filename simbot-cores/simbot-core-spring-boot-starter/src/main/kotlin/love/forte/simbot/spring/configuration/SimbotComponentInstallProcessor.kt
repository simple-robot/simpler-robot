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

import love.forte.simbot.component.ComponentFactory
import love.forte.simbot.component.ComponentInstaller
import love.forte.simbot.component.findAnyInstallAllPlugins
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.spring.common.application.SpringApplicationConfigurationProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


/**
 * 在 starter 中通过 [ComponentInstaller] 安装组件信息。
 * 只能存在一个，默认情况下会尝试加载当前所有的
 * [ComponentFactory] （无配置注册）、可通过 SPI 自动加载的以及 [SimbotComponentInstaller] 并进行处理。
 *
 * 注册自定义的 [SimbotComponentInstallProcessor] 可覆盖默认行为。
 *
 * @author ForteScarlet
 */
public interface SimbotComponentInstallProcessor {
    /**
     * 处理 [installer].
     */
    public fun process(installer: ComponentInstaller)

}

/**
 * 在 starter 中的 [SimbotComponentInstallProcessor] 默认实现
 * [DefaultSimbotComponentInstallProcessor] 行为里对 [ComponentInstaller]
 * 进行自定义处理的可扩展类型。
 * 可以注册多个。
 *
 * @see SimbotComponentInstallProcessor
 * @see DefaultSimbotComponentInstallProcessor
 *
 */
public interface SimbotComponentInstaller {
    public fun install(installer: ComponentInstaller)
}

/**
 * 配置 [SimbotComponentInstallProcessor] 默认行为实现的配置类。
 *
 */
@Configuration(proxyBeanMethods = false)
public open class DefaultSimbotComponentInstallProcessorConfiguration {
    @Bean(DEFAULT_SIMBOT_COMPONENT_INSTALL_PROCESSOR_BEAN_NAME)
    @ConditionalOnMissingBean(SimbotComponentInstallProcessor::class)
    public open fun defaultSimbotComponentInstallProcessor(
        properties: SpringApplicationConfigurationProperties,
        @Autowired(required = false) factories: List<ComponentFactory<*, *>>? = null,
        @Autowired(required = false) installers: List<SimbotComponentInstaller>? = null,
    ): DefaultSimbotComponentInstallProcessor {
        return DefaultSimbotComponentInstallProcessor(
            properties.components.autoInstallProviders,
            properties.components.autoInstallProviderConfigurers,
            factories ?: emptyList(),
            installers ?: emptyList()
        )
    }

    public companion object {
        public const val DEFAULT_SIMBOT_COMPONENT_INSTALL_PROCESSOR_BEAN_NAME: String =
            "love.forte.simbot.spring.configuration.defaultSimbotComponentInstallProcessor"
    }
}

/**
 * [SimbotComponentInstallProcessor] 的默认行为实现
 */
public class DefaultSimbotComponentInstallProcessor(
    private val autoInstallProviders: Boolean,
    private val autoInstallProviderConfigurers: Boolean,
    private val factories: List<ComponentFactory<*, *>>,
    private val installers: List<SimbotComponentInstaller>,
) : SimbotComponentInstallProcessor {
    override fun process(installer: ComponentInstaller) {
        if (autoInstallProviders) {
            installer.findAnyInstallAllPlugins(autoInstallProviderConfigurers)
            logger.debug(
                "Automatically install all component providers automatically with autoLoadProviderConfigurers={}",
                autoInstallProviderConfigurers
            )
        } else {
            logger.debug("Automatic installation from component providers was disabled")
            // 自动的
        }

        // factories
        factories.forEach {
            installer.install(it)
            logger.debug("Installed component factory {} of current factories", it)
        }

        // installers
        installers.forEach {
            it.install(installer)
            logger.debug("Installed installer by {}", it)
        }
    }

    public companion object {
        private val logger = LoggerFactory.getLogger(DefaultSimbotComponentInstallProcessor::class)
    }
}

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

package love.forte.simbot.spring.configuration.application

import love.forte.simbot.spring.application.Spring
import love.forte.simbot.spring.common.application.SpringApplicationLauncher
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * 对 [SimbotApplicationLauncherFactory] 的处理器，
 * 通过 [SimbotApplicationLauncherFactory] 得到一个
 * 预备启动器 [SpringApplicationLauncher]。
 *
 * 默认情况下会直接提供 [Spring] 并构建它。
 */
public fun interface SimbotApplicationLauncherFactoryProcessor {
    /**
     * 处理 [applicationLauncherFactory]
     */
    public fun process(applicationLauncherFactory: SimbotApplicationLauncherFactory): SpringApplicationLauncher
}

/**
 * 用于配置 [SimbotApplicationLauncherFactoryProcessor] 的默认实现 [DefaultSimbotApplicationLauncherFactoryProcessor]
 * 的配置器。
 */
@Configuration(proxyBeanMethods = false)
public open class DefaultSimbotApplicationLauncherFactoryProcessorConfiguration {
    @Bean(DEFAULT_SIMBOT_APPLICATION_LAUNCHER_FACTORY_PROCESSOR_BEAN_NAME)
    @ConditionalOnMissingBean(SimbotApplicationLauncherFactoryProcessor::class)
    public open fun defaultSimbotApplicationLauncherFactoryProcessor(): DefaultSimbotApplicationLauncherFactoryProcessor =
        DefaultSimbotApplicationLauncherFactoryProcessor

    public companion object {
        public const val DEFAULT_SIMBOT_APPLICATION_LAUNCHER_FACTORY_PROCESSOR_BEAN_NAME: String =
            "love.forte.simbot.spring.configuration.application.defaultSimbotApplicationLauncherFactoryProcessor"
    }
}

/**
 * [SimbotApplicationLauncherFactoryProcessor] 的默认逻辑实现，
 * 会直接向 [SimbotApplicationLauncherFactory] 提供一个 [Spring] 并得到
 * [SpringApplicationLauncher].
 */
public object DefaultSimbotApplicationLauncherFactoryProcessor : SimbotApplicationLauncherFactoryProcessor {
    override fun process(applicationLauncherFactory: SimbotApplicationLauncherFactory): SpringApplicationLauncher =
        applicationLauncherFactory.process(Spring)
}

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

import love.forte.simbot.spring.common.application.SpringApplication
import love.forte.simbot.spring.common.application.SpringApplicationLauncher
import love.forte.simbot.suspendrunner.runInNoScopeBlocking
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


/**
 *
 * @author ForteScarlet
 */
@Configuration(proxyBeanMethods = false)
public open class SimbotApplicationConfiguration {
    @Bean(SPRING_APPLICATION_LAUNCHER_BEAN_NAME)
    public open fun springApplicationLauncher(
        factory: SimbotApplicationLauncherFactory,
        processor: SimbotApplicationLauncherFactoryProcessor
    ): SpringApplicationLauncher =
        processor.process(factory)

    @Bean(
        value = [SPRING_APPLICATION_BEAN_NAME],
        destroyMethod = "cancel"
    )
    public open fun springApplication(launcher: SpringApplicationLauncher): SpringApplication = runInNoScopeBlocking {
        launcher.launch()
    }

    public companion object {
        public const val SPRING_APPLICATION_LAUNCHER_BEAN_NAME: String =
            "love.forte.simbot.spring.configuration.application.defaultSpringApplicationLauncher"

        public const val SPRING_APPLICATION_BEAN_NAME: String =
            "love.forte.simbot.spring.configuration.application.defaultSpringApplication"
    }
}

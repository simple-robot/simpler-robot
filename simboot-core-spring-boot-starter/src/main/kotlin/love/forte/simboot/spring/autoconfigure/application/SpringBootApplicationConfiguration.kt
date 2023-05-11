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

package love.forte.simboot.spring.autoconfigure.application

import love.forte.simboot.core.application.BootApplicationConfiguration
import love.forte.simboot.core.application.BootApplicationConfiguration.Companion.DEFAULT_BOT_VERIFY_GLOB
import love.forte.simbot.bot.Bot
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.context.ApplicationContext


/**
 * 用于外部化配置的配置属性类。
 */
@Suppress("MemberVisibilityCanBePrivate")
public class SpringBootApplicationConfigurationProperties {
    
    /**
     * 需要加载的所有 bot 配置文件的资源扫描glob。默认为 [DEFAULT_BOT_VERIFY_GLOB]。
     *
     */
    public var botConfigurationResources: List<String> = listOf(DEFAULT_BOT_VERIFY_GLOB)
    
    /**
     * 是否在bot注册后，在 `application` 构建完毕的时候自动执行 [Bot.start]。
     *
     * 默认为true。
     *
     */
    public var isAutoStartBots: Boolean = true
    
    
    public fun toConfiguration(
        applicationArguments: ApplicationArguments,
        applicationContext: ApplicationContext,
    ): SpringBootApplicationConfiguration {
        return SpringBootApplicationConfiguration().also { config ->
            config.applicationContext = applicationContext
            config.applicationArguments = applicationArguments
            config.args = applicationArguments.sourceArgs.asList()
            config.botConfigurationResources = this.botConfigurationResources
            applicationContext.classLoader?.also {
                config.classLoader = it
            }
            config.isAutoStartBots = this.isAutoStartBots
        }
    }
}


/**
 * 在 [SpringBoot] 中所使用的应用程序配置类。
 *
 * 在使用前需要对 [applicationArguments] 、[applicationContext] 等与 Spring Boot 相关的配置信息。
 *
 * @see BootApplicationConfiguration
 */
public open class SpringBootApplicationConfiguration : BootApplicationConfiguration() {
    public open lateinit var applicationArguments: ApplicationArguments
    public open lateinit var applicationContext: ApplicationContext
    
    override var logger: Logger = LoggerFactory.getLogger(SpringBootApplicationConfiguration::class.java)
    override var classLoader: ClassLoader = SpringBootApplicationConfiguration::class.java.classLoader
    
    @Deprecated("Use @SimbotTopLevelListenerScan(...) in spring")
    override var topLevelBinderScanPackage: List<String> = emptyList()
    
    @Deprecated("Use @SimbotTopLevelBinderScan(...) in spring")
    override var topLevelListenerScanPackage: List<String> = emptyList()
}

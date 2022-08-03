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

package love.forte.simboot.spring.autoconfigure.application

import love.forte.simboot.core.application.BootApplicationConfiguration
import love.forte.simboot.core.application.BootApplicationConfiguration.Companion.DEFAULT_BOT_VERIFY_GLOB
import love.forte.simboot.listener.ParameterBinderFactory
import love.forte.simboot.spring.autoconfigure.EnableSimbot
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
     * Deprecated: @see [love.forte.simboot.spring.autoconfigure.SimbotTopLevelListenerScan]
     */
    @Deprecated("Use @SimbotTopLevelListenerScan(...)")
    public var topLevelListenerScanPackage: List<String> = emptyList()
    
    
    /**
     * Deprecated: @see [love.forte.simboot.spring.autoconfigure.SimbotTopLevelBinderScan]
     */
    @Deprecated("Use @SimbotTopLevelBinderScan(...)")
    public var topLevelBinderScanPackage: List<String> = emptyList()
    
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
    
            
            @Suppress("DEPRECATION")
            config.topLevelListenerScanPackage = this.topLevelListenerScanPackage
            @Suppress("DEPRECATION")
            config.topLevelBinderScanPackage = this.topLevelBinderScanPackage
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
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
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext


/**
 * 用于外部化配置的配置属性类。
 */
@Suppress("MemberVisibilityCanBePrivate")
public class SpringBootApplicationConfigurationProperties {
    /**
     * 运行参数。
     */
    public var args: List<String> = emptyList()
    
    /**
     * 需要加载的所有 `*.bot(.*)?` 文件的资源扫描glob。默认为 [DEFAULT_BOT_VERIFY_GLOB]。
     *
     */
    public var botConfigurationResources: List<String> = listOf(DEFAULT_BOT_VERIFY_GLOB)
    
    
    public fun toConfiguration(applicationContext: ApplicationContext): SpringBootApplicationConfiguration {
        return SpringBootApplicationConfiguration().also {
            it.args = this.args
            it.botConfigurationResources = this.botConfigurationResources
            it.applicationContext = applicationContext
        }
    }
}


public open class SpringBootApplicationConfiguration : BootApplicationConfiguration() {
    override var logger: Logger = LoggerFactory.getLogger(SpringBootApplicationConfiguration::class.java)
    internal lateinit var applicationContext: ApplicationContext
}
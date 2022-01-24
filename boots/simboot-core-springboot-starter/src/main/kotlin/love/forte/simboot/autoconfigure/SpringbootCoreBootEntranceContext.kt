/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
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
 *
 */

package love.forte.simboot.autoconfigure

import love.forte.di.BeanContainer
import love.forte.simboot.Configuration
import love.forte.simboot.core.CoreBootEntranceContext
import love.forte.simboot.factory.BeanContainerFactory
import love.forte.simboot.factory.ConfigurationFactory
import love.forte.simbot.BotVerifyInfo
import love.forte.simbot.LoggerFactory
import love.forte.simbot.event.EventListenerManager
import org.slf4j.Logger
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import java.io.InputStream

/**
 *
 * @author ForteScarlet
 */
public class SpringbootCoreBootEntranceContext(
    private val _configurationFactory: ConfigurationFactory,
    private val _beanContainerFactory: BeanContainerFactory,
    private val _listenerManager: EventListenerManager,
    override val topFunctionScanPackages: Set<String>,
    override val args: Array<String>
) : CoreBootEntranceContext {

    override fun getConfigurationFactory(): ConfigurationFactory {
        return _configurationFactory
    }

    override fun getBeanContainerFactory(): BeanContainerFactory {
        return _beanContainerFactory
    }

    override fun getAllBotInfos(
        configuration: Configuration,
        beanContainer: BeanContainer
    ): List<BotVerifyInfo> {
        //
        val resolver = PathMatchingResourcePatternResolver()
        // find file first
        val resources = resolver.getResources("file:$BOTS_PATTERN")
            .ifEmpty { resolver.getResources("classpath:$BOTS_PATTERN") }

        return resources.map { SpringResourceBotVerifyInfo(it) }
    }

    override fun getListenerManager(beanContainer: BeanContainer): EventListenerManager {
        return _listenerManager
    }

    override val logger: Logger = LoggerFactory.getLogger(SpringbootCoreBootEntranceContext::class)


    public companion object {
        private const val BOTS_PATTERN = "simbot-bots/**.bot"
    }
}


private class SpringResourceBotVerifyInfo(
    private val resource: Resource
) : BotVerifyInfo {
    override val infoName: String get() = resource.filename ?: resource.url.toString()
    override fun inputStream(): InputStream = resource.inputStream
}
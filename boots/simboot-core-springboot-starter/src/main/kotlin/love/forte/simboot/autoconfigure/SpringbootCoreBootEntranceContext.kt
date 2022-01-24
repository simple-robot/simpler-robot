/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
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
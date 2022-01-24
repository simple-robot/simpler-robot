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

import love.forte.simboot.core.CoreBootEntranceContext
import love.forte.simboot.factory.BeanContainerFactory
import love.forte.simboot.factory.ConfigurationFactory
import love.forte.simbot.event.EventListenerManager
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean

/**
 * 配置 [SpringbootCoreBootEntranceContext]
 * @author ForteScarlet
 */
public open class SpringbootCoreBootEntranceContextConfiguration {

    @Bean
    @ConditionalOnMissingBean(CoreBootEntranceContext::class)
    public fun springbootCoreBootEntranceContextConfiguration(
        configurationFactory: ConfigurationFactory,
        beanContainerFactory: BeanContainerFactory,
        listenerManager: EventListenerManager,
        args: ApplicationArguments
    ): SpringbootCoreBootEntranceContext {
        return SpringbootCoreBootEntranceContext(
            configurationFactory,
            beanContainerFactory,
            listenerManager,
            emptySet(),
            args.sourceArgs
        )
    }


}
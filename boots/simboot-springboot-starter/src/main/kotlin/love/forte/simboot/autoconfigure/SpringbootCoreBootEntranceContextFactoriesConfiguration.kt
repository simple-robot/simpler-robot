/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simboot.autoconfigure

import love.forte.simboot.factory.BeanContainerFactory
import love.forte.simboot.factory.ConfigurationFactory
import org.springframework.beans.factory.ListableBeanFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.core.env.ConfigurableEnvironment

/**
 *
 * 通过 [ConfigurableEnvironment] 配置 [SpringConfigurationFactory].
 *
 * @author ForteScarlet
 */
public open class SpringbootCoreBootEntranceContextFactoriesConfiguration {

    /**
     * 提供 [ConfigurationFactory] 配置。
     */
    @Bean
    @ConditionalOnMissingBean(ConfigurationFactory::class)
    public open fun configurationFactory(environment: ConfigurableEnvironment): ConfigurationFactory {
        return SpringConfigurationFactory(environment)
    }


    /**
     * 提供 [BeanContainerFactory] 配置。
     */
    @Bean
    @ConditionalOnMissingBean(BeanContainerFactory::class)
    public open fun beanContainerFactory(beanFactory: ListableBeanFactory): SpringBeanContainerFactory {
        return SpringBeanContainerFactory(beanFactory)
    }




}


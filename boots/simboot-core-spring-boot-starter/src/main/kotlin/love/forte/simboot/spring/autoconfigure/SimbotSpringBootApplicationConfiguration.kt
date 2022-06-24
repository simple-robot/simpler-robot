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

package love.forte.simboot.spring.autoconfigure

import love.forte.simboot.spring.autoconfigure.application.SpringBootApplication
import love.forte.simboot.spring.autoconfigure.application.SpringBootApplicationConfiguration
import love.forte.simboot.spring.autoconfigure.application.SpringBootApplicationConfigurationProperties
import love.forte.simboot.spring.autoconfigure.application.springBootApplication
import love.forte.simbot.Api4J
import love.forte.simbot.application.Application
import love.forte.simbot.event.EventListenerManager
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean


/**
 *
 * 对 [Application] 的配置类。
 *
 * @author ForteScarlet
 */
public open class SimbotSpringBootApplicationConfiguration {
    
    @Bean
    @ConditionalOnMissingBean(SpringBootApplicationConfigurationProperties::class)
    @ConfigurationProperties("simbot")
    public fun applicationConfigurationProperties(): SpringBootApplicationConfigurationProperties {
        return SpringBootApplicationConfigurationProperties()
    }
    
    
    @Bean
    @ConditionalOnMissingBean(SpringBootApplicationConfiguration::class)
    public fun applicationConfiguration(
        applicationConfigurationProperties: SpringBootApplicationConfigurationProperties,
        applicationContext: ApplicationContext,
        applicationArguments: ApplicationArguments,
    ): SpringBootApplicationConfiguration {
        
        return applicationConfigurationProperties.toConfiguration(
            applicationArguments,
            applicationContext
        )
    }
    
    
    /**
     * 构建 [simbot application][Application].
     */
    @OptIn(Api4J::class)
    @Bean(destroyMethod = "shutdownBlocking")
    @ConditionalOnMissingBean(Application::class)
    public fun simbotSpringBootApplication(
        initialConfiguration: SpringBootApplicationConfiguration,
        configurationConfigures: List<SimbotSpringBootApplicationConfigurationConfigure>,
        applicationConfigures: List<SimbotSpringBootApplicationBuildConfigure>,
        coroutineDispatcherContainer: CoroutineDispatcherContainer,
    ): SpringBootApplication {
        // check application context init.
        initialConfiguration.applicationContext
        
        return springBootApplication(initialConfiguration,
            {
                // initial
                coroutineContext += coroutineDispatcherContainer.dispatcher
                
                configurationConfigures.forEach { configure ->
                    configure.run { config() }
                }
                
                
            }) { configuration ->
            applicationConfigures.forEach { configure ->
                configure.run { config(configuration) }
            }
            // TODO..?
        }.launchBlocking()
    }
    
    
    // region after application
    /**
     * 当 [Application] 构建完成后, 提供 [Application.Environment] 信息。
     */
    @Bean
    public fun simbotApplicationEnvironment(application: Application): Application.Environment = application.environment
    
    
    /**
     * 当 [Application] 构建完成后, 提供 [EventListenerManager] 信息。
     */
    @Bean
    public fun simbotApplicationBotManager(application: Application): EventListenerManager =
        application.eventListenerManager
    // endregion
    
    
}
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

package love.forte.simboot.spring.autoconfigure

import love.forte.simboot.spring.autoconfigure.application.SpringBootApplication
import love.forte.simboot.spring.autoconfigure.application.SpringBootApplicationConfiguration
import love.forte.simboot.spring.autoconfigure.application.SpringBootApplicationConfigurationProperties
import love.forte.simboot.spring.autoconfigure.application.springBootApplication
import love.forte.simbot.Api4J
import love.forte.simbot.application.Application
import love.forte.simbot.application.BotManagers
import love.forte.simbot.event.EventListenerManager
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.logger.logger
import love.forte.simbot.utils.runInNoScopeBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.getBean
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.ResourceLoaderAware
import org.springframework.context.annotation.Bean
import org.springframework.core.io.ResourceLoader


/**
 *
 * 对 [Application] 的配置类。
 *
 * @author ForteScarlet
 */
public open class SimbotSpringBootApplicationConfiguration : ResourceLoaderAware {
    private var resourceLoader: ResourceLoader? = null
    override fun setResourceLoader(resourceLoader: ResourceLoader) {
        this.resourceLoader = resourceLoader
    }

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
        applicationContext: ApplicationContext
    ): SpringBootApplicationConfiguration {
        val applicationArguments = applicationContext.getBean<ApplicationArguments>()

        return applicationConfigurationProperties.toConfiguration(
            applicationArguments,
            applicationContext
        ).also {
            val resourceClassLoader = resourceLoader?.classLoader
            if (resourceClassLoader != null) {
                it.classLoader = resourceClassLoader
            }
        }
    }


    /**
     * 构建 [simbot application][Application].
     */
    @Bean(destroyMethod = "shutdownBlocking")
    @ConditionalOnMissingBean(Application::class)
    public fun simbotSpringBootApplication(
        initialConfiguration: SpringBootApplicationConfiguration,
        @Autowired(required = false) configurationConfigures: List<SimbotSpringBootApplicationConfigurationConfigure>? = null,
        @Autowired(required = false) applicationConfigures: List<SimbotSpringBootApplicationBuildConfigure>? = null,
        coroutineDispatcherContainer: CoroutineDispatcherContainer,
    ): SpringBootApplication {
        return runInNoScopeBlocking {
            logger.debug("Launching application...")
            springBootApplication(initialConfiguration,
                {
                    // initial
                    coroutineContext += coroutineDispatcherContainer.dispatcher

                    configurationConfigures?.forEach { configure ->
                        configure.run { config() }
                    }


                }) { configuration ->
                applicationConfigures?.forEach { configure ->
                    configure.run { config(configuration) }
                }
            }.launch()
        }.also {
            logger.debug("Application launched. {}", it)
        }
    }


    // region after application
    /**
     * 当 [Application] 构建完成后, 提供 [Application.Environment]。
     * @see Application.environment
     */
    @Bean
    public fun simbotApplicationEnvironment(application: Application): Application.Environment = application.environment


    /**
     * 当 [simbotSpringBootApplication] 配置完成后进行 [ApplicationPostProcessor] 的后置处理，
     * 并返回一个标志用对象 [AutoConfigureMarker.AfterApplicationPostProcessor], 用来控制依赖顺序。
     *
     */
    @Bean
    public fun simbotApplicationAfterApplication(
        application: Application,
        @Autowired(required = false) applicationPostProcessors: List<ApplicationPostProcessor>? = null,
    ): AutoConfigureMarker.AfterApplicationPostProcessor {
        runInNoScopeBlocking {
            applicationPostProcessors?.forEach { processor -> processor.processApplication(application) }
        }
        return AutoConfigureMarker.AfterApplicationPostProcessor
    }

    /**
     * 当 [Application] 构建完成后, 提供 [EventListenerManager]。
     * 通过 [AutoConfigureMarker.AfterApplicationPostProcessor] 控制其流程在 [simbotApplicationAfterApplication] 之后。
     * @see Application.eventListenerManager
     */
    @Bean
    public fun simbotApplicationEventListenerManager(
        application: Application,
        @Suppress("unused")
        marker: AutoConfigureMarker.AfterApplicationPostProcessor
    ): EventListenerManager =
        application.eventListenerManager

    /**
     * 当 [Application] 构建完成后, 提供 [BotManagers]。
     * 通过 [AutoConfigureMarker.AfterApplicationPostProcessor] 控制其流程在 [simbotApplicationAfterApplication] 之后。
     * @see Application.botManagers
     */
    @Bean
    public fun simbotApplicationBotManagers(
        application: Application,
        @Suppress("unused")
        marker: AutoConfigureMarker.AfterApplicationPostProcessor
    ): BotManagers = application.botManagers
    // endregion


    public companion object {
        private val logger = LoggerFactory.logger<SimbotSpringBootApplicationConfiguration>()
    }
}

/**
 * 当 [Application] 启动完成后的处理器。
 *
 *
 *
 * @see BlockingApplicationPostProcessor
 */
public interface ApplicationPostProcessor {
    public suspend fun processApplication(application: Application)

}


/**
 * 当 [Application] 启动完成后的阻塞式处理器。
 */
@Api4J
public interface BlockingApplicationPostProcessor : ApplicationPostProcessor {
    public fun processApplicationBlocking(application: Application)

    override suspend fun processApplication(application: Application) {
        processApplicationBlocking(application)
    }
}

/**
 * 标记用类型，用于通过 @Bean 控制依赖加载（注入）顺序。
 */
public sealed class AutoConfigureMarker {
    /**
     * 当 [Application] 创建完成且完成了所有的 [ApplicationPostProcessor] 处理后的标记类。
     *
     */
    public data object AfterApplicationPostProcessor : AutoConfigureMarker()
}

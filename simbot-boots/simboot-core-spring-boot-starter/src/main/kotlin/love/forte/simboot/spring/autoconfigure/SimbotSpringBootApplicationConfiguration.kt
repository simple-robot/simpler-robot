/*
 * Copyright (c) 2022 ForteScarlet <ForteScarlet@163.com>
 *
 * 本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x 、simbot3 等) 的一部分。
 * simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 * 发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 * 你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 * https://www.gnu.org/licenses
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 * https://www.gnu.org/licenses/lgpl-3.0-standalone.html
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
        applicationContext: ApplicationContext,
        applicationArguments: ApplicationArguments,
    ): SpringBootApplicationConfiguration {

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
    public object AfterApplicationPostProcessor : AutoConfigureMarker()
}

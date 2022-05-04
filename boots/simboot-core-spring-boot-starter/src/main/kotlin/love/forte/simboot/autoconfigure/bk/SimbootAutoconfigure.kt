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
 */

package love.forte.simboot.autoconfigure.bk

import love.forte.simboot.SimbootContext
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order

/**
 *
 * @author ForteScarlet
 */
@Import(
    SpringbootCoreBootEntranceContextFactoriesConfiguration::class
)
@Order(Ordered.LOWEST_PRECEDENCE) // The lowest
public open class SimbootAutoconfigure
// :
// ImportBeanDefinitionRegistrar,
// BeanFactoryAware
{
    // private lateinit var beanFactory: ListableBeanFactory

    @Bean
    @ConditionalOnMissingBean(SimbootContext::class)
    public fun runAndConfigSimbotContext(appRunner: SimbootAppRunner): SimbootContext {
        return appRunner.run()
    }

    // override fun registerBeanDefinitions(
    //     importingClassMetadata: AnnotationMetadata,
    //     registry: BeanDefinitionRegistry,
    //     importBeanNameGenerator: BeanNameGenerator
    // ) {
    //
    //
    //     val beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(SimbootContext::class.java) {
    //         val runner = beanFactory.getBean(SimbootAppRunner::class.java)
    //         runner.run()
    //     }
    //         .beanDefinition
    //     val name = importBeanNameGenerator.generateBeanName(beanDefinition, registry)
    //     registry.registerBeanDefinition(name, beanDefinition)
    // }

    // override fun setBeanFactory(beanFactory: BeanFactory) {
    //     this.beanFactory = if (beanFactory is ListableBeanFactory) beanFactory
    //     else throw SimbotIllegalArgumentException("BeanFactory is not ListableBeanFactory.")
    // }


}




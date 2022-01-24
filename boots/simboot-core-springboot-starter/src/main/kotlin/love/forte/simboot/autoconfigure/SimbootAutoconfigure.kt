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

import love.forte.simboot.SimbootContext
import love.forte.simbot.SimbotIllegalArgumentException
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.BeanFactoryAware
import org.springframework.beans.factory.ListableBeanFactory
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanNameGenerator
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar
import org.springframework.core.type.AnnotationMetadata

/**
 *
 * @author ForteScarlet
 */
@Import(
    SpringbootCoreBootEntranceContextFactoriesConfiguration::class
)
public open class SimbootAutoconfigure :
    ImportBeanDefinitionRegistrar,
    BeanFactoryAware {
    private lateinit var beanFactory: ListableBeanFactory

    override fun registerBeanDefinitions(
        importingClassMetadata: AnnotationMetadata,
        registry: BeanDefinitionRegistry,
        importBeanNameGenerator: BeanNameGenerator
    ) {


        val beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(SimbootContext::class.java) {
            val runner = beanFactory.getBean(SimbootAppRunner::class.java)
            runner.run()
        }
            .beanDefinition
        val name = importBeanNameGenerator.generateBeanName(beanDefinition, registry)
        registry.registerBeanDefinition(name, beanDefinition)
    }

    override fun setBeanFactory(beanFactory: BeanFactory) {
        this.beanFactory = if (beanFactory is ListableBeanFactory) beanFactory
        else throw SimbotIllegalArgumentException("BeanFactory is not ListableBeanFactory.")
    }


}




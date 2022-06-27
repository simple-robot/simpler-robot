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

import love.forte.simboot.annotation.Listener
import love.forte.simbot.LoggerFactory
import love.forte.simbot.event.EventListener
import love.forte.simbot.event.EventListenerBuilder
import org.springframework.beans.BeansException
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.AbstractBeanDefinition
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.beans.factory.support.DefaultBeanNameGenerator
import org.springframework.context.ResourceLoaderAware
import org.springframework.core.io.ResourceLoader
import org.springframework.core.type.AnnotationMetadata
import kotlin.reflect.jvm.jvmName

/**
 * 扫描寻找所有返回值类型为 [EventListener] 或 [EventListenerBuilder] 类型，且标记了 [@Listener][Listener] 注解的函数，
 * 并将这些函数同样视为类似于标记了 [@Bean][org.springframework.context.annotation.Bean] 的效果 ———— 将它们追加当依赖环境中，
 * 而不是解析为普通的监听函数。
 *
 */
public open class EventListenerRegistryPostProcessor : BeanDefinitionRegistryPostProcessor, ResourceLoaderAware {
    private lateinit var _loader: ResourceLoader
    override fun setResourceLoader(resourceLoader: ResourceLoader) {
        _loader = resourceLoader
    }
    
    private inline val classLoader: ClassLoader
        get() =
            _loader.classLoader ?: Thread.currentThread().contextClassLoader ?: ClassLoader.getSystemClassLoader()
    
    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
    }
    
    private data class AnnotationMetadataWithBeanName(val metadata: AnnotationMetadata, val beanName: String)
    
    override fun postProcessBeanDefinitionRegistry(registry: BeanDefinitionRegistry) {
        val metadataSet = mutableSetOf<AnnotationMetadataWithBeanName>()
        for (name in registry.beanDefinitionNames) {
            val definition = registry.getBeanDefinition(name)
            if (definition is AnnotatedBeanDefinition) {
                val metadata = definition.metadata
                
                val annotatedMethods = metadata.getAnnotatedMethods(Listener::class.jvmName)
                if (annotatedMethods.isNotEmpty()) {
                    if (definition.factoryMethodMetadata != null) {
                        continue
                    }
                    metadataSet.add(AnnotationMetadataWithBeanName(metadata, name))
                }
            }
        }
        
        logger.debug("Found {} @Listener functions that are suspected to be eligible", metadataSet.size)
        
        for ((metadata, beanName) in metadataSet) {
            for (methodMetadata in metadata.getAnnotatedMethods(Listener::class.jvmName)) {
                val returnTypeName = methodMetadata.returnTypeName
                if (isNotCompliant(returnTypeName)) {
                    continue
                }
                
                if (isCompliant(returnTypeName)) {
                    try {
                        val newDefinition = AnnotatedGenericBeanDefinition(metadata, methodMetadata).apply {
                            this.originatingBeanDefinition
                            autowireMode = AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR
                            factoryBeanName = beanName
                        }
                        val newBeanName = generateBeanName(newDefinition, registry)
                        registry.registerBeanDefinition(newBeanName, newDefinition)
                        logger.debug(
                            "Create new bean definition for EventListener. beanName={}, definition={}",
                            newBeanName,
                            newDefinition
                        )
                    } catch (e: BeansException) {
                        logger.error(
                            "Could not resolve and register the EventListener function bean. beanName={}, metadata={}",
                            beanName,
                            metadata
                        )
                    }
                    
                }
                
            }
        }
    }
    
    /**
     * 是否一眼不合规
     */
    private fun isNotCompliant(returnTypeName: String): Boolean {
        return returnTypeName == "void"
    }
    
    /**
     * 是否合规 —— 是常见的那几个类型，或者属于 [EventListener] 类型或 [EventListenerBuilder] 类型。
     */
    private fun isCompliant(returnTypeName: String): Boolean {
        return kotlin.runCatching {
            val returnType = classLoader.loadClass(returnTypeName)
            EventListener::class.java.isAssignableFrom(returnType)
                    || EventListenerBuilder::class.java.isAssignableFrom(returnType)
        }.getOrElse { e ->
            if (logger.isDebugEnabled) {
                logger.debug("Cannot load class with return type [$returnTypeName] by classLoader [$classLoader]", e)
            }
            false
        }
    }
    
    private fun generateBeanName(definition: BeanDefinition, registry: BeanDefinitionRegistry): String {
        return DefaultBeanNameGenerator.INSTANCE.generateBeanName(definition, registry) + NAME_SUFFIX
    }
    
    public companion object {
        private val logger = LoggerFactory.getLogger<EventListenerRegistryPostProcessor>()
        private const val NAME_SUFFIX = "#simbot#listener"
    }
}
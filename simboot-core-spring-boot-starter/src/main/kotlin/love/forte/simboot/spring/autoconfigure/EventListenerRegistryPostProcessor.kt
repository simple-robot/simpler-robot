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

import love.forte.simboot.annotation.Listener
import love.forte.simbot.event.EventListener
import love.forte.simbot.event.EventListenerBuilder
import love.forte.simbot.event.EventListenerRegistrationDescription
import org.slf4j.LoggerFactory
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
 * 扫描寻找所有返回值类型为 [EventListener] 或 [EventListenerRegistrationDescription] 或 [EventListenerBuilder] 类型，且标记了 [@Listener][Listener] 注解的函数，
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
     * 是否合规 —— 是常见的那几个类型，或者属于 [EventListener] 类型或 [EventListenerBuilder] 类型或 [EventListenerRegistrationDescription] 类型.
     */
    private fun isCompliant(returnTypeName: String): Boolean {
        return kotlin.runCatching {
            val returnType = classLoader.loadClass(returnTypeName)
            EventListener::class.java.isAssignableFrom(returnType)
                    || EventListenerRegistrationDescription::class.java.isAssignableFrom(returnType)
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
        private val logger = LoggerFactory.getLogger(EventListenerRegistryPostProcessor::class.java)
        private const val NAME_SUFFIX = "#simbot#listener"
    }
}

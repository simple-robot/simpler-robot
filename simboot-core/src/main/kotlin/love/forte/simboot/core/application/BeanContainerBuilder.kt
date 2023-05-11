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

package love.forte.simboot.core.application

import love.forte.annotationtool.core.KAnnotationTool
import love.forte.annotationtool.core.getAnnotation
import love.forte.di.Bean
import love.forte.di.BeanContainer
import love.forte.di.annotation.Beans
import love.forte.di.asBean
import love.forte.di.core.*
import love.forte.di.core.internal.AnnotationGetter
import love.forte.simboot.core.utils.scanClass
import love.forte.simbot.InternalSimbotApi
import javax.inject.Named
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.KVisibility
import kotlin.reflect.cast

/**
 *
 * 在 [BootApplicationBuilder] 中通过 [BootApplicationBuilder.beans] 配置依赖管理信息。
 *
 * ```kotlin
 *  beans {
 *      bean(name, instance)
 *      beanBy(name) { Foo() }
 *      bean(name, Foo::class) { Foo() }
 *      scan("com.example1", "com.example2")
 *  }
 * ```
 */
public interface BeanContainerBuilder {
    /**
     * 增加一个后置处理器。
     *
     * 此项源于 [CoreBeanManagerConfiguration].
     */
    public fun postProcessor(processor: CoreBeanManagerBeanRegisterPostProcessor)
    
    /**
     * 设置父级container。
     *
     * 此项源于 [CoreBeanManagerConfiguration].
     */
    public var parentContainer: BeanContainer
    
    /**
     * 向当前依赖管理中增加一个bean。
     */
    public fun <T : Any> bean(name: String, bean: Bean<out T>): Bean<out T>
    
    /**
     * 向当前依赖管理中增加一个单例bean。
     */
    public fun <T : Any> bean(name: String, instance: T): Bean<out T>
    
    /**
     * 向当前依赖管理中增加一个bean构建器。
     *
     * 此bean每次获取都会通过此函数进行构建。
     */
    public fun <T : Any> bean(name: String, type: KClass<T>, factory: () -> T): Bean<out T>
    
    /**
     * 扫描指定的包路径并增加所有标记了 [@Beans][Beans] (或者说是 [@Named][javax.inject.Named]) 的类。
     *
     * @see BeanContainer
     */
    public fun scan(vararg targetPackages: String)
    
    /**
     * 扫描指定的包路径并增加所有标记了 [@Beans][Beans] (或者说是 [@Named][javax.inject.Named]) 的类。
     *
     * @see BeanContainer
     */
    public fun scan(
        classLoader: ClassLoader,
        vararg targetPackages: String,
    )
    
    /**
     * 扫描指定的包路径并增加所有标记了 [@Beans][Beans] (或者说是 [@Named][javax.inject.Named]) 的类。
     *
     * @see BeanContainer
     */
    public fun scan(targetPackages: List<String>)
    
    /**
     * 扫描指定的包路径并增加所有标记了 [@Beans][Beans] (或者说是 [@Named][javax.inject.Named]) 的类。
     *
     * @see BeanContainer
     */
    public fun scan(
        classLoader: ClassLoader,
        targetPackages: List<String>,
    )
}


/**
 *
 * 注册一个bean。
 *
 * e.g.
 * ```kotlin
 * beanBy("foo") { Foo() }
 * ```
 */
public inline fun <reified T : Any> BeanContainerBuilder.beanBy(
    name: String,
    crossinline factory: () -> T,
): Bean<out T> {
    return bean(name, T::class) { factory() }
}


internal class BeanContainerBuilderImpl(
    private val tool: KAnnotationTool,
    private val defaultClassLoader: ClassLoader,
    private val configuration: BootApplicationConfiguration,
) : BeanContainerBuilder {
    private val annotationTool = KAnnotationTool()
    private val annotationGetter = AnnotationToolGetter(annotationTool)
    
    private val beans = mutableListOf<BeanWithName<*>>()
    
    private data class BeanWithName<T : Any>(val name: String, val bean: Bean<T>)
    
    private val classesScans = mutableListOf<() -> Sequence<KClass<*>>>()
    
    private val processors: MutableList<CoreBeanManagerBeanRegisterPostProcessor> = mutableListOf()
    
    override fun postProcessor(processor: CoreBeanManagerBeanRegisterPostProcessor) {
        processors.add(processor)
    }
    
    override var parentContainer: BeanContainer = BeanContainer
    
    
    override fun <T : Any> bean(name: String, bean: Bean<out T>): Bean<out T> {
        beans.add(BeanWithName(name, bean))
        return bean
    }
    
    override fun <T : Any> bean(name: String, instance: T): Bean<out T> {
        val bean = instance.asBean()
        bean(name, bean)
        return bean
    }
    
    override fun <T : Any> bean(name: String, type: KClass<T>, factory: () -> T): Bean<out T> {
        val bean = SimpleBeanBuilder(type).apply {
            isSingleton = false
            factory(factory)
        }.build()
        bean(name, bean)
        return bean
    }
    
    override fun scan(classLoader: ClassLoader, vararg targetPackages: String) {
        scan(classLoader, targetPackages.toList())
    }
    
    override fun scan(targetPackages: List<String>) {
        scan(defaultClassLoader, targetPackages)
    }
    
    override fun scan(vararg targetPackages: String) {
        scan(defaultClassLoader, targetPackages = targetPackages)
    }
    
    @OptIn(InternalSimbotApi::class)
    override fun scan(classLoader: ClassLoader, targetPackages: List<String>) {
        val logger = configuration.logger
        classesScans.add {
            scanClass(classLoader, targetPackages, { e, className ->
                logger.warn("Class [{}] failed to load and will be skipped.", className)
                if (logger.isDebugEnabled) {
                    logger.debug("Reason for failure: $e", e)
                }
                null
            }) {
                mapNotNull { c ->
                    kotlin.runCatching { c.kotlin }.getOrElse {
                        logger.warn("The class [{}] cannot resolve to KClass and will be skipped.", c)
                        null
                    }
                }.filter { k ->
                    val isPublic = runCatching { k.visibility == KVisibility.PUBLIC }.getOrDefault(false)
                    if (!isPublic) {
                        logger.warn("The visibility of kClass [{}] is not PUBLIC and will be skipped.", k)
                    }
                    
                    val named = kotlin.runCatching { tool.getAnnotation<Named>(k) }.getOrNull()
                    
                    named != null
                }
            }
        }
    }
    
    fun build(): CoreBeanManager {
        val manager = coreBeanManager {
            processors.addAll(this@BeanContainerBuilderImpl.processors)
            parentContainer = this@BeanContainerBuilderImpl.parentContainer
        }
        beans.forEach { (name, bean) ->
            manager.register(name, bean)
        }
        val classRegistrar = coreBeanClassRegistrar(annotationGetter)
        val classes = classesScans.flatMap { it() }
        classRegistrar.register(types = classes.toSet().toTypedArray())
        classRegistrar.inject(manager)
        
        return manager
    }
}


private class AnnotationToolGetter(private val tool: KAnnotationTool) : AnnotationGetter {
    override fun <T : Annotation> containsAnnotation(
        element: KAnnotatedElement,
        annotationType: KClass<T>,
    ): Boolean {
        return tool.getAnnotation(element, annotationType) != null
    }
    
    override fun <R : Any> getAnnotationProperty(
        element: KAnnotatedElement,
        annotationType: KClass<out Annotation>,
        name: String,
        propertyType: KClass<R>,
    ): R? {
        val annotation = tool.getAnnotation(element, annotationType) ?: return null
        val values = tool.getAnnotationValues(annotation)
        return values[name]?.let { propertyType.cast(it) }
    }
    
    override fun <R : Any> getAnnotationsProperties(
        element: KAnnotatedElement,
        annotationType: KClass<out Annotation>,
        name: String,
        propertyType: KClass<R>,
    ): List<R> {
        return tool.getAnnotations(element, annotationType).mapNotNull {
            tool.getAnnotationValues(it)[name]?.let { v -> propertyType.cast(v) }
        }
    }
}



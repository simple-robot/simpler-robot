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

package love.forte.simboot.core.application

import love.forte.annotationtool.core.KAnnotationTool
import love.forte.di.Bean
import love.forte.di.BeanContainer
import love.forte.di.asBean
import love.forte.di.core.CoreBeanManagerBeanRegisterPostProcessor
import love.forte.di.core.SimpleBeanBuilder
import love.forte.di.core.coreBeanClassRegistrar
import love.forte.di.core.coreBeanManager
import love.forte.di.core.internal.AnnotationGetter
import love.forte.simboot.core.internal.ResourcesScanner
import love.forte.simboot.core.internal.visitJarEntry
import love.forte.simboot.core.internal.visitPath
import love.forte.simbot.SimbotIllegalStateException
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
     * 扫描指定的包路径并增加所有标记 [Beans]
     */
    public fun scan(
        classLoader: ClassLoader = BeanContainerBuilder::class.java.classLoader,
        vararg targetPackages: String
    )
}


internal class BeanContainerBuilderImpl(private val configuration: BootApplicationConfiguration) :
    BeanContainerBuilder {
    private val annotationTool = KAnnotationTool()
    private val annotationGetter = AnnotationToolGetter(annotationTool)

    private val beans = mutableListOf<BeanWithName<*>>()

    private data class BeanWithName<T : Any>(val name: String, val bean: Bean<T>)

    private val classesScans = mutableListOf<() -> Collection<KClass<*>>>()

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
        val pathReplace = Regex("[/\\\\]")
        classesScans.add {
            ResourcesScanner<KClass<*>>(classLoader).use { scanner ->
                for (scanPkg in targetPackages) {
                    val scanPath = scanPkg.replace(".", "/")
                    scanner.scan(scanPath)
                    scanner.glob("$scanPath**.class")
                }
                scanner.visitJarEntry { entry, _ ->
                    val classname = entry.name.replace(pathReplace, ".").substringBeforeLast(".class")
                    val loadClass = runCatching {
                        scanner.classLoader.loadClass(classname)
                    }.getOrElse { e ->
                        configuration.logger.warn("Class [{}] failed to load and will be skipped.", classname)
                        if (configuration.logger.isDebugEnabled) {
                            configuration.logger.debug("Reason for failure: $e", e)
                        }
                        null
                    }
                    if (loadClass != null) {
                        sequenceOf(loadClass.kotlin)
                    } else {
                        emptySequence()
                    }
                }
                    .visitPath { (_, r) ->
                        // '/Xxx.class'
                        val classname = r.replace(pathReplace, ".").substringBeforeLast(".class").let {
                            if (it.startsWith(".")) it.substring(1) else it
                        }
                        val loadClass = runCatching {
                            scanner.classLoader.loadClass(classname)
                        }.getOrElse { e -> throw SimbotIllegalStateException("Class load filed: $classname", e) }
                        sequenceOf(loadClass.kotlin)
                    }
                    .collectSequence(true)
                    /* Packages and file facades are not yet supported in Kotlin reflection. Meanwhile please use Java reflection to inspect this class: class ResourceGetTestKt */
                    .filter { k -> runCatching { k.visibility == KVisibility.PUBLIC }.getOrDefault(false) }
                    .toList()
            }
        }
    }

    fun build(): BeanContainer {
        val manager = coreBeanManager {
            processors.addAll(this@BeanContainerBuilderImpl.processors)
            parentContainer = this@BeanContainerBuilderImpl.parentContainer
        }
        beans.forEach { (name, bean) ->
            manager.register(name, bean)
        }
        val classRegistrar = coreBeanClassRegistrar(annotationGetter)
        val classes = classesScans.flatMap { it() }
        classRegistrar.register(*classes.toTypedArray())
        classRegistrar.inject(manager)

        return manager
    }
}


private class AnnotationToolGetter(private val tool: KAnnotationTool) : AnnotationGetter {
    override fun <T : Annotation> containsAnnotation(
        element: KAnnotatedElement,
        annotationType: KClass<T>
    ): Boolean {
        return tool.getAnnotation(element, annotationType) != null
    }

    override fun <R : Any> getAnnotationProperty(
        element: KAnnotatedElement,
        annotationType: KClass<out Annotation>,
        name: String,
        propertyType: KClass<R>
    ): R? {
        val annotation = tool.getAnnotation(element, annotationType) ?: return null
        val values = tool.getAnnotationValues(annotation)
        return values[name]?.let { propertyType.cast(it) }
    }

    override fun <R : Any> getAnnotationsProperties(
        element: KAnnotatedElement,
        annotationType: KClass<out Annotation>,
        name: String,
        propertyType: KClass<R>
    ): List<R> {
        return tool.getAnnotations(element, annotationType).mapNotNull {
            tool.getAnnotationValues(it)[name]?.let { v -> propertyType.cast(v) }
        }
    }
}



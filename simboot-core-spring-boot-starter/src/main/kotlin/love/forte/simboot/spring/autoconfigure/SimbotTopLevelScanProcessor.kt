/*
 * Copyright (c) 2022-2023 ForteScarlet <ForteScarlet@163.com>
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

import love.forte.di.BeanContainer
import love.forte.simboot.annotation.Binder
import love.forte.simboot.annotation.Listener
import love.forte.simboot.core.binder.BinderManager
import love.forte.simboot.core.listener.FunctionalListenerProcessContext
import love.forte.simboot.core.listener.KFunctionListenerProcessor
import love.forte.simboot.listener.ParameterBinderFactory
import love.forte.simbot.InternalSimbotApi
// import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.event.EventListener
import love.forte.simbot.event.EventListenerBuilder
import love.forte.simbot.event.EventListenerRegistrationDescription
import love.forte.simbot.event.EventListenerRegistrationDescription.Companion.toRegistrationDescription
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.BeansException
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition
import org.springframework.beans.factory.support.*
import org.springframework.context.EnvironmentAware
import org.springframework.context.ResourceLoaderAware
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar
import org.springframework.core.MethodIntrospector
import org.springframework.core.annotation.AnnotationAttributes
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.core.env.Environment
import org.springframework.core.io.ResourceLoader
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.core.type.AnnotationMetadata
import org.springframework.core.type.StandardMethodMetadata
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory
import org.springframework.util.ClassUtils
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName
import kotlin.reflect.jvm.kotlinFunction

/**
 * 顶层函数的扫描、处理器。用于扫描并处理各种指定类型的Kotlin顶层函数。
 *
 * @see SimbotTopLevelBinderScanProcessor
 * @see SimbotTopLevelListenerScanProcessor
 */
@InternalSimbotApi
public sealed class AbstractSimbotTopLevelScanProcessor : ImportBeanDefinitionRegistrar, EnvironmentAware,
    ResourceLoaderAware {
    protected abstract val annotationType: KClass<out Annotation>
    protected abstract val methodAnnotationType: KClass<out Annotation>
    protected abstract val annotationPackageAttributeName: String
    protected lateinit var lateEnvironment: Environment
    protected lateinit var lateResourceLoader: ResourceLoader
    
    override fun setEnvironment(environment: Environment) {
        this.lateEnvironment = environment
    }
    
    override fun setResourceLoader(resourceLoader: ResourceLoader) {
        this.lateResourceLoader = resourceLoader
    }
    
    override fun registerBeanDefinitions(
        importingClassMetadata: AnnotationMetadata,
        registry: BeanDefinitionRegistry,
        importBeanNameGenerator: BeanNameGenerator,
    ) {
        val scanPackages = importingClassMetadata.toScanPackages()
        val globs = scanPackages.map {
            it.replace(".", "/") + "/**/*.class"
        }
        val topFunctionSequence = topFunctions(lateResourceLoader, globs)
        
        process(Context(importingClassMetadata, registry, importBeanNameGenerator, topFunctionSequence))
    }
    
    private fun AnnotationMetadata.toScanPackages(): Set<String> {
        val attributes = AnnotationAttributes
            .fromMap(getAnnotationAttributes(annotationType.java.name))
            ?: return emptySet()
        
        val value = attributes.getStringArray("value")
        return value.toSet().ifEmpty { setOf(ClassUtils.getPackageName(className)) }
    }
    
    protected data class Context(
        val importingClassMetadata: AnnotationMetadata,
        val registry: BeanDefinitionRegistry,
        val importBeanNameGenerator: BeanNameGenerator,
        val topFunctionSequence: Sequence<AnnotationMetadata>,
    )
    
    protected abstract fun process(context: Context)
}

/**
 * 标记配置需要进行扫描的顶层监听函数。
 *
 * ```java
 * @SimbotTopLevelListenerScan({"com.example.foo", "com.example.bar"})
 * public class FooConfiguration {
 *    // ...
 * }
 * ```
 *
 * ```kotlin
 * @SimbotTopLevelListenerScan(["com.example.foo", "com.example.bar"])
 * open class FooConfiguration
 * ```
 *
 *
 */
// TODO
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
@Import(SimbotTopLevelListenerScanProcessor::class)
public annotation class SimbotTopLevelListenerScan(
    val value: Array<String> = [],
)


// TODO

/**
 * 顶层listener扫描处理器。
 *
 */
@OptIn(InternalSimbotApi::class)
public class SimbotTopLevelListenerScanProcessor : AbstractSimbotTopLevelScanProcessor() {
    private val logger = LoggerFactory.getLogger(SimbotTopLevelListenerScanProcessor::class.java)
    override val annotationType: KClass<SimbotTopLevelListenerScan> get() = SimbotTopLevelListenerScan::class
    override val methodAnnotationType: KClass<Listener> get() = Listener::class
    override val annotationPackageAttributeName: String get() = "value"
    
    @OptIn(InternalSimbotApi::class)
    override fun process(context: Context) {
        val (_, registry, beanNameGenerator, topFunctionSequence) = context
        
        val eventListenerTypeName = EventListener::class.jvmName
        val eventListenerRegistrationDescriptionTypeName = EventListenerRegistrationDescription::class.jvmName
        val eventListenerBuilderTypeName = EventListenerBuilder::class.jvmName
        val processedMethod = mutableSetOf<Method>()
        
        topFunctionSequence.forEach { annotationMetadata ->
            annotationMetadata.getAnnotatedMethods(methodAnnotationType.jvmName).forEach f@{ methodMetadata ->
                if (methodMetadata.isOverridable) {
                    return@f
                }
                if (!methodMetadata.isStatic) {
                    return@f
                }
                
                
                // 需要是：公开、静态的函数。
                val returnTypeName = methodMetadata.returnTypeName
                var type: Class<*>? = null
                fun getType(): Class<*> {
                    return type ?: ClassUtils.forName(returnTypeName, lateResourceLoader.classLoader).also {
                        type = it
                    }
                }
                
                if (
                    (returnTypeName == eventListenerTypeName || EventListener::class.java.isAssignableFrom(getType())) ||
                    (returnTypeName == eventListenerRegistrationDescriptionTypeName || EventListenerRegistrationDescription::class.java.isAssignableFrom(getType())) ||
                    (returnTypeName == eventListenerBuilderTypeName || EventListenerBuilder::class.java.isAssignableFrom(
                        getType()
                    ))
                ) {
                    try {
                        val beanDefinition = AnnotatedGenericBeanDefinition(annotationMetadata, methodMetadata).apply {
                            autowireMode = AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR
                        }
                        val beanName = DefaultBeanNameGenerator.INSTANCE.generateBeanName(
                            beanDefinition,
                            registry
                        ) + "#${methodMetadata.methodName}#GENERATED_TOP_LISTENER"
                        registry.registerBeanDefinition(beanName, beanDefinition)
                        logger.debug(
                            "Created new bean definition for top-level EventListener. beanName={}, definition={}",
                            beanName,
                            beanDefinition
                        )
                    } catch (be: BeansException) {
                        logger.error(
                            "Could not resolve and register the top-level EventListener function bean. metadata={}",
                            annotationMetadata, be
                        )
                    }
                } else {
                    if (methodMetadata is StandardMethodMetadata) {
                        val method = methodMetadata.introspectedMethod
                        if (method in processedMethod) {
                            return@f
                        }
                        
                        val listenerBuilder = resolveMethodToListenerBuilder(
                            method,
                            AnnotationUtils.findAnnotation(method, Listener::class.java)!!,
                            logger
                        )
                        
                        val beanDefinition =
                            BeanDefinitionBuilder.genericBeanDefinition(TopLevelEventListenerBuilder::class.java) { listenerBuilder }.beanDefinition
                        val beanName = beanNameGenerator.generateBeanName(
                            beanDefinition,
                            registry
                        ) + "#${methodMetadata.methodName}#GENERATED_TOP_LISTENER_FUNCTION"
                        
                        registry.registerBeanDefinition(beanName, beanDefinition)
                        processedMethod.add(method)
                        logger.debug(
                            "Created new bean definition for top-level EventListener via function. beanName={}, definition={}",
                            beanName,
                            beanDefinition
                        )
                    } else {
                        // normal listener method, get method
                        val targetMethods: Map<Method, Listener> =
                            MethodIntrospector.selectMethods(getType(), MethodIntrospector.MetadataLookup {
                                if (it.name != methodMetadata.methodName || it.returnType.name != returnTypeName) {
                                    return@MetadataLookup null
                                }
                                
                                AnnotationUtils.findAnnotation(it, Listener::class.java)
                            })
                        
                        targetMethods.forEach f2@{ method, listenerAnnotation ->
                            if (method in processedMethod) {
                                return@f2
                            }
                            val listenerBuilder = resolveMethodToListenerBuilder(
                                method,
                                listenerAnnotation,
                                logger
                            )
                            val beanDefinition =
                                BeanDefinitionBuilder.genericBeanDefinition(TopLevelEventListenerBuilder::class.java) { listenerBuilder }.beanDefinition
                            val beanName = DefaultBeanNameGenerator.INSTANCE.generateBeanName(
                                beanDefinition,
                                registry
                            ) + "#${methodMetadata.methodName}#GENERATED_TOP_LISTENER_FUNCTION"
                            registry.registerBeanDefinition(beanName, beanDefinition)
                            processedMethod.add(method)
                            logger.debug(
                                "Created new bean definition for top-level EventListener via function. beanName={}, definition={}",
                                beanName,
                                beanDefinition
                            )
                        }
                    }
                }
            }
        }
    }
    
    @OptIn(InternalSimbotApi::class)
    private fun resolveMethodToListenerBuilder(
        method: Method, listenerAnnotation: Listener, logger: Logger,
    ): TopLevelEventListenerBuilder? {
        if (!Modifier.isPublic(method.modifiers)) {
            logger.warn(
                "The modifier of method [{}] is not PUBLIC. This method will not be resolved to EventListener instance.",
                method
            )
            return null
        }
        
        val function = kotlin.runCatching { method.kotlinFunction }.getOrNull()
        if (function == null) {
            logger.debug("Cannot resolve method [{}] to kotlin function. Skip it.", method)
            return null
        }
        
        return TopLevelEventListenerBuilder { listenerProcessor, binderManager, beanContainer ->
            listenerProcessor.process(
                FunctionalListenerProcessContext(
                    function = function,
                    binderManager = binderManager,
                    beanContainer = beanContainer,
                )
            ).toRegistrationDescription(priority = listenerAnnotation.priority, isAsync = listenerAnnotation.async)
        }
    }
}

/**
 * 顶层监听函数解析后向容器中注册的构建函数。
 *
 * 用于后续的配置类提供进一步所需信息并进行最终构建。
 */
@InternalSimbotApi
public fun interface TopLevelEventListenerBuilder {
    public fun build(
        listenerProcessor: KFunctionListenerProcessor,
        binderManager: BinderManager,
        beanContainer: BeanContainer,
    ): EventListenerRegistrationDescription
}

/**
 * 标记配置需要进行扫描的顶层binder函数。
 *
 * ```java
 * @SimbotTopLevelBinderScan({"com.example.foo", "com.example.bar"})
 * public class FooConfiguration {
 *    // ...
 * }
 * ```
 *
 * ```kotlin
 * @SimbotTopLevelBinderScan(["com.example.foo", "com.example.bar"])
 * open class FooConfiguration
 * ```
 *
 *
 */
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
@Import(SimbotTopLevelBinderScanProcessor::class)
public annotation class SimbotTopLevelBinderScan(
    val value: Array<String> = [],
)


/**
 * 顶层binder扫描处理器
 *
 */
@OptIn(InternalSimbotApi::class)
public class SimbotTopLevelBinderScanProcessor : AbstractSimbotTopLevelScanProcessor() {
    private val logger = LoggerFactory.getLogger(SimbotTopLevelBinderScanProcessor::class.java)
    override val annotationType: KClass<out Annotation> get() = SimbotTopLevelBinderScan::class
    override val methodAnnotationType: KClass<out Annotation> get() = Binder::class
    override val annotationPackageAttributeName: String get() = "value"
    
    override fun process(context: Context) {
        val (_, registry, beanNameGenerator, topFunctionSequence) = context
        
        topFunctionSequence.forEach { annotationMetadata ->
            annotationMetadata.getAnnotatedMethods(methodAnnotationType.jvmName).forEach f@{ methodMetadata ->
                if (methodMetadata.isOverridable) {
                    return@f
                }
                if (!methodMetadata.isStatic) {
                    return@f
                }
                
                
                // 需要是：公开、静态的函数。
                val returnTypeName = methodMetadata.returnTypeName
                var type: Class<*>? = null
                fun getType(): Class<*> {
                    return type ?: ClassUtils.forName(returnTypeName, lateResourceLoader.classLoader).also {
                        type = it
                    }
                }
                
                val parameterBinderFactoryTypeName = ParameterBinderFactory::class.java.name
                
                if (returnTypeName == parameterBinderFactoryTypeName || ParameterBinderFactory::class.java.isAssignableFrom(
                        getType()
                    )
                ) {
                    try {
                        val beanDefinition = AnnotatedGenericBeanDefinition(annotationMetadata, methodMetadata).apply {
                            autowireMode = AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR
                        }
                        val beanName = beanNameGenerator.generateBeanName(
                            beanDefinition,
                            registry
                        ) + "#GENERATED_TOP_BINDER_FACTORY"
                        registry.registerBeanDefinition(beanName, beanDefinition)
                        logger.debug(
                            "Created new bean definition for top-level ParameterBinderFactory. beanName={}, definition={}",
                            beanName,
                            beanDefinition
                        )
                    } catch (be: BeansException) {
                        logger.error(
                            "Could not resolve and register the top-level ParameterBinderFactory function bean. metadata={}",
                            annotationMetadata,
                            be
                        )
                    }
                }
                // TODO else?
            }
        }
    }
}

private fun topFunctions(
    resourceLoader: ResourceLoader,
    globs: Collection<String>,
): Sequence<AnnotationMetadata> {
    val scanner = PathMatchingResourcePatternResolver(resourceLoader)
    val readerFactory = SimpleMetadataReaderFactory(resourceLoader)
    
    return globs.asSequence().flatMap { glob ->
        scanner.getResources(glob).asSequence()
    }.mapNotNull { r ->
        runCatching {
            readerFactory.getMetadataReader(r).annotationMetadata
        }.getOrNull()
    }.filter { it.isTopClass() }
}

private fun AnnotationMetadata.isTopClass(): Boolean = kotlin.runCatching {
    if (!hasAnnotation("kotlin.Metadata")) {
        return false
    }
    if (!isFinal) {
        return false
    }
    if (!isConcrete) {
        return false
    }
    if (isAnnotation) {
        return false
    }
    if (!isIndependent) {
        return false
    }
    if (interfaceNames.isNotEmpty()) {
        return false
    }
    if (superClassName != null && superClassName != "java.lang.Object") {
        return false
    }
    if (hasEnclosingClass()) {
        return false
    }
    true
}.getOrElse { false }

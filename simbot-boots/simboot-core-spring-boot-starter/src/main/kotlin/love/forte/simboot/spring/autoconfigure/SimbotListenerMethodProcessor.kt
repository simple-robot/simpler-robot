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

import love.forte.simboot.annotation.Binder
import love.forte.simboot.annotation.Listener
import love.forte.simboot.annotation.scopeIfDefault
import love.forte.simboot.core.binder.BinderManager
import love.forte.simboot.core.binder.CoreBinderManager
import love.forte.simboot.core.listener.FunctionalListenerProcessContext
import love.forte.simboot.core.listener.KFunctionListenerProcessor
import love.forte.simboot.core.utils.sign
import love.forte.simboot.listener.ParameterBinderFactory
import love.forte.simboot.spring.autoconfigure.utils.SpringAnnotationTool
import love.forte.simbot.LoggerFactory
import love.forte.simbot.SimbotIllegalStateException
import love.forte.simbot.event.EventListener
import love.forte.simbot.event.EventListenerBuilder
import org.slf4j.Logger
import org.springframework.aop.framework.autoproxy.AutoProxyUtils
import org.springframework.aop.scope.ScopedObject
import org.springframework.aop.scope.ScopedProxyUtils
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.getBean
import org.springframework.beans.factory.getBeanNamesForType
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.core.MethodIntrospector
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.core.annotation.AnnotationUtils
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.kotlinFunction


/**
 *
 * @author ForteScarlet
 */
public class SimbotListenerMethodProcessor : ApplicationContextAware, BeanFactoryPostProcessor,
    BeanDefinitionRegistryPostProcessor {
    private lateinit var applicationContext: ApplicationContext
    private lateinit var beanContainer: SpringBeanContainer
    private lateinit var binderManager: BinderManager
    private lateinit var registry: BeanDefinitionRegistry
    private val logger = LoggerFactory.getLogger<SimbotListenerMethodProcessor>()
    
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
        this.beanContainer = SpringBeanContainer(applicationContext)
    }
    
    override fun postProcessBeanDefinitionRegistry(registry: BeanDefinitionRegistry) {
        this.registry = registry
    }
    
    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        this.binderManager = resolveBinderManager(beanFactory)
        val beanNames = beanFactory.getBeanNamesForType(Any::class.java)
        for (beanName in beanNames) {
            if (ScopedProxyUtils.isScopedTarget(beanName)) {
                continue
            }
            
            val beanType = beanFactory.getTargetTypeSafely(beanName) ?: continue
            
            kotlin.runCatching {
                processListener(beanName, beanType)
            }
        }
        
        // Top level listener?
    }
    
    private fun resolveBinderManager(beanFactory: ConfigurableListableBeanFactory): BinderManager {
        val globalBinderFactories: MutableList<ParameterBinderFactory> = mutableListOf()
        val idBinderFactories: MutableMap<String, ParameterBinderFactory> = mutableMapOf()
        
        instanceBinders(beanFactory, globalBinderFactories, idBinderFactories)
        functionalBinders(beanFactory, globalBinderFactories, idBinderFactories)
        
        val binderManager = CoreBinderManager(globalBinderFactories, idBinderFactories)
        
        // TODO..?
        // val topBinderPackages = configuration.topLevelBinderScanPackage
        // if (topBinderPackages.isNotEmpty()) {
        //     SimbotSpringBootListenerAutoRegisterBuildConfigure.logger.debug(
        //         "Resolving top-level function binder in {}", topBinderPackages
        //     )
        //     resolveTopLevelManagerTo(
        //         configuration.classLoader,
        //         configuration.topLevelBinderScanPackage,
        //         globalBinderFactories,
        //         idBinderFactories,
        //         binderManager
        //     )
        // } else {
        //     SimbotSpringBootListenerAutoRegisterBuildConfigure.logger.debug("Top-level binder package scan target is empty.")
        // }
        
        
        return binderManager
    }
    
    
    private fun instanceBinders(
        beanFactory: ConfigurableListableBeanFactory,
        globalBinderFactories: MutableList<ParameterBinderFactory>,
        idBinderFactories: MutableMap<String, ParameterBinderFactory>,
    ) {
        val binderInstanceNames = beanFactory.getBeanNamesForType<ParameterBinderFactory>()
        
        for (name in binderInstanceNames) {
            val jType = beanFactory.getTargetTypeSafely(name) ?: continue
            val binderAnnotation = AnnotatedElementUtils.findMergedAnnotation(jType, Binder::class.java)
            
            val id = binderAnnotation?.value?.firstOrNull() ?: name
            
            fun global() {
                val globalBinderFactory =
                    beanFactory.getBean<ParameterBinderFactory>(name)  // beanContainer[name, ParameterBinderFactory::class.java]
                globalBinderFactories.add(globalBinderFactory)
            }
            
            fun specify() {
                val specifyBinderFactory =
                    beanFactory.getBean<ParameterBinderFactory>(name) // beanContainer[name, ParameterBinderFactory::class.java]
                idBinderFactories.merge(id, specifyBinderFactory) { old, curr ->
                    throw SimbotIllegalStateException("Duplicate binder factory id [$id]: old [$old] vs current [$curr]")
                }
            }
            
            if (binderAnnotation == null) {
                global()
            } else {
                val scope = binderAnnotation.scopeIfDefault { Binder.Scope.SPECIFY }
                if (scope == Binder.Scope.CURRENT) {
                    // 通过类直接实现的BinderFactory的scope不能为 CURRENT
                    throw SimbotIllegalStateException("The scope of the BinderFactory directly implemented by the class cannot be CURRENT, but $binderAnnotation of type [$jType] named [$name]")
                }
                if (scope == Binder.Scope.GLOBAL) {
                    global()
                } else {
                    specify()
                }
            }
        }
    }
    
    private fun functionalBinders(
        beanFactory: ConfigurableListableBeanFactory,
        globalBinderFactories: MutableList<ParameterBinderFactory>,
        idBinderFactories: MutableMap<String, ParameterBinderFactory>,
    ) {
        // val binderInstanceNames =
        val beanNamesForType = beanFactory.getBeanNamesForType<Any>()
        for (name in beanNamesForType) {
            val jClass = beanFactory.getTargetTypeSafely(name) ?: continue
            if (!AnnotationUtils.isCandidateClass(jClass, Binder::class.java)) {
                continue
            }
            
            val annotatedMethods: Map<Method, Binder> = jClass.selectMethodsSafely() ?: continue // logger?
            
            val context = applicationContext
            
            annotatedMethods.forEach { method, binderAnnotation ->
                val scope = binderAnnotation.scope
                
                // skip if scope == CURRENT.
                if (scope == Binder.Scope.CURRENT) {
                    return@forEach
                }
                
                val function = method.getKotlinFunctionSafely() ?: return@forEach
                val id = binderAnnotation.value.firstOrNull()
                
                fun global() {
                    globalBinderFactories.add(binderManager.resolveFunctionToBinderFactory(function) {
                        context.getBean(name)
                    })
                }
                
                fun specify(id: String) {
                    val binderFactory = binderManager.resolveFunctionToBinderFactory(function) {
                        context.getBean(name)
                    }
                    idBinderFactories.merge(id, binderFactory) { old, curr ->
                        throw SimbotIllegalStateException("Duplicate binder factory id [$id]: old [$old] vs current [$curr]")
                    }
                }
                
                when (scope) {
                    Binder.Scope.DEFAULT -> {
                        if (id != null) {
                            specify(id)
                        } else {
                            global()
                        }
                    }
                    
                    Binder.Scope.SPECIFY -> {
                        if (id == null) {
                            throw SimbotIllegalStateException("The scope of binder [$binderAnnotation] on function [$function] is SPECIFY, but the required property id (Binder.value) is null.")
                        }
                        specify(id)
                    }
                    
                    else -> {
                        // is global~
                        global()
                    }
                }
            }
        }
    }
    
    
    private fun processListener(beanName: String, beanType: Class<*>) {
        if (!AnnotationUtils.isCandidateClass(beanType, Listener::class.java)) {
            return
        }
        
        val annotatedMethods: Map<Method, Listener> = beanType.selectMethodsSafely() ?: return
        
        val tool = SpringAnnotationTool()
        val listenerProcessor = KFunctionListenerProcessor(tool)
        
        
        annotatedMethods.forEach { (method, listenerAnnotation) ->
            val eventListener =
                resolveMethodToListener(beanName, method, listenerAnnotation, listenerProcessor, logger)
                    ?: return@forEach
            
            val beanDefinition = eventListener.resolveToBeanDefinition(beanName)
            
            registry.registerBeanDefinition(eventListener.beanName(beanName), beanDefinition)
        }
        
    }
    
    private fun resolveMethodToListener(
        beanName: String, method: Method, listenerAnnotation: Listener,
        listenerProcessor: KFunctionListenerProcessor, logger: Logger,
    ): EventListener? {
        if (!Modifier.isPublic(method.modifiers)) {
            logger.warn(
                "The modifier of method [{}] is not PUBLIC. This method will not be resolved to EventListener instance.",
                method
            )
            return null
        }
        
        val returnType = method.returnType
        if (EventListenerBuilder::class.java.isAssignableFrom(returnType)) {
            logger.warn(
                "The return type of method [{}] is subclass of [love.forte.simbot.event.EventListenerBuilder] and this method will not be resolved to EventListener instance. ",
                method
            )
            return null
        }
        
        
        if (EventListener::class.java.isAssignableFrom(returnType)) {
            logger.warn(
                "The return type of method [{}] is subclass of [love.forte.simbot.event.EventListener] and this method will not be resolved to EventListener instance. ",
                method
            )
            return null
        }
        
        val function = method.getKotlinFunctionSafely()
        if (function == null) {
            logger.debug("Cannot resolve method [{}] of bean named [{}] to kotlin function. Skip it.", method, beanName)
            return null
        }
        
        val listenerId = listenerAnnotation.id.ifEmpty { "$beanName#${function.sign()}" }
        
        return listenerProcessor.process(
            FunctionalListenerProcessContext(
                id = listenerId,
                function = function,
                priority = listenerAnnotation.priority,
                isAsync = listenerAnnotation.async,
                binderManager = binderManager,
                beanContainer = beanContainer,
            )
        )
    }
    
    
    private fun EventListener.resolveToBeanDefinition(
        beanName: String,
    ): BeanDefinition {
        return BeanDefinitionBuilder.genericBeanDefinition(EventListener::class.java) { this }
            // .addDependsOn(beanName)
            .setPrimary(false)
            .beanDefinition
    }
    
    
    private fun ConfigurableListableBeanFactory.getTargetTypeSafely(beanName: String): Class<*>? {
        val type = kotlin.runCatching { AutoProxyUtils.determineTargetClass(this, beanName) }.getOrElse {
            return null
        }
        
        return if (ScopedObject::class.java.isAssignableFrom(type)) {
            return kotlin.runCatching {
                AutoProxyUtils.determineTargetClass(this, ScopedProxyUtils.getTargetBeanName(beanName))
            }.getOrElse { type }
        } else {
            type
        }
    }
    
    private fun Method.getKotlinFunctionSafely(): KFunction<*>? {
        return kotlin.runCatching { kotlinFunction }.getOrNull()
    }
    
    private inline fun <reified A : Annotation> Class<*>.selectMethodsSafely(): Map<Method, A>? {
        return runCatching {
            MethodIntrospector.selectMethods(this, MethodIntrospector.MetadataLookup<A> { method ->
                AnnotatedElementUtils.findMergedAnnotation(method, A::class.java)
            })
        }.getOrNull()
    }
    
    private fun EventListener.beanName(beanName: String): String = "simbot.listener.$beanName#$id"
    
}


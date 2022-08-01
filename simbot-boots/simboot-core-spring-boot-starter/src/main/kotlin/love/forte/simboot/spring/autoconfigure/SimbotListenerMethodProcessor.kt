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

import love.forte.di.all
import love.forte.simboot.annotation.Binder
import love.forte.simboot.annotation.scopeIfDefault
import love.forte.simboot.core.binder.BinderManager
import love.forte.simboot.core.binder.CoreBinderManager
import love.forte.simboot.listener.ParameterBinderFactory
import love.forte.simboot.spring.autoconfigure.utils.Quadruple
import love.forte.simbot.SimbotIllegalStateException
import org.springframework.aop.framework.autoproxy.AutoProxyUtils
import org.springframework.aop.scope.ScopedObject
import org.springframework.aop.scope.ScopedProxyUtils
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.getBean
import org.springframework.beans.factory.getBeanNamesForType
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.core.annotation.AnnotationUtils
import kotlin.reflect.jvm.kotlinFunction


/**
 *
 * @author ForteScarlet
 */
public class SimbotListenerMethodProcessor : ApplicationContextAware, BeanFactoryPostProcessor {
    private lateinit var applicationContext: ApplicationContext
    private lateinit var binderManager: BinderManager
    
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }
    
    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        this.binderManager = resolveBinderManager(beanFactory)
        val beanNames = beanFactory.getBeanNamesForType(Any::class.java)
        for (beanName in beanNames) {
            if (ScopedProxyUtils.isScopedTarget(beanName)) {
                continue
            }
            
            var type = beanFactory.tryGetTargetType(beanName) ?: continue
            
            kotlin.runCatching {
            
            }
        }
    }
    
    private fun ConfigurableListableBeanFactory.processListener(beanName: String, targetType: Class<*>) {
    
    }
    
    
    private fun resolveBinderManager(beanFactory: ConfigurableListableBeanFactory): BinderManager {
        val globalBinderFactories: MutableList<ParameterBinderFactory> = mutableListOf()
        val idBinderFactories: MutableMap<String, ParameterBinderFactory> = mutableMapOf()
        
        val binderManager = CoreBinderManager(globalBinderFactories, idBinderFactories)
        // region instance binders
        instanceBinders(beanFactory, globalBinderFactories, idBinderFactories)
        // endregion
        
        // region functional binders
        functionalBinders(beanFactory, globalBinderFactories, idBinderFactories)
        // endregion
        
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
            val jType = beanFactory.tryGetTargetType(name) ?: continue
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
            val jType = beanFactory.tryGetTargetType(name) ?: continue
            // TODO @see EventListenerMethodProcessor
            
            
            
            
        }
    
        // beanNamesForType.asSequence().flatMap { name ->
        //     val jClass =
        //         kotlin.runCatching { applicationContext.getType(name) }.getOrNull() ?: return@flatMap emptySequence()
        //     jClass.methods.asSequence().mapNotNull { method ->
        //         val binderAnnotation =
        //             AnnotationUtils.findAnnotation(method, Binder::class.java) ?: return@mapNotNull null
        //
        //         // skip if scope == CURRENT.
        //         if (binderAnnotation.scope == Binder.Scope.CURRENT) {
        //             return@mapNotNull null
        //         }
        //
        //         Quadruple(name, jClass, method, binderAnnotation)
        //     }
        //
        // }.forEach { (name, _, method, binder) ->
        //     val function = kotlin.runCatching { method.kotlinFunction }.getOrNull() ?: return@forEach
        //
        //     // not current
        //     val scope = binder.scope
        //     val id = binder.value.firstOrNull()
        //
        //
        //     fun global() {
        //         globalBinderFactories.add(binderManager.resolveFunctionToBinderFactory(function) {
        //             beanContainer[name]
        //         })
        //     }
        //
        //     fun specify(id: String) {
        //         val binderFactory = binderManager.resolveFunctionToBinderFactory(function) {
        //             beanContainer[name]
        //         }
        //         idBinderFactories.merge(id, binderFactory) { old, curr ->
        //             throw SimbotIllegalStateException("Duplicate binder factory id [$id]: old [$old] vs current [$curr]")
        //         }
        //     }
        //
        //
        //     when (scope) {
        //         Binder.Scope.DEFAULT -> {
        //             if (id != null) {
        //                 specify(id)
        //             } else {
        //                 global()
        //             }
        //         }
        //
        //         Binder.Scope.SPECIFY -> {
        //             if (id == null) {
        //                 throw SimbotIllegalStateException("The scope of binder [$binder] on function [$function] is SPECIFY, but the required property id (Binder.value) is null.")
        //             }
        //             specify(id)
        //         }
        //
        //         else -> {
        //             // is global
        //             global()
        //         }
        //     }
        // }
    }
    
}


private fun ConfigurableListableBeanFactory.tryGetTargetType(beanName: String): Class<*>? {
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
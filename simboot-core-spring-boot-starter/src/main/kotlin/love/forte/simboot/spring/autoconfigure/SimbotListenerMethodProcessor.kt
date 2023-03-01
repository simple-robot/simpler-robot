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

// import love.forte.simbot.logger.LoggerFactory
import love.forte.simboot.annotation.Binder
import love.forte.simboot.annotation.Listener
import love.forte.simboot.annotation.scopeIfDefault
import love.forte.simboot.core.binder.BinderManager
import love.forte.simboot.core.binder.CoreBinderManager
import love.forte.simboot.core.listener.FunctionalListenerProcessContext
import love.forte.simboot.core.listener.KFunctionListenerProcessor
import love.forte.simboot.listener.ParameterBinderFactory
import love.forte.simboot.spring.autoconfigure.utils.SpringAnnotationTool
import love.forte.simbot.InternalSimbotApi
import love.forte.simbot.SimbotIllegalStateException
import love.forte.simbot.event.EventListener
import love.forte.simbot.event.EventListenerBuilder
import love.forte.simbot.event.EventListenerRegistrationDescription
import love.forte.simbot.event.EventListenerRegistrationDescription.Companion.toRegistrationDescription
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.aop.framework.autoproxy.AutoProxyUtils
import org.springframework.aop.scope.ScopedObject
import org.springframework.aop.scope.ScopedProxyUtils
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.getBean
import org.springframework.beans.factory.getBeanNamesForType
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.annotation.ConfigurationClassPostProcessor
import org.springframework.core.MethodIntrospector
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.core.annotation.AnnotationUtils
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.kotlinFunction

// BeanDefinitionRegistryPostProcessor
/**
 *
 * @author ForteScarlet
 */
public class SimbotListenerMethodProcessor : ApplicationContextAware, ConfigurationClassPostProcessor() {
    private lateinit var applicationContext: ApplicationContext
    private lateinit var beanContainer: SpringBeanContainer
    private lateinit var binderManager: BinderManager
    private lateinit var registry: BeanDefinitionRegistry

    private val tool = SpringAnnotationTool()
    private val listenerProcessor = KFunctionListenerProcessor(tool)

    private val globalBinderFactories: MutableList<ParameterBinderFactory> = mutableListOf()
    private val idBinderFactories: MutableMap<String, ParameterBinderFactory> = mutableMapOf()


    private val logger = LoggerFactory.getLogger(SimbotListenerMethodProcessor::class.java)

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

            beanFactory.processListener(beanName, beanType)
        }
    }

    private fun resolveBinderManager(beanFactory: ConfigurableListableBeanFactory): BinderManager {
        instanceBinders(beanFactory, globalBinderFactories, idBinderFactories)
        functionalBinders(beanFactory, globalBinderFactories, idBinderFactories)

        return CoreBinderManager(globalBinderFactories, idBinderFactories)
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


    @OptIn(InternalSimbotApi::class)
    private fun BeanFactory.processListener(beanName: String, beanType: Class<*>) {
        if (TopLevelEventListenerBuilder::class.java.isAssignableFrom(beanType)) {
//            val eventListenerRegistrationDescription = getBean<TopLevelEventListenerBuilder>(beanName)
//                .build(listenerProcessor, binderManager, beanContainer)
//            val beanDefinition = eventListenerRegistrationDescription.resolveToBeanDefinition()

            val beanDefinition = resolveToBeanDefinition {
                getBean<TopLevelEventListenerBuilder>(beanName)
                    .build(listenerProcessor, binderManager, beanContainer)
            }


            registry.registerBeanDefinition("$beanName#BUILT_LISTENER", beanDefinition)
        }

        if (!AnnotationUtils.isCandidateClass(beanType, Listener::class.java)) {
            return
        }

        val annotatedMethods: Map<Method, Listener> = beanType.selectMethodsSafely() ?: return




        annotatedMethods.forEach { (method, listenerAnnotation) ->
            val eventListenerRegistrationDescription =
                resolveMethodToListener(beanName, method, listenerAnnotation, listenerProcessor, logger)
                    ?: return@forEach

//            val beanDefinition = eventListenerRegistrationDescription.resolveToBeanDefinition()
            val beanDefinition = resolveToBeanDefinition(eventListenerRegistrationDescription)

            registry.registerBeanDefinition(generatedListenerBeanName(beanName, method), beanDefinition)
        }

    }

    private fun resolveMethodToListener(
        beanName: String, method: Method, listenerAnnotation: Listener,
        listenerProcessor: KFunctionListenerProcessor, logger: Logger,
    ): (() -> EventListenerRegistrationDescription)? {
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


        if (EventListenerRegistrationDescription::class.java.isAssignableFrom(returnType)) {
            logger.warn(
                "The return type of method [{}] is subclass of [love.forte.simbot.event.EventListenerRegistrationDescription] and this method will not be resolved to EventListener instance. ",
                method
            )
            return null
        }

        val function = method.getKotlinFunctionSafely()
        if (function == null) {
            logger.debug("Cannot resolve method [{}] of bean named [{}] to kotlin function. Skip it.", method, beanName)
            return null
        }

        return {
            listenerProcessor.process(
                FunctionalListenerProcessContext(
                    function = function,
                    binderManager = binderManager,
                    beanContainer = beanContainer,
                )
            ).toRegistrationDescription(priority = listenerAnnotation.priority, isAsync = listenerAnnotation.async)
        }
    }

    private fun resolveToBeanDefinition(instanceSupplier: () -> EventListenerRegistrationDescription): BeanDefinition {
        return BeanDefinitionBuilder.genericBeanDefinition(
            EventListenerRegistrationDescription::class.java,
            instanceSupplier
        ).setPrimary(false).beanDefinition
    }


    private fun ConfigurableListableBeanFactory.getTargetTypeSafely(beanName: String): Class<*>? {
        val type = kotlin.runCatching { AutoProxyUtils.determineTargetClass(this, beanName) }.getOrNull() ?: return null

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
            MethodIntrospector.selectMethods(this, MethodIntrospector.MetadataLookup { method ->
                AnnotatedElementUtils.findMergedAnnotation(method, A::class.java)
            })
        }.getOrNull()
    }

    private fun generatedListenerBeanName(beanName: String, method: Method): String =
        "$beanName${method.toGenericString()}#GENERATED_LISTENER"

}


/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package love.forte.simbot.spring.configuration.binder

import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.quantcat.common.annotations.Binder
import love.forte.simbot.quantcat.common.binder.ParameterBinderFactory
import love.forte.simbot.quantcat.common.binder.SimpleBinderManager
import love.forte.simbot.quantcat.common.binder.toBinderFactory
import love.forte.simbot.spring.configuration.listener.SimbotEventListenerFunctionProcessor
import love.forte.simbot.spring.utils.getKotlinFunctionSafely
import love.forte.simbot.spring.utils.getTargetTypeSafely
import love.forte.simbot.spring.utils.selectMethodsSafely
import org.springframework.aop.scope.ScopedProxyUtils
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.context.annotation.ConfigurationClassPostProcessor
import org.springframework.core.annotation.AnnotationUtils


/**
 * 将标记了 [Binder] 的类型和函数转化为 [ParameterBinderFactory]、
 * 处理 [ParameterBinderManagerBuilderConfigurer] 以及加载所有的 [ParameterBinderFactory]
 *
 * @author ForteScarlet
 */
@AutoConfigureBefore(SimbotEventListenerFunctionProcessor::class)
public open class ResolveBinderManagerProcessor : ConfigurationClassPostProcessor() {
    protected open lateinit var registry: BeanDefinitionRegistry

    override fun postProcessBeanDefinitionRegistry(registry: BeanDefinitionRegistry) {
        this.registry = registry
    }

    @OptIn(ExperimentalSimbotAPI::class)
    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        val builder = ParameterBinderManagerBuilderImpl()
        beanFactory.beanNamesIterator.asSequence()
            .filter { !ScopedProxyUtils.isScopedTarget(it) }
            .forEach { beanName ->
                val beanType = beanFactory.getTargetTypeSafely(beanName) ?: return@forEach

                beanFactory.processToBinderFactoryBuilder(beanName, beanType, builder)
            }

        val simpleManager = SimpleBinderManager(builder.globalBinders, builder.idBinders)
        val beanDefinition =
            BeanDefinitionBuilder.genericBeanDefinition(SimpleBinderManager::class.java) { simpleManager }.apply {
                setPrimary(false)
            }.beanDefinition

        registry.registerBeanDefinition(BINDER_MANAGER_BEAN_NAME, beanDefinition)
    }

    @Suppress("UNCHECKED_CAST")
    private fun ConfigurableListableBeanFactory.processToBinderFactoryBuilder(
        beanName: String,
        beanType: Class<*>,
        builder: ParameterBinderManagerBuilderImpl
    ) {
        if (ParameterBinderFactory::class.java.isAssignableFrom(beanType)) {
            resolveFactoryInstance(beanName, beanType as Class<out ParameterBinderFactory>, builder)
        }

        if (ParameterBinderManagerBuilderConfigurer::class.java.isAssignableFrom(beanType)) {
            resolveConfigurer(beanName, beanType as Class<out ParameterBinderManagerBuilderConfigurer>, builder)
        }

        if (AnnotationUtils.isCandidateClass(beanType, Binder::class.java)) {
            resolveCandidate(beanName, beanType, builder)
        }
    }

    protected open fun ConfigurableListableBeanFactory.resolveFactoryInstance(
        beanName: String,
        beanType: Class<out ParameterBinderFactory>,
        builder: ParameterBinderManagerBuilderImpl
    ) {
        val instance = getBean(beanName, beanType)
        val binder = this.findAnnotationOnBean(beanName, Binder::class.java)

        resolveFactoryWithBinder(binder, beanName, instance, builder)
    }

    protected open fun ConfigurableListableBeanFactory.resolveConfigurer(
        beanName: String,
        beanType: Class<out ParameterBinderManagerBuilderConfigurer>,
        builder: ParameterBinderManagerBuilderImpl
    ) {
        val instance = getBean(beanName, beanType)
        instance.configure(builder)
    }

    @OptIn(ExperimentalSimbotAPI::class)
    protected open fun ConfigurableListableBeanFactory.resolveCandidate(
        beanName: String,
        beanType: Class<*>,
        builder: ParameterBinderManagerBuilderImpl
    ) {
        val binderMethods = beanType.selectMethodsSafely<Binder>()?.takeIf { it.isNotEmpty() } ?: return

        binderMethods.forEach { (method, binderAnnotation) ->
            val function = method.getKotlinFunctionSafely() ?: return@forEach
            val factory = function.toBinderFactory { getBean(beanName, beanType) }

            resolveFactoryWithBinder(binderAnnotation, beanName, factory, builder)
        }

    }

    protected open class ParameterBinderManagerBuilderImpl : ParameterBinderManagerBuilder {
        public open val globalBinders: MutableList<ParameterBinderFactory> = mutableListOf()
        public open val idBinders: MutableMap<String, ParameterBinderFactory> = mutableMapOf()

        override fun addBinderFactory(id: String, factory: ParameterBinderFactory) {
            if (idBinders.containsKey(id)) {
                throw DuplicateBinderIdException("Duplicate binder factory id: $id")
            }

            idBinders[id] = factory
        }

        override fun addBinderFactory(factory: ParameterBinderFactory) {
            globalBinders.add(factory)
        }
    }


    protected open fun resolveFactoryWithBinder(
        binder: Binder?,
        beanName: String,
        factory: ParameterBinderFactory,
        builder: ParameterBinderManagerBuilderImpl
    ) {
        if (binder == null) {
            builder.addBinderFactory(factory)
            return
        }

        when (binder.scope) {
            Binder.Scope.GLOBAL -> {
                builder.addBinderFactory(factory)
                return
            }

            Binder.Scope.SPECIFY -> {
                val binderId = binder.id.takeIf { it.isNotBlank() }
                require(binderId != null) { "Scope of @Binder on bean '$beanName' is ${Binder.Scope.SPECIFY}, but the required property 'id' is empty" }
                builder.addBinderFactory(binderId, factory)
            }

            Binder.Scope.DEFAULT -> {
                val binderId = binder.id.takeIf { it.isNotBlank() }
                if (binderId != null) {
                    builder.addBinderFactory(binderId, factory)
                } else {
                    builder.addBinderFactory(factory)
                }
            }
        }
    }


    public companion object {
        public const val BINDER_MANAGER_BEAN_NAME: String =
            "love.forte.simbot.spring.configuration.binder.resolvedBinderManager"

    }
}


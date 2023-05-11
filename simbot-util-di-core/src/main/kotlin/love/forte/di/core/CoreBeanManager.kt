/*
 * Copyright (c) 2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.di.core

import love.forte.di.Bean
import love.forte.di.BeanContainer
import love.forte.di.BeanManager
import love.forte.di.core.internal.CoreBeanManagerImpl
import kotlin.reflect.KClass


/**
 * 基础的BeanManager.
 *
 * [CoreBeanManager] 提供最基础的 [Bean] 管理。
 *
 * @author ForteScarlet
 */
public interface CoreBeanManager : BeanManager {
    override fun register(name: String, bean: Bean<*>)
    override fun getOrNull(name: String): Any?
    override fun <T : Any> getAll(type: KClass<T>?): List<String>
    override fun <T : Any> getOrNull(type: KClass<T>): T?
    override fun getTypeOrNull(name: String): KClass<*>?

    public companion object {
        @JvmStatic
        public fun newCoreBeanManager(
            parentContainer: BeanContainer,
            vararg processors: CoreBeanManagerBeanRegisterPostProcessor
        ): CoreBeanManager = newCoreBeanManager(parentContainer, processors.asList())


        @JvmStatic
        public fun newCoreBeanManager(
            parentContainer: BeanContainer,
            processors: List<CoreBeanManagerBeanRegisterPostProcessor>
        ): CoreBeanManager = CoreBeanManagerImpl(
            parentContainer, processors.toList(),
        )
    }
}


/**
 * [CoreBeanManager] 中，每次将要实际注入一个Bean之前都会调用的后置处理器。
 *
 * 会在bean验证名称之前进行处理。
 *
 */
public fun interface CoreBeanManagerBeanRegisterPostProcessor : Comparable<CoreBeanManagerBeanRegisterPostProcessor> {

    /**
     * 优先级.
     */
    public val priority: Int get() = 100

    /**
     * 得到即将被注册的[Bean], 并返回最终的处理结果。
     * 如果在某个流程中得到null，则终止本次处理。
     */
    public fun process(bean: Bean<Any>, beanManager: CoreBeanManager): Bean<*>?


    override fun compareTo(other: CoreBeanManagerBeanRegisterPostProcessor): Int {
        return priority.compareTo(other.priority)
    }
}


@DslMarker
@Retention(AnnotationRetention.BINARY)
internal annotation class CbmConfDsl


public class CoreBeanManagerConfiguration {
    @CbmConfDsl
    public var processors: MutableList<CoreBeanManagerBeanRegisterPostProcessor> = mutableListOf()

    @CbmConfDsl
    public var parentContainer: BeanContainer = BeanContainer

    @CbmConfDsl
    public fun plusProcessor(processor: CoreBeanManagerBeanRegisterPostProcessor): CoreBeanManagerConfiguration = also {
        processors.add(processor)
    }

    @CbmConfDsl
    public fun process(processor: CoreBeanManagerBeanRegisterPostProcessor) {
        plusProcessor(processor)
    }

    public fun build(): CoreBeanManager {
        return CoreBeanManagerImpl(
            parentContainer, processors
        )
    }


}


@CbmConfDsl
public inline fun coreBeanManager(config: CoreBeanManagerConfiguration.() -> Unit): CoreBeanManager {
    return CoreBeanManagerConfiguration().also(config).build()
}

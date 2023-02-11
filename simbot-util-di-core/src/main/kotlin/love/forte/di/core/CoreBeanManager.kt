/*
 * Copyright (c) 2023 ForteScarlet <ForteScarlet@163.com>
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

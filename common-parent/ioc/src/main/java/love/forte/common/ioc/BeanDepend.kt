/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     BeanDepend.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.common.ioc

import love.forte.simbot.common.constant.PriorityConstant


/**
 * 用于 [BeanDepend] 中的用于获取实例的函数。
 */
public fun interface EmptyInstanceSupplier<out T> : () -> T

/**
 * 通过一个bean实例和一个 [DependBeanFactory] 来对其进行注入。
 */
public fun interface InstanceInjector<T> : (T, DependBeanFactory) -> T


/**
 * Bean Depend, 存放于 [DependCenter] 中的Bean代表。
 * 提供一个简易的 [builder][BeanDependBuilder] 来构建一个实例。
 */
public interface BeanDepend<B> : Comparable<BeanDepend<*>> {

    /**
     * 此bean对应的实际数据类型
     */
    val type: Class<out B>

    /**
     * 它们都有自己对应的名称，并作为唯一保存依据。
     */
    val name: String


    /**
     * 是否应为单例
     */
    fun isSingle(): Boolean


    /**
     * 获取一个空实例的函数
     */
    val emptyInstanceSupplier: EmptyInstanceSupplier<B>


    /**
     * 实例注入函数
     */
    val instanceInjector: InstanceInjector<B>


    /** 优先级  */
    val priority: Int

    @JvmDefault
    override fun compareTo(other: BeanDepend<*>): Int = priority.compareTo(other.priority)

}


/**
 * [BeanDepend] 的实现类
 */
public data class BeanDependData<B>(
    override val type: Class<out B>,
    override val name: String,
    private val single: Boolean,
    override val emptyInstanceSupplier: EmptyInstanceSupplier<B>,
    override val instanceInjector: InstanceInjector<B>,
    override val priority: Int
) : BeanDepend<B> {
    /**
     * 是否应为单例
     */
    override fun isSingle(): Boolean = single
}


/**
 * builder. 必须要有 [type]、[name]、[emptyInstanceSupplier]、[instanceInjector]
 */
public class BeanDependBuilder<B> {

    private var type: Class<out B>? = null
    private var name: String? = null
    private var single: Boolean = false
    private var emptyInstanceSupplier: EmptyInstanceSupplier<B>? = null
    private var instanceInjector: InstanceInjector<B>? = null
    private var priority: Int = PriorityConstant.TENTH

    fun type(type: Class<out B>): BeanDependBuilder<B> {
        this.type = type
        return this
    }

    fun name(name: String): BeanDependBuilder<B> {
        this.name = name
        return this
    }

    fun single(single: Boolean): BeanDependBuilder<B> {
        this.single = single
        return this
    }

    fun emptyInstanceSupplier(emptyInstanceSupplier: EmptyInstanceSupplier<B>): BeanDependBuilder<B> {
        this.emptyInstanceSupplier = emptyInstanceSupplier
        return this
    }

    fun <INS : B> emptyInstanceSupplier(instance: INS): BeanDependBuilder<B> {
        this.emptyInstanceSupplier = EmptyInstanceSupplier { instance }
        return this
    }

    fun instanceInjector(instanceInjector: InstanceInjector<B>): BeanDependBuilder<B> {
        this.instanceInjector = instanceInjector
        return this
    }

    fun priority(priority: Int): BeanDependBuilder<B> {
        this.priority = priority
        return this
    }

    fun build(): BeanDepend<B> =
        BeanDependData(type!!, name!!, single, emptyInstanceSupplier!!, instanceInjector!!, priority)
}


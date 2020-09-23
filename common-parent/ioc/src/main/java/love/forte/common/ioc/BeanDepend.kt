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
public fun interface InstanceSupplier<out T> : (DependBeanFactory) -> T

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
     * 获取一个实例的函数
     */
    val instanceSupplier: InstanceSupplier<B>

    /**
     * 需要被初始化
     */
    val needInit: Boolean

    /**
     * 可以被Config注入
     */
    val asConfig: Boolean


    /** 优先级  */
    val priority: Int


    @JvmDefault
    override fun compareTo(other: BeanDepend<*>): Int = priority.compareTo(other.priority)

}


/**
 * [BeanDepend] 的实现类
 */
public class BeanDependData<B>(
    override val type: Class<out B>,
    override val name: String,
    private val single: Boolean,
    override val needInit: Boolean,
    override val asConfig: Boolean,
    override val instanceSupplier: InstanceSupplier<B>,
    override val priority: Int
) : BeanDepend<B> {
    override fun isSingle(): Boolean = single

    override fun toString(): String = "BeanDepend(type=$type, name=$name, single=$single, needInit=$needInit, instanceSupplier=$instanceSupplier, priority=$priority)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BeanDependData<*>

        if (type != other.type) return false
        if (name != other.name) return false
        if (single != other.single) return false
        if (priority != other.priority) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + single.hashCode()
        result = 31 * result + priority
        return result
    }

}

/**
 * builder. 必须要有 [type]、[name]、[instanceSupplier]、[instanceInjector]
 */
public open class BeanDependBuilder<B> {

    private var type: Class<out B>? = null
    private var name: String? = null
    private var single: Boolean = false
    private var needInit: Boolean = false
    private var asConfig: Boolean = false
    private var instanceSupplier: InstanceSupplier<B>? = null
    // private var instanceInjector: InstanceInjector<B> = InstanceInjector { b, _ -> b }
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

    fun needInit(needInit: Boolean): BeanDependBuilder<B> {
        this.needInit = needInit
        return this
    }

    fun asConfig(asConfig: Boolean): BeanDependBuilder<B> {
        this.asConfig = asConfig
        return this
    }

    fun instanceSupplier(instanceSupplier: InstanceSupplier<B>): BeanDependBuilder<B> {
        this.instanceSupplier = instanceSupplier
        return this
    }

    fun <INS : B> instanceSupplierByInstance(instance: INS): BeanDependBuilder<B> {
        this.instanceSupplier = InstanceSupplier { instance }
        return this
    }

    // fun instanceInjector(instanceInjector: InstanceInjector<B>): BeanDependBuilder<B> {
    //     this.instanceInjector = instanceInjector
    //     return this
    // }

    fun priority(priority: Int): BeanDependBuilder<B> {
        this.priority = priority
        return this
    }

    fun build(): BeanDepend<B> =
        BeanDependData(type!!, name!!, single, needInit, asConfig, instanceSupplier!!, priority)
}


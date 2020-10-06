/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     FilterManagerImpl.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.filter

import love.forte.simbot.core.annotation.Filters
import love.forte.simbot.core.api.message.MsgGet
import java.util.concurrent.ConcurrentHashMap


/**
 * 恒定返回false的 [AtDetection] 实例。
 */
internal val ConstantFalseAtDetection: AtDetection = AtDetection { false }


/**
 *
 * [过滤器管理器][FilterManager] 实现。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public class FilterManagerImpl : FilterManager {

    /**
     * 全部的自定义过滤器列表。
     */
    private val _filters: MutableMap<String, ListenerFilter> = ConcurrentHashMap()

    /**
     * 全部的 [AtDetectionFactory] 构建工厂。
     */
    private val atDetectionFactories: MutableList<AtDetectionFactory> = mutableListOf()


    /**
     * 获取所有的监听过滤器。
     * 获取的均为自定义过滤器。
     */
    override val filters: List<ListenerFilter>
        get() = _filters.values.toMutableList()

    /**
     * 根据一个名称获取一个对应的过滤器。
     */
    override fun getFilter(name: String): ListenerFilter? = _filters[name]


    /**
     * 通过注解构建一个 [过滤器][ListenerFilter]
     */
    override fun getFilter(filters: Filters): ListenerFilter {
        return AnnotationFiltersListenerFilterImpl(filters, this)
    }


    /**
     * 根据一个msg实例构建一个 [AtDetection] 函数。
     * 如果存在很多 [AtDetection] 实例，则会将他们构建为一个 [AtDetection]，并且会尝试寻找返回true的一个实例。
     */
    override fun getAtDetection(msg: MsgGet): AtDetection {
        return when(atDetectionFactories.size) {
            0 -> ConstantFalseAtDetection
            1 -> atDetectionFactories.first().getAtDetection(msg)
            else -> AtDetection {
                atDetectionFactories.any { factory ->
                    factory(msg).atBot()
                }
            }
        }
    }

    /**
     * 注册一个 [AtDetection] 构建函数。
     */
    override fun registryAtDetection(atDetectionFactory: AtDetectionFactory) {
        atDetectionFactories.add(atDetectionFactory)
    }

    /**
     * 注册一个 [过滤器][ListenerFilter] 实例。
     *
     * @throws FilterAlreadyExistsException 如果filter已经存在则可能抛出此异常。
     */
    override fun registerFilter(name: String, filter: ListenerFilter) {
        _filters.merge(name, filter) { oldValue, newValue ->
            throw FilterAlreadyExistsException("Duplicate custom filter name: $name, Conflicting filter：'$oldValue' VS '$newValue'")
        }
    }
}

/**
 * [AtDetectionFactory].invoke(MsgGet)。
 */
internal operator fun AtDetectionFactory.invoke(msg: MsgGet): AtDetection = this.getAtDetection(msg)
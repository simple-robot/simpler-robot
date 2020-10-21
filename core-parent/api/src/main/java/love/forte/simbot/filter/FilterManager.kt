/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     FilterManager.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.filter

import love.forte.simbot.annotation.Filters


/**
 * 过滤器管理中心。
 * 管理中心主要用于负责管理自定义过滤器以及构建注解过滤器。
 */
public interface FilterManager :
    AtDetectionFactory,
    AtDetectionRegistrar,
    ListenerFilterRegistrar,
    ListenerFilterAnnotationFactory {

    /**
     * 获取所有的监听过滤器。
     * 获取的均为自定义过滤器。
     */
    val filters: List<ListenerFilter>

    /**
     * 根据一个名称获取一个对应的过滤器。
     */
    fun getFilter(name: String): ListenerFilter?

}


/**
 * [FilterManager] 构建器，用于注册并构建一个filter。
 */
public interface FilterManagerBuilder {

    /**
     * 注册一个过滤器。
     */
    fun register(name: String, filter: ListenerFilter): FilterManagerBuilder

    /**
     * 构建一个manager
     */
    fun build(): FilterManager
}


/**
 * 注解过滤器工厂。
 */
public interface ListenerFilterAnnotationFactory {
    /**
     * 通过注解构建一个 [过滤器][ListenerFilter]
     */
    fun getFilter(filters: Filters) : ListenerFilter
}




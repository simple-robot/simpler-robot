/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     ListenerManagerBuilderImpl.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.listener

import love.forte.simbot.core.filter.AtDetectionFactory
import love.forte.simbot.core.filter.FilterManager
import love.forte.simbot.core.filter.ListenerFilter

/**
 *
 * 监听函数管理器的构建函数。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
class ListenerManagerBuilderImpl : ListenerManagerBuilder, ListenerRegistrar {

    /** 监听函数列表 */
    var listenerFunctions: MutableList<ListenerFunction> = mutableListOf()
    /**
     * 注册一个 [监听函数][ListenerFunction]。
     */
    override fun register(listenerFunction: ListenerFunction) {
        listenerFunctions.add(listenerFunction)
    }

    /**
     * at匹配器工厂。
     */
    lateinit var atDetectionFactory: AtDetectionFactory


    /**
     * 得到一个 [ListenerManager] 实例。
     */
    override fun build(): ListenerManager {
        TODO("Not yet implemented")
    }
}
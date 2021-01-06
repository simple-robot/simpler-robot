/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */

package love.forte.simbot.core.configuration

import love.forte.common.ioc.DependBeanFactory
import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.common.ioc.annotation.Depend
import love.forte.simbot.core.filter.CoreFilterManagerBuilder
import love.forte.simbot.core.filter.CoreFilterTargetManager
import love.forte.simbot.filter.*

/**
 *
 * filter 配置器。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
@ConfigBeans("coreFilterConfiguration")
public class CoreFilterConfiguration {

    /**
     * 依赖工厂，用于获取所有的异常处理器。
     */
    @Depend
    private lateinit var dependBeanFactory: DependBeanFactory

    /**
     * filter manager 构建器。
     */
    @CoreBeans("coreFilterManagerBuilder")
    fun coreFilterManagerBuilder(filterTargetManager: FilterTargetManager): FilterManagerBuilder = CoreFilterManagerBuilder(filterTargetManager)

    /**
     * [FilterTargetManager] 实例。
     */
    @CoreBeans("coreFilterTargetManager")
    fun coreFilterTargetManager(): FilterTargetManager = CoreFilterTargetManager().apply {
        // add all default checkers
        checkers.addAll(defaultProcessorChecker)
    }

    /**
     * filter manager. 通过构建器创建。
     *
     * @see FilterManager
     * @see AtDetectionFactory
     * @see AtDetectionRegistrar
     * @see ListenerFilterRegistrar
     * @see ListenerFilterAnnotationFactory
     */
    @CoreBeans("coreFilterManager")
    fun coreFilterManager(builder: FilterManagerBuilder): FilterManager {
        val allNames = dependBeanFactory.allBeans

        val filterType = ListenerFilter::class.java

        allNames.forEach { name ->
            val type = dependBeanFactory.getType(name)
            if(filterType.isAssignableFrom(type)) {
                // is filter, register it.
                builder.register(name, dependBeanFactory[name] as ListenerFilter)
            }
        }

        return builder.build()
    }

}
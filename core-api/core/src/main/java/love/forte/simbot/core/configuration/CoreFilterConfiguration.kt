/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
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

        allNames.forEach { name ->
            val type = dependBeanFactory.getType(name)
            if(ListenerFilter::class.java.isAssignableFrom(type)) {
                // is filter, register it.
                builder.register(name, dependBeanFactory[name] as ListenerFilter)
            }
            if (ListenerFilterRegistry::class.java.isAssignableFrom(type)) {
                (dependBeanFactory[name] as ListenerFilterRegistry).registerFilter(builder)
            }
        }

        return builder.build()
    }

}





/**
 * 用于注册代码生成的 [ListenerFilter] 实例的注册接口。实现此接口并注入到容器中，
 * 由 [CoreFilterConfiguration] 进行扫描与注册。
 *
 * 注意！不要与 [ListenerFilterRegistrar] 搞混，可以实现多个的是当前的 [ListenerFilterRegistry] 接口。
 *
 */
public interface ListenerFilterRegistry {

    /**
     * 可以进行过滤器注册。
     */
    fun registerFilter(builder: FilterManagerBuilder)
}
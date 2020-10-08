/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     CoreFilterConfiguration.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.configuration

import love.forte.common.ioc.DependBeanFactory
import love.forte.common.ioc.annotation.Beans
import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.common.ioc.annotation.Depend
import love.forte.common.utils.annotation.AnnotationUtil
import love.forte.simbot.core.filter.*

/**
 *
 * filter 配置器。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
@ConfigBeans
public class CoreFilterConfiguration {

    /**
     * 依赖工厂，用于获取所有的异常处理器。
     */
    @Depend
    private lateinit var dependBeanFactory: DependBeanFactory

    /**
     * filter manager 构建器。
     */
    @CoreBeans
    fun coreFilterManagerBuilder(): FilterManagerBuilder = CoreFilterManagerBuilder()


    /**
     * filter manager. 通过构建器创建。
     *
     * @see FilterManager
     * @see AtDetectionFactory
     * @see AtDetectionRegistrar
     * @see ListenerFilterRegistrar
     * @see ListenerFilterAnnotationFactory
     */
    @CoreBeans
    fun coreFilterManager(builder: FilterManagerBuilder): FilterManager {
        val allNames = dependBeanFactory.allBeans

        allNames.forEach {
            val type = dependBeanFactory.getType(it)
            if(type is ListenerFilter) {
                // is filter, register it.
                builder.register(it, type)
            }
        }

        return builder.build()
    }

}
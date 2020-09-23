/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     BeanDependInitializing.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.common.ioc


/**
 *
 * bean 依赖初始化的时候可以执行的生命周期。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public interface BeanDependInitializing {

    /**
     * 当被实例化了的时候。
     */
    fun afterCreate()

    /**
     * 当参数被设置了之后。
     */
    fun afterPropertiesInject()
}